package com.example.goshtflix.dao


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.goshtflix.model.Exercicio
import com.example.goshtflix.model.Treino

@Database(entities = [Treino::class, Exercicio::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun treinoDao(): TreinoDao
    abstract fun exercicioDao(): ExercicioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "goshtflix_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}