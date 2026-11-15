package com.example.miprimeraaplicacion.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "coupons",
    indices = [Index(value = ["code"], unique = true)]
)
data class CouponEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val code: String,

    val discountPercent: Int,

    val isActive: Boolean = true
)
