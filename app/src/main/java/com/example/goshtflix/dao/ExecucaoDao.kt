package com.example.goshtflix.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goshtflix.model.Execucao
import kotlinx.coroutines.flow.Flow

@Dao
interface ExecucaoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(execucao: Execucao)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirVarias(execucoes: List<Execucao>)

    @Query("SELECT * FROM execucoes WHERE exercicioId = :exercicioId ORDER BY data DESC")
    fun getExecucoesPorExercicio(exercicioId: String): Flow<List<Execucao>>
}