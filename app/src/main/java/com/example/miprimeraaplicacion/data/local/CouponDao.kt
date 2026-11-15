package com.example.miprimeraaplicacion.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponDao {

    @Query("SELECT * FROM coupons ORDER BY id DESC")
    fun observeAll(): Flow<List<CouponEntity>>

    @Query(
        "SELECT * FROM coupons " +
                "WHERE UPPER(code) = UPPER(:code) AND isActive = 1 LIMIT 1"
    )
    suspend fun findActiveByCode(code: String): CouponEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(coupon: CouponEntity): Long

    @Update
    suspend fun update(coupon: CouponEntity)

    @Delete
    suspend fun delete(coupon: CouponEntity)
}
