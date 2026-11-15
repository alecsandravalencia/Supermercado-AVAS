package com.example.miprimeraaplicacion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miprimeraaplicacion.data.local.ProductWithCategory
import com.example.miprimeraaplicacion.ui.components.EmptyState
import com.example.miprimeraaplicacion.ui.components.ProductCard

@Composable
fun FavoritesScreen(
    favoriteProducts: List<ProductWithCategory>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit
) {
    if (favoriteProducts.isEmpty()) {
        EmptyState("Aún no tienes favoritos 🤍\nToca el corazón de un producto para guardarlo aquí.")
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

            items(favoriteProducts, key = { it.product.id }) { item ->
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
