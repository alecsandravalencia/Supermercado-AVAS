package com.example.miprimeraaplicacion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.miprimeraaplicacion.data.local.CategoryEntity
import com.example.miprimeraaplicacion.data.local.ProductWithCategory
import com.example.miprimeraaplicacion.ui.components.CategoryBadge


@Composable
fun ProductFormScreen(
    existing: ProductWithCategory?,
    categories: List<CategoryEntity>,
    onSave: (
        name: String,
        categoryId: Int,
        price: Int,
        onSale: Boolean,
        emoji: String?
    ) -> Unit,
    onCancel: () -> Unit
) {
    val isEditing = existing != null

    var name by rememberSaveable { mutableStateOf(existing?.product?.name ?: "") }
    var priceText by rememberSaveable {
        mutableStateOf(existing?.product?.price?.toString() ?: "")
    }
    var categoryId by rememberSaveable { mutableStateOf(existing?.product?.categoryId) }
    var onSale by rememberSaveable { mutableStateOf(existing?.product?.onSale ?: false) }
    var emoji by rememberSaveable { mutableStateOf(existing?.product?.emoji ?: "") }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    val price = priceText.toIntOrNull()

    val nameError = name.isBlank()
    val priceError = price == null || price <= 0
    val categoryError = categoryId == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isEditing) "Editar producto ✏️" else "Nuevo producto ➕",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            singleLine = true,
            isError = showErrors && nameError,
            supportingText = {
                if (showErrors && nameError) Text("El nombre es obligatorio")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it.filter { char -> char.isDigit() } },
            label = { Text("Precio (COP)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showErrors && priceError,
            supportingText = {
                if (showErrors && priceError) Text("Escribe un precio mayor a 0")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Categoría",
            style = MaterialTheme.typography.titleMedium
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories, key = { it.id }) { category ->
                CategoryBadge(
                    category = category,
                    selected = categoryId == category.id,
                    onClick = { categoryId = category.id }
                )
            }
        }

        if (showErrors && categoryError) {
            Text(
                text = "Selecciona una categoría",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        OutlinedTextField(
            value = emoji,
            onValueChange = { emoji = it.take(8) },
            label = { Text("Emoji (opcional, ej: 🍫)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Producto en oferta",
                style = MaterialTheme.typography.bodyLarge
            )

            Switch(
                checked = onSale,
                onCheckedChange = { onSale = it }
            )
        }

        Button(
            onClick = {
                if (nameError || priceError || categoryError) {
                    showErrors = true
                } else {
                    onSave(name, categoryId!!, price!!, onSale, emoji)}
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Guardar cambios" else "Registrar producto")
        }

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}
