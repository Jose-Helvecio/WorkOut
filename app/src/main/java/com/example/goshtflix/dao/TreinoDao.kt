package com.example.goshtflix.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goshtflix.model.Treino
import kotlinx.coroutines.flow.Flow

@Dao
interface TreinoDao {

    @Query("SELECT * FROM treinos ORDER BY nome ASC")
    fun getAllTreinos(): Flow<List<Treino>> // Usa Flow para observar mudanças

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreino(treino: Treino)

    @Update
    suspend fun updateTreino(treino: Treino)

    @Delete
    suspend fun deleteTreino(treino: Treino)

    @Query("SELECT * FROM treinos WHERE id = :treinoId LIMIT 1")
    suspend fun getTreinoById(treinoId: String): Treino?
}
