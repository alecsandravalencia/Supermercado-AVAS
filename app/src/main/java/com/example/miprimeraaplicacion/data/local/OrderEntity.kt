package com.example.miprimeraaplicacion.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "orders")
data class OrderEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val createdAt: Long,

    val itemCount: Int,

    val subtotal: Int,

    val discount: Int,

    val shipping: Int,

    val total: Int,

    val couponCode: String? = null
)

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId")]
)
data class OrderItemEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val orderId: Int,

    val productName: String,

    val unitPrice: Int,

    val quantity: Int
)

data class OrderWithItems(
    @Embedded
    val order: OrderEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)
