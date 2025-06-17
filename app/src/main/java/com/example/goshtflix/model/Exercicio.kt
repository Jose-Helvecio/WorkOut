package com.example.goshtflix.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Exercicio(
    var id: String = "",
    var treinoId: String = "",
    var nome: String = "",
    var imagemUrl: String = "",
    var observacoes: String = ""
) : Parcelable
