package com.example.goshtflix.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "execucoes")
data class Execucao(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exercicioId: String, // referencia ao exercício
    val treinoId: String,    // opcional para facilitar queries por treino
    val data: Long = System.currentTimeMillis(),
    val serie: Int,
    val peso: Double,
    val repeticoes: Int
)