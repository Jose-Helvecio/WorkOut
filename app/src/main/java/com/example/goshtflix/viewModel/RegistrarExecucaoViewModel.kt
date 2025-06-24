package com.example.goshtflix.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.goshtflix.dao.AppDatabase
import com.example.goshtflix.dao.ExecucaoDao
import com.example.goshtflix.model.Execucao
import com.example.goshtflix.model.Exercicio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrarExecucaoViewModel(application: Application) : AndroidViewModel(application) {

    // Certifique-se que AppDatabase.getDatabase(application).execucaoDao() retorna o DAO correto
    private val execucaoDao = AppDatabase.getDatabase(application).execucaoDao()

    // O método salvarExecucao individual não será mais usado diretamente pela UI neste cenário,
    // mas pode ser mantido para outras finalidades ou testes.
    fun salvarExecucao(exercicio: Exercicio, serie: Int, reps: Int, peso: Double) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { // Garante que a operação de DB seja em background
                execucaoDao.inserir(
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
    }

    // Este método será o principal para salvar os dados das séries
    fun salvarVariasExecucoes(lista: List<Execucao>) {
        viewModelScope.launch(Dispatchers.IO) {
            execucaoDao.inserirVarias(lista) // Assumindo que este método existe no seu DAO
        }
    }

    fun getExecucoes(exercicioId: String): LiveData<List<Execucao>> {
        return execucaoDao.getExecucoesPorExercicio(exercicioId).asLiveData()
    }
}
