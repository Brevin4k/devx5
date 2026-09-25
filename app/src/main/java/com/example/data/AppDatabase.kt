package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [OrderEntity::class, MaterialEntity::class, SettingsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "printcalc_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.appDao())
                }
            }
        }

        suspend fun populateInitialData(dao: AppDao) {
            // Default printer settings
            dao.saveSettings(SettingsEntity())

            // Default materials catalog
            val defaultMaterials = listOf(
                MaterialEntity(name = "PLA Padrão", type = "PLA", pricePerKg = 95.0, brand = "Geral", color = "Preto/Branco", isDefault = true),
                MaterialEntity(name = "PETG Resistente", type = "PETG", pricePerKg = 115.0, brand = "Premium", color = "Preto", isDefault = false),
                MaterialEntity(name = "ABS Técnico", type = "ABS", pricePerKg = 89.0, brand = "Standard", color = "Cinza", isDefault = false),
                MaterialEntity(name = "TPU Flexível", type = "TPU", pricePerKg = 160.0, brand = "Flex", color = "Transparente", isDefault = false),
                MaterialEntity(name = "Resina UV Standard (SLA)", type = "RESINA", pricePerKg = 175.0, brand = "Anycubic/Elegoo", color = "Cinza", isDefault = false),
                MaterialEntity(name = "Nylon Carbon Fiber (CF)", type = "NYLON", pricePerKg = 280.0, brand = "Engenharia", color = "Preto", isDefault = false)
            )
            dao.insertMaterials(defaultMaterials)

            // Example customer order (similar to user prompt: "Lukinha, 10 peças")
            val sampleOrder = OrderEntity(
                clientName = "Lukinha",
                projectName = "Suporte Articulado",
                quantity = 10,
                materialName = "PETG Resistente",
                filamentGrams = 350.0,
                filamentCostPerKg = 115.0,
                printHours = 12.5,
                printerWatts = 150.0,
                energyCostPerKwh = 0.85,
                extraCosts = 15.0,
                extraCostsDescription = "Parafusos e insertos M3",
                profitPercentage = 100.0,
                totalProductionCost = 57.84,
                totalProfit = 57.84,
                totalFinalPrice = 115.68,
                paymentMethod = "Parcelado em 2x",
                installments = 2,
                installmentValue = 57.84,
                status = "EM_IMPRESSAO",
                notes = "Cliente pediu acabamento reforçado com 4 perímetros",
                createdAt = System.currentTimeMillis() - 86400000L
            )
            dao.insertOrder(sampleOrder)
        }
    }
}
