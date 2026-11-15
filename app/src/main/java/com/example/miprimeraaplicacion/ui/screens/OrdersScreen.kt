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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miprimeraaplicacion.data.local.OrderWithItems
import com.example.miprimeraaplicacion.ui.components.EmptyState
import com.example.miprimeraaplicacion.ui.components.SummaryRow
import com.example.miprimeraaplicacion.util.formatPrice
import com.example.miprimeraaplicacion.viewmodel.OrderStats
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    orders: List<OrderWithItems>,
    stats: OrderStats
) {
    if (orders.isEmpty()) {
        EmptyState("Aún no has realizado pedidos 🧾\nCuando finalices una compra aparecerá aquí.")
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CO"))

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Mis pedidos 🧾",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // Estadísticas
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
                        text = "📊 Estadísticas de compras",
                        style = MaterialTheme.typography.titleMedium
                    )

                    SummaryRow("Pedidos realizados", stats.orderCount.toString())
                    SummaryRow("Total gastado", formatPrice(stats.totalSpent))
                    SummaryRow("Promedio por pedido", formatPrice(stats.averageOrder))
                    SummaryRow("Pedido más grande", formatPrice(stats.biggestOrder))
                }
            }
        }

        // Historial
        items(orders, key = { it.order.id }) { orderWithItems ->
            val order = orderWithItems.order

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pedido #${order.id}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = dateFormat.format(Date(order.createdAt)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    orderWithItems.items.forEach { item ->
                        Text(
                            text = "${item.quantity} x ${item.productName} — " +
                                    formatPrice(item.unitPrice * item.quantity),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (order.couponCode != null) {
                        Text(
                            text = "🏷️ Cupón ${order.couponCode}: -${formatPrice(order.discount)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(
                        text = "Total: ${formatPrice(order.total)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
