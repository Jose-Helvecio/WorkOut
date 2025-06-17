package com.example.goshtflix.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.goshtflix.model.Exercicio
import com.example.goshtflix.repository.ExercicioRepository
import com.example.goshtflix.repository.FirebaseRepository

class ExercicioViewModel : ViewModel() {

    private val exercicioRepository = ExercicioRepository()

    private val _exercicios = MutableLiveData<List<Exercicio>>()
    val exercicios: LiveData<List<Exercicio>> = _exercicios

    fun carregarExercicios(idTreino: String) {
        exercicioRepository.obterExercicios(idTreino) { lista ->
            _exercicios.postValue(lista)
        }
    }

    fun deletarExercicio(idTreino: String, exercicioId: String, imagemUrl: String) {
        exercicioRepository.removerExercicio(idTreino, exercicioId, imagemUrl) {
            carregarExercicios(idTreino)
        }
    }

    fun salvarExercicio(idTreino: String, exercicio: Exercicio) {
        exercicioRepository.salvarExercicio(idTreino, exercicio) {
            carregarExercicios(idTreino)
        }
    }

    fun atualizarExercicio(idTreino: String, exercicio: Exercicio) {
        exercicioRepository.atualizarExercicio(idTreino, exercicio) {
            carregarExercicios(idTreino)
        }
    }
}

