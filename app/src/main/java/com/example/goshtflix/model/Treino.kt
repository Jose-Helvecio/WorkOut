package com.example.goshtflix.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Treino(
    var id: String = "",
    var nome: String = "",
    var descricao: String = "",
    val criadoEm: Long = System.currentTimeMillis(),
    var userId: String = ""
) : Parcelable

