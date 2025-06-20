package com.example.goshtflix.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "treinos")

data class Treino(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var nome: String = "",
    var descricao: String = "",
    val criadoEm: Long = System.currentTimeMillis(),
    var userId: String = ""
) : Parcelable

