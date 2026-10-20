package com.example.miprimeraaplicacion

import android.os.Bundle

import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.miprimeraaplicacion.ui.theme.MiPrimeraAplicacionTheme

data class Product(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: Int,
    val rating: Double,
    val enOferta: Boolean = false,
    val emoji: String = "🛒",
    @DrawableRes val imagen: Int? = null
)

data class Category(
    val emoji: String,
    val name: String
)

object Routes {
    const val HOME = "inicio"
    const val FAVORITES = "favoritos"
    const val CART = "carrito"
}

const val CUPON_VALIDO = "AVAS10"
const val DESCUENTO_CUPON = 10
const val COSTO_DOMICILIO = 5000
const val DOMICILIO_GRATIS_DESDE = 50000

fun formatPrice(value: Int): String {
    return "\$" + value.toString().reversed().chunked(3).joinToString(".").reversed()
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MiPrimeraAplicacionTheme {

                SuperMercadoApp()

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperMercadoApp() {

    var searchQuery by remember {
        mutableStateOf("")
    }

    var cartQuantities by remember {
        mutableStateOf<Map<Int, Int>>(emptyMap())
    }

    var favorites by remember {
        mutableStateOf<Set<Int>>(emptySet())
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val products = listOf(
        Product(
            id = 1,
            nombre = "Leche entera 1L",
            categoria = "Lácteos",
            precio = 4200,
            rating = 4.7,
            enOferta = true,
            imagen = R.drawable.leche
        ),
        Product(
            id = 2,
            nombre = "Huevos x30",
            categoria = "Despensa",
            precio = 16500,
            rating = 3.9,
            imagen = R.drawable.huevos
        ),
        Product(
            id = 3,
            nombre = "Arroz 500g",
            categoria = "Granos",
            precio = 3100,
            rating = 4.8,
            enOferta = true,
            imagen = R.drawable.arroz
        ),
        Product(
            id = 4,
            nombre = "Pan tajado",
            categoria = "Panadería",
            precio = 6900,
            rating = 4.4,
            imagen = R.drawable.pantajado
        ),
        Product(
            id = 5,
            nombre = "Manzana roja 1kg",
            categoria = "Frutas",
            precio = 7800,
            rating = 4.6,
            enOferta = true,
            imagen = R.drawable.manzanas
        ),
        Product(
            id = 6,
            nombre = "Banano 1kg",
            categoria = "Frutas",
            precio = 3500,
            rating = 4.5,
            imagen = R.drawable.bananos
        ),
        Product(
            id = 7,
            nombre = "Frijol 500g",
            categoria = "Granos",
            precio = 4350,
            rating = 4.2,
            imagen = R.drawable.frijoles
        ),
        Product(
            id = 8,
            nombre = "Jugo de naranja 1L",
            categoria = "Bebidas",
            precio = 6500,
            rating = 4.4,
            enOferta = true,
            imagen = R.drawable.jugo
        )
    )

    val categories = listOf(
        Category("🍉", "Frutas"),
        Category("🥤", "Bebidas"),
        Category("🥛", "Lácteos"),
        Category("🍚", "Granos"),
        Category("🍞", "Panadería"),
        Category("🥚", "Despensa")
    )

    fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(
                navController.graph.findStartDestination().id
            ) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun increaseQuantity(productId: Int) {
        val currentlyQuantity = cartQuantities[productId] ?: 0

        cartQuantities = cartQuantities + (
                productId to (currentlyQuantity + 1)
                )
    }

    fun decreaseQuantity(productId: Int) {
        val currentlyQuantity = cartQuantities[productId] ?: 0

        if (currentlyQuantity > 1) {
            cartQuantities = cartQuantities + (
                    productId to (currentlyQuantity - 1)
                    )
        } else {
            cartQuantities = cartQuantities - productId
        }
    }

    fun toggleFavorite(productId: Int) {
        favorites = if (productId in favorites) {
            favorites - productId
        } else {
            favorites + productId
        }
    }

    fun clearCart() {
        cartQuantities = emptyMap()
    }

    val cartCount = cartQuantities.values.sum()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🛒 Supermercado AVAS",
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                actions = {
                    Card(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { navigateTo(Routes.CART) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(
                            text = "🛒 $cartCount",
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            ),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                NavigationBarItem(
                    selected = currentRoute == Routes.HOME,
                    onClick = {
                        navigateTo(Routes.HOME)
                    },
                    icon = {
                        Text("🏠")
                    },
                    label = {
                        Text("Inicio")
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == Routes.FAVORITES,
                    onClick = {
                        navigateTo(Routes.FAVORITES)
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (favorites.isNotEmpty()) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    ) {
                                        Text("${favorites.size}")
                                    }
                                }
                            }
                        ) {
                            Text("❤️")
                        }
                    },
                    label = {
                        Text("Favoritos")
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == Routes.CART,
                    onClick = {
                        navigateTo(Routes.CART)
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    ) {
                                        Text("$cartCount")
                                    }
                                }
                            }
                        ) {
                            Text("🛒")
                        }
                    },
                    label = {
                        Text("Carrito")
                    }
                )
            }
        }

    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    searchQuery = searchQuery,
                    onSearchChange = { newText ->
                        searchQuery = newText
                    },
                    categories = categories,
                    products = products,
                    cartQuantities = cartQuantities,
                    favorites = favorites,
                    onIncrease = { productId ->
                        increaseQuantity(productId)
                    },
                    onDecrease = { productId ->
                        decreaseQuantity(productId)
                    },
                    onToggleFavorite = { productId ->
                        toggleFavorite(productId)
                    }
                )
            }

            composable(Routes.FAVORITES) {
                FavoritesScreen(
                    products = products,
                    cartQuantities = cartQuantities,
                    favorites = favorites,
                    onIncrease = { productId ->
                        increaseQuantity(productId)
                    },
                    onDecrease = { productId ->
                        decreaseQuantity(productId)
                    },
                    onToggleFavorite = { productId ->
                        toggleFavorite(productId)
                    }
                )
            }

            composable(Routes.CART) {
                CartScreen(
                    products = products,
                    cartQuantities = cartQuantities,
                    onIncrease = { productId ->
                        increaseQuantity(productId)
                    },
                    onDecrease = { productId ->
                        decreaseQuantity(productId)
                    },
                    onClearCart = {
                        clearCart()
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    categories: List<Category>,
    products: List<Product>,
    cartQuantities: Map<Int, Int>,
    favorites: Set<Int>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit
) {
    var selectedCategory by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val filteredProducts = products.filter { product ->
        val matchesSearch = product.nombre.contains(
            searchQuery,
            ignoreCase = true
        )
        val matchesCategory = selectedCategory == null ||
                product.categoria == selectedCategory

        matchesSearch && matchesCategory
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Column {
                Text(
                    text = "¡Bienvenid@! 👋🏼",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "¿Qué deseas comprar?",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = {
                    Text("Buscar productos")
                },
                leadingIcon = {
                    Text("🔍")
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🚚",
                        fontSize = 32.sp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Domicilio gratis",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "En compras desde ${formatPrice(DOMICILIO_GRATIS_DESDE)}. " +
                                    "Usa el cupón $CUPON_VALIDO y ahorra $DESCUENTO_CUPON%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Categorías",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryBadge(
                        category = category,
                        selected = selectedCategory == category.name,
                        onClick = {
                            selectedCategory =
                                if (selectedCategory == category.name) null
                                else category.name
                        }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Productos",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Ver todos →",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable {
                        selectedCategory = null
                    }
                )
            }
        }

        if (filteredProducts.isEmpty()) {
            item {
                Text(
                    text = "No se encontraron productos",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            items(filteredProducts, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    quantity = cartQuantities[product.id] ?: 0,
                    isFavorite = product.id in favorites,
                    onToggleFavorite = {
                        onToggleFavorite(product.id)
                    },
                    onIncrease = {
                        onIncrease(product.id)
                    },
                    onDecrease = {
                        onDecrease(product.id)
                    }
                )
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    products: List<Product>,
    cartQuantities: Map<Int, Int>,
    favorites: Set<Int>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit
) {
    val favoriteProducts = products.filter { it.id in favorites }

    if (favoriteProducts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aún no tienes favoritos 🤍\nToca el corazón de un producto para guardarlo aquí.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Mis favoritos ❤️",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            items(favoriteProducts, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    quantity = cartQuantities[product.id] ?: 0,
                    isFavorite = true,
                    onToggleFavorite = {
                        onToggleFavorite(product.id)
                    },
                    onIncrease = {
                        onIncrease(product.id)
                    },
                    onDecrease = {
                        onDecrease(product.id)
                    }
                )
            }
        }
    }
}

@Composable
fun CartScreen(
    products: List<Product>,
    cartQuantities: Map<Int, Int>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onClearCart: () -> Unit
) {
    var couponInput by rememberSaveable {
        mutableStateOf("")
    }
    var couponApplied by rememberSaveable {
        mutableStateOf(false)
    }
    var couponError by rememberSaveable {
        mutableStateOf(false)
    }
    var showOrderDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var lastOrderTotal by rememberSaveable {
        mutableStateOf(0)
    }

    val cartProducts = products.filter { (cartQuantities[it.id] ?: 0) > 0 }

    val subtotal = cartProducts.sumOf { it.precio * (cartQuantities[it.id] ?: 0) }
    val discount = if (couponApplied) subtotal * DESCUENTO_CUPON / 100 else 0
    val shipping = when {
        subtotal == 0 -> 0
        subtotal >= DOMICILIO_GRATIS_DESDE -> 0
        else -> COSTO_DOMICILIO
    }
    val total = subtotal - discount + shipping

    if (showOrderDialog) {
        AlertDialog(
            onDismissRequest = {
                showOrderDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showOrderDialog = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            title = {
                Text("¡Pedido realizado! 🎉")
            },
            text = {
                Text("Pagaste ${formatPrice(lastOrderTotal)}. ¡Gracias por comprar en Supermercado AVAS!")
            }
        )
    }

    if (cartProducts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tu carrito está vacío 🛒\nAgrega productos desde Inicio.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Mi carrito 🛒",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            items(cartProducts, key = { it.id }) { product ->
                CartItemRow(
                    product = product,
                    quantity = cartQuantities[product.id] ?: 0,
                    onIncrease = {
                        onIncrease(product.id)
                    },
                    onDecrease = {
                        onDecrease(product.id)
                    }
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🏷️ ¿Tienes un cupón?",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = {
                                    couponInput = it
                                    couponError = false
                                },
                                label = {
                                    Text("Código")
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    if (couponInput.trim().uppercase() == CUPON_VALIDO) {
                                        couponApplied = true
                                        couponError = false
                                    } else {
                                        couponApplied = false
                                        couponError = true
                                    }
                                }
                            ) {
                                Text("Aplicar")
                            }
                        }

                        if (couponApplied) {
                            Text(
                                text = "✅ Cupón aplicado: $DESCUENTO_CUPON% de descuento",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (couponError) {
                            Text(
                                text = "❌ Cupón no válido",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Resumen del pedido",
                            style = MaterialTheme.typography.titleLarge
                        )

                        SummaryRow("Subtotal", formatPrice(subtotal))

                        if (couponApplied) {
                            SummaryRow(
                                "Descuento ($DESCUENTO_CUPON%)",
                                "-${formatPrice(discount)}"
                            )
                        }

                        SummaryRow(
                            "Domicilio 🚚",
                            if (shipping == 0) "Gratis" else formatPrice(shipping)
                        )

                        if (shipping > 0) {
                            Text(
                                text = "Te faltan ${formatPrice(DOMICILIO_GRATIS_DESDE - subtotal)} para el domicilio gratis",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        SummaryRow(
                            label = "Total",
                            value = formatPrice(total),
                            highlight = true
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                lastOrderTotal = total
                                showOrderDialog = true
                                couponApplied = false
                                couponInput = ""
                                onClearCart()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Finalizar compra")
                        }

                        OutlinedButton(
                            onClick = {
                                couponApplied = false
                                couponInput = ""
                                onClearCart()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Vaciar carrito")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductImage(
    product: Product,
    modifier: Modifier = Modifier
) {
    if (product.imagen != null) {
        Image(
            painter = painterResource(id = product.imagen),
            contentDescription = product.nombre,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = modifier.background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = product.emoji,
                fontSize = 44.sp
            )
        }
    }
}

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = if (isFavorite) "❤️" else "🤍",
        fontSize = 24.sp,
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onDecrease,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Text("-")
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedButton(
            onClick = onIncrease,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Text("+")
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (highlight) MaterialTheme.typography.titleLarge
            else MaterialTheme.typography.bodyLarge
        )

        Text(
            text = value,
            style = if (highlight) MaterialTheme.typography.titleLarge
            else MaterialTheme.typography.titleMedium,
            color = if (highlight) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    quantity: Int,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.size(110.dp)
            ) {
                ProductImage(
                    product = product,
                    modifier = Modifier.fillMaxSize()
                )

                if (product.enOferta) {
                    Card(
                        modifier = Modifier.align(Alignment.TopStart),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(
                            text = "OFERTA",
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 2.dp
                            ),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    FavoriteButton(
                        isFavorite = isFavorite,
                        onClick = onToggleFavorite
                    )
                }

                Text(
                    text = product.categoria,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "⭐ ${product.rating}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = formatPrice(product.precio),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                if (quantity == 0) {
                    Button(
                        onClick = onIncrease
                    ) {
                        Text("🛒 Agregar")
                    }
                } else {
                    QuantitySelector(
                        quantity = quantity,
                        onIncrease = onIncrease,
                        onDecrease = onDecrease
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    product: Product,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductImage(
                product = product,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${formatPrice(product.precio)} c/u",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = formatPrice(product.precio * quantity),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                QuantitySelector(
                    quantity = quantity,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease
                )
            }
        }
    }
}

@Composable
fun CategoryBadge(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primaryContainer
            },
            contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onPrimaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.emoji
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = category.name,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuperMercadoAppPreview() {
    MiPrimeraAplicacionTheme {
        SuperMercadoApp()
    }
}