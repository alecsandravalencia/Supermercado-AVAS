package com.example.miprimeraaplicacion.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("categoryId")]
)
data class ProductEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val categoryId: Int,

    val price: Int,

    val rating: Double = 4.0,

    val onSale: Boolean = false,

    // Nombre del drawable (ej: "leche"). Null si el producto no tiene imagen.
    val imageName: String? = null,

    // Emoji propio del producto (opcional). Si es null se usa el de su categoría.
    val emoji: String? = null,

    val isFavorite: Boolean = false,

    // Cantidad en el carrito (0 = no está en el carrito)
    val cartQuantity: Int = 0
)

// Producto junto con su categoría (consulta con relación)
data class ProductWithCategory(
    @Embedded
    val product: ProductEntity,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)
