package com.example.miprimeraaplicacion.data.repository

import androidx.room.withTransaction
import com.example.miprimeraaplicacion.data.local.CategoryEntity
import com.example.miprimeraaplicacion.data.local.CouponEntity
import com.example.miprimeraaplicacion.data.local.OrderEntity
import com.example.miprimeraaplicacion.data.local.OrderItemEntity
import com.example.miprimeraaplicacion.data.local.OrderWithItems
import com.example.miprimeraaplicacion.data.local.ProductEntity
import com.example.miprimeraaplicacion.data.local.ProductWithCategory
import com.example.miprimeraaplicacion.data.local.SupermercadoDatabase
import com.example.miprimeraaplicacion.util.calculateTotals
import kotlinx.coroutines.flow.Flow

class SupermercadoRepository(
    private val database: SupermercadoDatabase
) {

    private val productDao = database.productDao()
    private val categoryDao = database.categoryDao()
    private val couponDao = database.couponDao()
    private val orderDao = database.orderDao()

    // ---------- LECTURA (Flow: la UI se actualiza sola) ----------

    val products: Flow<List<ProductWithCategory>> = productDao.observeAll()

    val categories: Flow<List<CategoryEntity>> = categoryDao.observeAll()

    val coupons: Flow<List<CouponEntity>> = couponDao.observeAll()

    val orders: Flow<List<OrderWithItems>> = orderDao.observeAll()

    // ---------- PRODUCTOS (CRUD) ----------

    suspend fun addProduct(
        name: String,
        categoryId: Int,
        price: Int,
        onSale: Boolean,
        emoji: String?
    ) {
        val cleanName = name.trim()

        if (cleanName.isBlank() || price <= 0) return

        productDao.insert(
            ProductEntity(
                name = cleanName,
                categoryId = categoryId,
                price = price,
                onSale = onSale,
                emoji = emoji?.trim()?.ifBlank { null }
            )
        )
    }

    suspend fun updateProduct(
        productId: Int,
        name: String,
        categoryId: Int,
        price: Int,
        onSale: Boolean,
        emoji: String?
    ) {
        val entity = productDao.findById(productId) ?: return

        val cleanName = name.trim()

        if (cleanName.isBlank() || price <= 0) return

        productDao.update(
            entity.copy(
                name = cleanName,
                categoryId = categoryId,
                price = price,
                onSale = onSale,
                emoji = emoji?.trim()?.ifBlank { null }
            )
        )
    }

    suspend fun deleteProduct(productId: Int) {
        val entity = productDao.findById(productId) ?: return

        productDao.delete(entity)
    }

    suspend fun findProduct(productId: Int): ProductEntity? {
        return productDao.findById(productId)
    }

    // ---------- FAVORITOS ----------

    suspend fun toggleFavorite(productId: Int) {
        val entity = productDao.findById(productId) ?: return

        productDao.update(
            entity.copy(isFavorite = !entity.isFavorite)
        )
    }

    // ---------- CARRITO ----------

    suspend fun increaseQuantity(productId: Int) {
        val entity = productDao.findById(productId) ?: return

        productDao.update(
            entity.copy(cartQuantity = entity.cartQuantity + 1)
        )
    }

    suspend fun decreaseQuantity(productId: Int) {
        val entity = productDao.findById(productId) ?: return

        productDao.update(
            entity.copy(
                cartQuantity = (entity.cartQuantity - 1).coerceAtLeast(0)
            )
        )
    }

    suspend fun clearCart() {
        productDao.clearCart()
    }

    // ---------- CUPONES ----------

    suspend fun findActiveCoupon(code: String): CouponEntity? {
        val cleanCode = code.trim()

        if (cleanCode.isBlank()) return null

        return couponDao.findActiveByCode(cleanCode)
    }


    suspend fun addCoupon(code: String, discountPercent: Int): Boolean {
        val cleanCode = code.trim().uppercase()

        if (cleanCode.isBlank() || discountPercent !in 1..100) return false

        val id = couponDao.insert(
            CouponEntity(
                code = cleanCode,
                discountPercent = discountPercent
            )
        )

        return id != -1L
    }

    suspend fun toggleCoupon(coupon: CouponEntity) {
        couponDao.update(
            coupon.copy(isActive = !coupon.isActive)
        )
    }

    suspend fun deleteCoupon(coupon: CouponEntity) {
        couponDao.delete(coupon)
    }

    // ---------- PEDIDOS ----------
    suspend fun checkout(coupon: CouponEntity?): Int? {
        return database.withTransaction {
            val cart = productDao.getCartProducts()

            if (cart.isEmpty()) return@withTransaction null

            val subtotal = cart.sumOf { it.price * it.cartQuantity }
            val totals = calculateTotals(
                subtotal = subtotal,
                discountPercent = coupon?.discountPercent ?: 0
            )

            val orderId = orderDao.insertOrder(
                OrderEntity(
                    createdAt = System.currentTimeMillis(),
                    itemCount = cart.sumOf { it.cartQuantity },
                    subtotal = totals.subtotal,
                    discount = totals.discount,
                    shipping = totals.shipping,
                    total = totals.total,
                    couponCode = coupon?.code
                )
            ).toInt()

            orderDao.insertItems(
                cart.map {
                    OrderItemEntity(
                        orderId = orderId,
                        productName = it.name,
                        unitPrice = it.price,
                        quantity = it.cartQuantity
                    )
                }
            )

            productDao.clearCart()

            totals.total
        }
    }
}
