package com.example.goshtflix.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.goshtflix.model.Treino
import com.example.goshtflix.repository.FirebaseRepository
import com.example.goshtflix.repository.TreinoRepository
import com.example.goshtflix.utils.FirebaseUtils

class TreinoViewModel : ViewModel() {

    private val repo = TreinoRepository()
    val treinos = MutableLiveData<List<Treino>>()

    fun carregarTreinos() {
        repo.getTreinos(treinos)
    }

    fun adicionarTreino(treino: Treino, onComplete: (Boolean) -> Unit) {
        repo.addTreino(treino, onComplete)
    }

    fun deletarTreino(id: String, onComplete: (Boolean) -> Unit) {
        repo.deleteTreino(id, onComplete)
    }
}

