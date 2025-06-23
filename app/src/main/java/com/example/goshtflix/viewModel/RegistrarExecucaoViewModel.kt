package com.example.goshtflix.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.goshtflix.dao.AppDatabase
import com.example.goshtflix.model.Execucao
import com.example.goshtflix.model.Exercicio
import kotlinx.coroutines.launch

class RegistrarExecucaoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(application, AppDatabase::class.java, "app_db").build()

    fun salvarExecucao(exercicio: Exercicio, serie: Int, reps: Int, peso: Double) {
        viewModelScope.launch {
            db.execucaoDao().inserir(
                Execucao(
                    exercicioId = exercicio.id,
                    treinoId = exercicio.treinoId,
                    serie = serie,
                    repeticoes = reps,
                    peso = peso
                )
            )
        }
    }

    fun getExecucoes(exercicioId: String): LiveData<List<Execucao>> {
        return db.execucaoDao().getExecucoesPorExercicio(exercicioId)
    }
}
