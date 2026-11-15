package com.example.miprimeraaplicacion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.miprimeraaplicacion.data.local.CouponEntity
import com.example.miprimeraaplicacion.data.local.ProductWithCategory
import com.example.miprimeraaplicacion.ui.components.EmptyState
import com.example.miprimeraaplicacion.util.formatPrice

@Composable
fun ManageScreen(
    products: List<ProductWithCategory>,
    coupons: List<CouponEntity>,
    onNewProduct: () -> Unit,
    onEditProduct: (Int) -> Unit,
    onDeleteProduct: (Int) -> Unit,
    onAddCoupon: (String, Int, (Boolean) -> Unit) -> Unit,
    onToggleCoupon: (CouponEntity) -> Unit,
    onDeleteCoupon: (CouponEntity) -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Productos") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Cupones") }
            )
        }

        if (selectedTab == 0) {
            ManageProducts(
                products = products,
                onNewProduct = onNewProduct,
                onEditProduct = onEditProduct,
                onDeleteProduct = onDeleteProduct
            )
        } else {
            ManageCoupons(
                coupons = coupons,
                onAddCoupon = onAddCoupon,
                onToggleCoupon = onToggleCoupon,
                onDeleteCoupon = onDeleteCoupon
            )
        }
    }
}

@Composable
private fun ManageProducts(
    products: List<ProductWithCategory>,
    onNewProduct: () -> Unit,
    onEditProduct: (Int) -> Unit,
    onDeleteProduct: (Int) -> Unit
) {
    var productToDelete by rememberSaveable { mutableStateOf<Int?>(null) }

    val deleting = products.firstOrNull { it.product.id == productToDelete }

    if (deleting != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Seguro que quieres eliminar \"${deleting.product.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteProduct(deleting.product.id)
                        productToDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = onNewProduct,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("➕ Registrar producto")
            }
        }

        if (products.isEmpty()) {
            item {
                Text(
                    text = "No hay productos registrados",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        items(products, key = { it.product.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.product.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "${item.category.emoji} ${item.category.name}" +
                                if (item.product.onSale) " · OFERTA" else "",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = formatPrice(item.product.price),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onEditProduct(item.product.id) }) {
                            Text("✏️ Editar")
                        }

                        OutlinedButton(onClick = { productToDelete = item.product.id }) {
                            Text("🗑️ Eliminar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ManageCoupons(
    coupons: List<CouponEntity>,
    onAddCoupon: (String, Int, (Boolean) -> Unit) -> Unit,
    onToggleCoupon: (CouponEntity) -> Unit,
    onDeleteCoupon: (CouponEntity) -> Unit
) {
    var code by rememberSaveable { mutableStateOf("") }
    var percent by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                        text = "Nuevo cupón",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = code,
                        onValueChange = {
                            code = it
                            error = null
                        },
                        label = { Text("Código (ej: VERANO15)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = percent,
                        onValueChange = {
                            percent = it.filter { char -> char.isDigit() }
                            error = null
                        },
                        label = { Text("Descuento (%)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (error != null) {
                        Text(
                            text = error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            val value = percent.toIntOrNull()

                            when {
                                code.isBlank() -> error = "Escribe un código"
                                value == null || value !in 1..100 ->
                                    error = "El descuento debe estar entre 1 y 100"
                                else -> onAddCoupon(code, value) { created ->
                                    if (created) {
                                        code = ""
                                        percent = ""
                                    } else {
                                        error = "Ese código ya existe"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar cupón")
                    }
                }
            }
        }

        if (coupons.isEmpty()) {
            item { EmptyStateInline("No hay cupones registrados") }
        }

        items(coupons, key = { it.id }) { coupon ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = coupon.code,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "${coupon.discountPercent}% de descuento · " +
                                    if (coupon.isActive) "Activo" else "Inactivo",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Switch(
                        checked = coupon.isActive,
                        onCheckedChange = { onToggleCoupon(coupon) }
                    )

                    TextButton(onClick = { onDeleteCoupon(coupon) }) {
                        Text("🗑️")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateInline(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge
    )
}
