package com.example.miprimeraaplicacion.util

const val COSTO_DOMICILIO = 5000
const val DOMICILIO_GRATIS_DESDE = 50000

fun formatPrice(value: Int): String {
    return "\$" + value.toString().reversed().chunked(3).joinToString(".").reversed()
}

// Totales de una compra (se usan en el carrito y al guardar el pedido)
data class CartTotals(
    val subtotal: Int,
    val discount: Int,
    val shipping: Int
) {
    val total: Int get() = subtotal - discount + shipping
}

fun calculateTotals(subtotal: Int, discountPercent: Int): CartTotals {
    val discount = subtotal * discountPercent / 100
    val shipping = when {
        subtotal == 0 -> 0
        subtotal >= DOMICILIO_GRATIS_DESDE -> 0
        else -> COSTO_DOMICILIO
    }
    return CartTotals(subtotal, discount, shipping)
}
