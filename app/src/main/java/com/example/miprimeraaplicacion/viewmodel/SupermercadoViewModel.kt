package com.example.miprimeraaplicacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.miprimeraaplicacion.data.local.CouponEntity
import com.example.miprimeraaplicacion.data.local.ProductWithCategory
import com.example.miprimeraaplicacion.data.repository.SupermercadoRepository
import com.example.miprimeraaplicacion.util.CartTotals
import com.example.miprimeraaplicacion.util.calculateTotals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OrderStats(
    val orderCount: Int = 0,
    val totalSpent: Int = 0,
    val averageOrder: Int = 0,
    val biggestOrder: Int = 0
)

class SupermercadoViewModel(
    private val repository: SupermercadoRepository
) : ViewModel() {

    // ---------- DATOS DE ROOM ----------

    val products: StateFlow<List<ProductWithCategory>> = repository.products.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val categories = repository.categories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val coupons = repository.coupons.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val orders = repository.orders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // ---------- BÚSQUEDA Y FILTRO POR CATEGORÍA ----------

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId = _selectedCategoryId.asStateFlow()

    fun changeSearch(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
    }

    val filteredProducts = combine(
        products,
        _searchQuery,
        _selectedCategoryId
    ) { list, query, categoryId ->
        list.filter { item ->
            val matchesSearch = item.product.name.contains(query.trim(), ignoreCase = true)
            val matchesCategory = categoryId == null || item.product.categoryId == categoryId

            matchesSearch && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // ---------- FAVORITOS ----------

    val favoriteProducts = products.map { list ->
        list.filter { item -> item.product.isFavorite }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(productId)
        }
    }

    // ---------- CARRITO ----------

    val cartProducts = products.map { list ->
        list.filter { item -> item.product.cartQuantity > 0 }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val cartCount = products.map { list ->
        list.sumOf { item -> item.product.cartQuantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    fun increaseQuantity(productId: Int) {
        viewModelScope.launch {
            repository.increaseQuantity(productId)
        }
    }

    fun decreaseQuantity(productId: Int) {
        viewModelScope.launch {
            repository.decreaseQuantity(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
            resetCoupon()
        }
    }

    // ---------- CUPÓN APLICADO EN EL CARRITO ----------

    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon = _appliedCoupon.asStateFlow()

    private val _couponError = MutableStateFlow(false)
    val couponError = _couponError.asStateFlow()

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            val coupon = repository.findActiveCoupon(code)

            _appliedCoupon.value = coupon
            _couponError.value = coupon == null
        }
    }

    fun clearCouponError() {
        _couponError.value = false
    }

    private fun resetCoupon() {
        _appliedCoupon.value = null
        _couponError.value = false
    }

    val cartTotals: StateFlow<CartTotals> = combine(
        cartProducts,
        _appliedCoupon
    ) { cart, coupon ->
        val subtotal = cart.sumOf { item -> item.product.price * item.product.cartQuantity }

        calculateTotals(
            subtotal = subtotal,
            discountPercent = coupon?.discountPercent ?: 0
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartTotals(0, 0, 0)
    )

    // ---------- FINALIZAR COMPRA ----------

    private val _lastOrderTotal = MutableStateFlow<Int?>(null)
    val lastOrderTotal = _lastOrderTotal.asStateFlow()

    fun checkout() {
        viewModelScope.launch {
            val total = repository.checkout(_appliedCoupon.value)

            if (total != null) {
                _lastOrderTotal.value = total
                resetCoupon()
            }
        }
    }

    fun dismissOrderDialog() {
        _lastOrderTotal.value = null
    }

    // ---------- HISTORIAL Y ESTADÍSTICAS ----------

    val orderStats = orders.map { list ->
        if (list.isEmpty()) {
            OrderStats()
        } else {
            val totals = list.map { it.order.total }

            OrderStats(
                orderCount = list.size,
                totalSpent = totals.sum(),
                averageOrder = totals.sum() / list.size,
                biggestOrder = totals.max()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OrderStats()
    )

    // ---------- GESTIÓN DE PRODUCTOS ----------

    fun addProduct(
        name: String,
        categoryId: Int,
        price: Int,
        onSale: Boolean,
        emoji: String?
    ) {
        viewModelScope.launch {
            repository.addProduct(name, categoryId, price, onSale, emoji)
        }
    }

    fun updateProduct(
        productId: Int,
        name: String,
        categoryId: Int,
        price: Int,
        onSale: Boolean,
        emoji: String?
    ) {
        viewModelScope.launch {
            repository.updateProduct(productId, name, categoryId, price, onSale, emoji)
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    // ---------- GESTIÓN DE CUPONES ----------

    fun addCoupon(
        code: String,
        discountPercent: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            onResult(repository.addCoupon(code, discountPercent))
        }
    }

    fun toggleCoupon(coupon: CouponEntity) {
        viewModelScope.launch {
            repository.toggleCoupon(coupon)

            // Si se desactiva el cupón que estaba aplicado, se quita del carrito
            if (_appliedCoupon.value?.id == coupon.id) {
                resetCoupon()
            }
        }
    }

    fun deleteCoupon(coupon: CouponEntity) {
        viewModelScope.launch {
            repository.deleteCoupon(coupon)

            if (_appliedCoupon.value?.id == coupon.id) {
                resetCoupon()
            }
        }
    }
}

class SupermercadoViewModelFactory(
    private val repository: SupermercadoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SupermercadoViewModel::class.java))

        return SupermercadoViewModel(repository) as T
    }
}
