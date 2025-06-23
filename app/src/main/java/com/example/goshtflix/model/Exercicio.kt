package com.example.goshtflix.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "exercicios")

data class Exercicio(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var treinoId: String = "",
    var nome: String = "",
    var imagemLocalUri: String = "",
    var observacoes: String = "",
    val series: Int = 0,
    val repeticoes: Int = 0,
    val peso: Double = 0.0,
    val dataRegistro: Long = System.currentTimeMillis()
) : Parcelable
