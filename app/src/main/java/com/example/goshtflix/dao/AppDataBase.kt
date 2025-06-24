package com.example.goshtflix.dao

import android.content.Context
import androidx.room.*
import com.example.goshtflix.model.Execucao
import com.example.goshtflix.model.Exercicio
import com.example.goshtflix.model.Treino

@Database(
    entities = [Treino::class, Exercicio::class, Execucao::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun treinoDao(): TreinoDao
    abstract fun exercicioDao(): ExercicioDao
    abstract fun execucaoDao(): ExecucaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "goshtflix_database"
                )
                    // se você adicionou a migração para versão 2, inclua aqui
                    // .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration() // força reset do banco se erro de migração
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
