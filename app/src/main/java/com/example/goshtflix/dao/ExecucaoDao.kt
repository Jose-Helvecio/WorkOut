package com.example.goshtflix.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goshtflix.model.Execucao

@Dao
interface ExecucaoDao {

    @Query("SELECT * FROM execucoes WHERE exercicioId = :exercicioId ORDER BY data DESC")
    fun getExecucoesPorExercicio(exercicioId: String): LiveData<List<Execucao>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(execucao: Execucao)
}
