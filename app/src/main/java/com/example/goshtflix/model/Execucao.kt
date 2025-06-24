package com.example.goshtflix.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "execucoes")
data class Execucao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val exercicioId: String,
    val treinoId: String,
    val data: Date = Date(),
    val serie: Int,
    val peso: Double,
    val repeticoes: Int
)
