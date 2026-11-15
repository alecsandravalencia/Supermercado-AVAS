package com.example.miprimeraaplicacion.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// DAO: Data Access Object
// CRUD: Create (insert), Read (observeAll/findById), Update (update), Delete (delete)
@Dao
interface ProductDao {

    @Transaction
    @Query("SELECT * FROM products ORDER BY name")
    fun observeAll(): Flow<List<ProductWithCategory>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun findById(productId: Int): ProductEntity?

    @Query("SELECT * FROM products WHERE cartQuantity > 0")
    suspend fun getCartProducts(): List<ProductEntity>

    @Insert
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("UPDATE products SET cartQuantity = 0")
    suspend fun clearCart()
}
