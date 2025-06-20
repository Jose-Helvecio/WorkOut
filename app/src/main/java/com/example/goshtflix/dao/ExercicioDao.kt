package com.example.goshtflix.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goshtflix.model.Exercicio
import kotlinx.coroutines.flow.Flow

@Dao
interface ExercicioDao {

    @Query("SELECT * FROM exercicios WHERE treinoId = :treinoId ORDER BY nome ASC")
    fun getExerciciosByTreinoId(treinoId: String): Flow<List<Exercicio>> // Observar mudanças

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercicio(exercicio: Exercicio)

    @Update
    suspend fun updateExercicio(exercicio: Exercicio)

    @Delete
    suspend fun deleteExercicio(exercicio: Exercicio)

    @Query("SELECT * FROM exercicios WHERE id = :exercicioId LIMIT 1")
    suspend fun getExercicioById(exercicioId: String): Exercicio?
}