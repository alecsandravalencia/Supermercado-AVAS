package com.example.miprimeraaplicacion.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miprimeraaplicacion.data.local.CategoryEntity
import com.example.miprimeraaplicacion.data.local.ProductWithCategory
import com.example.miprimeraaplicacion.util.formatPrice

@Composable
fun EmptyState(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProductImage(
    item: ProductWithCategory,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageName = item.product.imageName


    val resId = remember(imageName) {
        if (imageName == null) 0
        else context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }

    if (resId != 0) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = item.product.name,
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
                text = item.product.emoji ?: item.category.emoji,
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
fun CategoryBadge(
    category: CategoryEntity,
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
            Text(text = category.emoji)

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = category.name,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun ProductCard(
    item: ProductWithCategory,
    onToggleFavorite: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    val product = item.product

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
            Box(modifier = Modifier.size(110.dp)) {
                ProductImage(
                    item = item,
                    modifier = Modifier.fillMaxSize()
                )

                if (product.onSale) {
                    Card(
                        modifier = Modifier.align(Alignment.TopStart),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(
                            text = "OFERTA",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    FavoriteButton(
                        isFavorite = product.isFavorite,
                        onClick = onToggleFavorite
                    )
                }

                Text(
                    text = item.category.name,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "⭐ ${product.rating}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = formatPrice(product.price),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (product.cartQuantity == 0) {
                    Button(onClick = onIncrease) {
                        Text("🛒 Agregar")
                    }
                } else {
                    QuantitySelector(
                        quantity = product.cartQuantity,
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
    item: ProductWithCategory,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    val product = item.product

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
                item = item,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${formatPrice(product.price)} c/u",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = formatPrice(product.price * product.cartQuantity),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                QuantitySelector(
                    quantity = product.cartQuantity,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease
                )
            }
        }
    }
}
