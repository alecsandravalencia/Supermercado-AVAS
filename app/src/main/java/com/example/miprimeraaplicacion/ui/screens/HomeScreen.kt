package com.example.miprimeraaplicacion.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miprimeraaplicacion.data.local.CategoryEntity
import com.example.miprimeraaplicacion.data.local.CouponEntity
import com.example.miprimeraaplicacion.data.local.ProductWithCategory
import com.example.miprimeraaplicacion.ui.components.CategoryBadge
import com.example.miprimeraaplicacion.ui.components.ProductCard
import com.example.miprimeraaplicacion.util.DOMICILIO_GRATIS_DESDE
import com.example.miprimeraaplicacion.util.formatPrice

@Composable
fun HomeScreen(
    products: List<ProductWithCategory>,
    categories: List<CategoryEntity>,
    coupons: List<CouponEntity>,
    searchQuery: String,
    selectedCategoryId: Int?,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (Int?) -> Unit,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit
) {
    val activeCoupon = coupons.firstOrNull { it.isActive }

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
                label = { Text("Buscar productos") },
                leadingIcon = { Text("🔍") },
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
                    Text(text = "🚚", fontSize = 32.sp)

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Domicilio gratis",
                            style = MaterialTheme.typography.titleMedium
                        )

                        val couponText = if (activeCoupon != null) {
                            " Usa el cupón ${activeCoupon.code} y ahorra ${activeCoupon.discountPercent}%"
                        } else {
                            ""
                        }

                        Text(
                            text = "En compras desde ${formatPrice(DOMICILIO_GRATIS_DESDE)}.$couponText",
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
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories, key = { it.id }) { category ->
                    CategoryBadge(
                        category = category,
                        selected = selectedCategoryId == category.id,
                        onClick = {
                            onCategorySelect(
                                if (selectedCategoryId == category.id) null else category.id
                            )
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
                    modifier = Modifier.clickable { onCategorySelect(null) }
                )
            }
        }

        if (products.isEmpty()) {
            item {
                Text(
                    text = "No se encontraron productos",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            items(products, key = { it.product.id }) { item ->
                ProductCard(
                    item = item,
                    onToggleFavorite = { onToggleFavorite(item.product.id) },
                    onIncrease = { onIncrease(item.product.id) },
                    onDecrease = { onDecrease(item.product.id) }
                )
            }
        }
    }
}
