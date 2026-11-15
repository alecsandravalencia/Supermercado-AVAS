package com.example.miprimeraaplicacion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miprimeraaplicacion.data.local.CouponEntity
import com.example.miprimeraaplicacion.data.local.ProductWithCategory
import com.example.miprimeraaplicacion.ui.components.CartItemRow
import com.example.miprimeraaplicacion.ui.components.EmptyState
import com.example.miprimeraaplicacion.ui.components.SummaryRow
import com.example.miprimeraaplicacion.util.CartTotals
import com.example.miprimeraaplicacion.util.DOMICILIO_GRATIS_DESDE
import com.example.miprimeraaplicacion.util.formatPrice

@Composable
fun CartScreen(
    cartProducts: List<ProductWithCategory>,
    totals: CartTotals,
    appliedCoupon: CouponEntity?,
    couponError: Boolean,
    lastOrderTotal: Int?,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onApplyCoupon: (String) -> Unit,
    onClearCouponError: () -> Unit,
    onCheckout: () -> Unit,
    onClearCart: () -> Unit,
    onDismissOrderDialog: () -> Unit
) {
    var couponInput by rememberSaveable { mutableStateOf("") }


    if (lastOrderTotal != null) {
        AlertDialog(
            onDismissRequest = onDismissOrderDialog,
            confirmButton = {
                TextButton(onClick = onDismissOrderDialog) {
                    Text("Aceptar")
                }
            },
            title = { Text("¡Pedido realizado! 🎉") },
            text = {
                Text(
                    "Pagaste ${formatPrice(lastOrderTotal)}. " +
                            "Lo puedes ver en la pestaña Pedidos. " +
                            "¡Gracias por comprar en Supermercado AVAS!"
                )
            }
        )
    }

    if (cartProducts.isEmpty()) {
        EmptyState("Tu carrito está vacío 🛒\nAgrega productos desde Inicio.")
        return
    }

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

        items(cartProducts, key = { it.product.id }) { item ->
            CartItemRow(
                item = item,
                onIncrease = { onIncrease(item.product.id) },
                onDecrease = { onDecrease(item.product.id) }
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
                                onClearCouponError()
                            },
                            label = { Text("Código") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = { onApplyCoupon(couponInput) },
                            enabled = couponInput.isNotBlank()
                        ) {
                            Text("Aplicar")
                        }
                    }

                    if (appliedCoupon != null) {
                        Text(
                            text = "✅ Cupón ${appliedCoupon.code} aplicado: " +
                                    "${appliedCoupon.discountPercent}% de descuento",
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

                    SummaryRow("Subtotal", formatPrice(totals.subtotal))

                    if (appliedCoupon != null) {
                        SummaryRow(
                            "Descuento (${appliedCoupon.discountPercent}%)",
                            "-${formatPrice(totals.discount)}"
                        )
                    }

                    SummaryRow(
                        "Domicilio 🚚",
                        if (totals.shipping == 0) "Gratis" else formatPrice(totals.shipping)
                    )

                    if (totals.shipping > 0) {
                        Text(
                            text = "Te faltan ${formatPrice(DOMICILIO_GRATIS_DESDE - totals.subtotal)} para el domicilio gratis",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    SummaryRow(
                        label = "Total",
                        value = formatPrice(totals.total),
                        highlight = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            couponInput = ""
                            onCheckout()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Finalizar compra")
                    }

                    OutlinedButton(
                        onClick = {
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
