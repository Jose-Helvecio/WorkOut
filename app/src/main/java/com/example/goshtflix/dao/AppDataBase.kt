package com.example.goshtflix.dao

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.goshtflix.model.Execucao
import com.example.goshtflix.model.Exercicio
import com.example.goshtflix.model.Treino

@Database(entities = [Treino::class, Exercicio::class, Execucao::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun treinoDao(): TreinoDao
    abstract fun exercicioDao(): ExercicioDao
    abstract fun execucaoDao(): ExecucaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // MIGRATION de versão 1 para 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `execucoes` (
                        `id` TEXT NOT NULL,
                        `exercicioId` TEXT NOT NULL,
                        `serie` INTEGER NOT NULL,
                        `repeticoes` INTEGER NOT NULL,
                        `peso` REAL NOT NULL,
                        `data` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "goshtflix_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
