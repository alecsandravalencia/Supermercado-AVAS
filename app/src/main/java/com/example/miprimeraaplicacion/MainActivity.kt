package com.example.miprimeraaplicacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miprimeraaplicacion.data.local.SupermercadoDatabase
import com.example.miprimeraaplicacion.data.repository.SupermercadoRepository
import com.example.miprimeraaplicacion.ui.components.EmptyState
import com.example.miprimeraaplicacion.ui.screens.CartScreen
import com.example.miprimeraaplicacion.ui.screens.FavoritesScreen
import com.example.miprimeraaplicacion.ui.screens.HomeScreen
import com.example.miprimeraaplicacion.ui.screens.ManageScreen
import com.example.miprimeraaplicacion.ui.screens.OrdersScreen
import com.example.miprimeraaplicacion.ui.screens.ProductFormScreen
import com.example.miprimeraaplicacion.ui.theme.MiPrimeraAplicacionTheme
import com.example.miprimeraaplicacion.viewmodel.SupermercadoViewModel
import com.example.miprimeraaplicacion.viewmodel.SupermercadoViewModelFactory

object Routes {
    const val HOME = "inicio"
    const val FAVORITES = "favoritos"
    const val CART = "carrito"
    const val ORDERS = "pedidos"
    const val MANAGE = "gestion"
    const val PRODUCT_FORM = "producto_form/{productId}"


    fun productForm(productId: Int?): String {
        return "producto_form/${productId ?: -1}"
    }
}

private val topLevelRoutes = listOf(
    Routes.HOME,
    Routes.FAVORITES,
    Routes.CART,
    Routes.ORDERS,
    Routes.MANAGE
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Config ROOM
        val database = SupermercadoDatabase.getDatabase(applicationContext)
        val repository = SupermercadoRepository(database)
        val factory = SupermercadoViewModelFactory(repository)

        setContent {
            MiPrimeraAplicacionTheme {
                val viewModel: SupermercadoViewModel = viewModel(factory = factory)

                SuperMercadoApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperMercadoApp(viewModel: SupermercadoViewModel) {


    val products by viewModel.products.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val favoriteProducts by viewModel.favoriteProducts.collectAsStateWithLifecycle()
    val cartProducts by viewModel.cartProducts.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val coupons by viewModel.coupons.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val orderStats by viewModel.orderStats.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartCount.collectAsStateWithLifecycle()
    val cartTotals by viewModel.cartTotals.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
    val couponError by viewModel.couponError.collectAsStateWithLifecycle()
    val lastOrderTotal by viewModel.lastOrderTotal.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

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
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
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

            if (currentRoute in topLevelRoutes) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Routes.HOME,
                        onClick = { navigateTo(Routes.HOME) },
                        icon = { Text("🏠") },
                        label = { Text("Inicio") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.FAVORITES,
                        onClick = { navigateTo(Routes.FAVORITES) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (favoriteProducts.isNotEmpty()) {
                                        Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                                            Text("${favoriteProducts.size}")
                                        }
                                    }
                                }
                            ) {
                                Text("❤️")
                            }
                        },
                        label = { Text("Favoritos") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.CART,
                        onClick = { navigateTo(Routes.CART) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (cartCount > 0) {
                                        Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                                            Text("$cartCount")
                                        }
                                    }
                                }
                            ) {
                                Text("🛒")
                            }
                        },
                        label = { Text("Carrito") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.ORDERS,
                        onClick = { navigateTo(Routes.ORDERS) },
                        icon = { Text("🧾") },
                        label = { Text("Pedidos") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.MANAGE,
                        onClick = { navigateTo(Routes.MANAGE) },
                        icon = { Text("⚙️") },
                        label = { Text("Gestión") }
                    )
                }
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
                    products = filteredProducts,
                    categories = categories,
                    coupons = coupons,
                    searchQuery = searchQuery,
                    selectedCategoryId = selectedCategoryId,
                    onSearchChange = viewModel::changeSearch,
                    onCategorySelect = viewModel::selectCategory,
                    onIncrease = viewModel::increaseQuantity,
                    onDecrease = viewModel::decreaseQuantity,
                    onToggleFavorite = viewModel::toggleFavorite
                )
            }

            composable(Routes.FAVORITES) {
                FavoritesScreen(
                    favoriteProducts = favoriteProducts,
                    onIncrease = viewModel::increaseQuantity,
                    onDecrease = viewModel::decreaseQuantity,
                    onToggleFavorite = viewModel::toggleFavorite
                )
            }

            composable(Routes.CART) {
                CartScreen(
                    cartProducts = cartProducts,
                    totals = cartTotals,
                    appliedCoupon = appliedCoupon,
                    couponError = couponError,
                    lastOrderTotal = lastOrderTotal,
                    onIncrease = viewModel::increaseQuantity,
                    onDecrease = viewModel::decreaseQuantity,
                    onApplyCoupon = viewModel::applyCoupon,
                    onClearCouponError = viewModel::clearCouponError,
                    onCheckout = viewModel::checkout,
                    onClearCart = viewModel::clearCart,
                    onDismissOrderDialog = viewModel::dismissOrderDialog
                )
            }

            composable(Routes.ORDERS) {
                OrdersScreen(
                    orders = orders,
                    stats = orderStats
                )
            }

            composable(Routes.MANAGE) {
                ManageScreen(
                    products = products,
                    coupons = coupons,
                    onNewProduct = {
                        navController.navigate(Routes.productForm(null))
                    },
                    onEditProduct = { productId ->
                        navController.navigate(Routes.productForm(productId))
                    },
                    onDeleteProduct = viewModel::deleteProduct,
                    onAddCoupon = viewModel::addCoupon,
                    onToggleCoupon = viewModel::toggleCoupon,
                    onDeleteCoupon = viewModel::deleteCoupon
                )
            }

            composable(
                route = Routes.PRODUCT_FORM,
                arguments = listOf(
                    navArgument("productId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: -1
                val existing = products.firstOrNull { it.product.id == productId }

                if (productId != -1 && existing == null) {
                    EmptyState("Cargando producto…")
                } else {
                    ProductFormScreen(
                        existing = existing,
                        categories = categories,
                        onSave = { name, categoryId, price, onSale, emoji ->
                            if (productId == -1) {
                                viewModel.addProduct(name, categoryId, price, onSale, emoji)
                            } else {
                                viewModel.updateProduct(
                                    productId, name, categoryId, price, onSale, emoji
                                )
                            }

                            navController.popBackStack()
                        },
                        onCancel = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
