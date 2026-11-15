package com.example.miprimeraaplicacion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        CouponEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SupermercadoDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun couponDao(): CouponDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: SupermercadoDatabase? = null

        fun getDatabase(context: Context): SupermercadoDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SupermercadoDatabase::class.java,
                    "supermercado_database"
                )
                    .addCallback(SeedCallback)
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}

// Datos iniciales: se insertan solo la primera vez que se crea la base de datos.
private object SeedCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        db.execSQL(
            "INSERT INTO categories (id, name, emoji) VALUES " +
                    "(1, 'Frutas', '🍉'), " +
                    "(2, 'Bebidas', '🥤'), " +
                    "(3, 'Lácteos', '🥛'), " +
                    "(4, 'Granos', '🍚'), " +
                    "(5, 'Panadería', '🍞'), " +
                    "(6, 'Despensa', '🥚')"
        )

        db.execSQL(
            "INSERT INTO products " +
                    "(name, categoryId, price, rating, onSale, imageName, isFavorite, cartQuantity) VALUES " +
                    "('Leche entera 1L', 3, 4200, 4.7, 1, 'leche', 0, 0), " +
                    "('Huevos x30', 6, 16500, 3.9, 0, 'huevos', 0, 0), " +
                    "('Arroz 500g', 4, 3100, 4.8, 1, 'arroz', 0, 0), " +
                    "('Pan tajado', 5, 6900, 4.4, 0, 'pantajado', 0, 0), " +
                    "('Manzana roja 1kg', 1, 7800, 4.6, 1, 'manzanas', 0, 0), " +
                    "('Banano 1kg', 1, 3500, 4.5, 0, 'bananos', 0, 0), " +
                    "('Frijol 500g', 4, 4350, 4.2, 0, 'frijoles', 0, 0), " +
                    "('Jugo de naranja 1L', 2, 6500, 4.4, 1, 'jugo', 0, 0)"
        )

        db.execSQL(
            "INSERT INTO coupons (code, discountPercent, isActive) VALUES ('AVAS10', 10, 1)"
        )
    }
}
