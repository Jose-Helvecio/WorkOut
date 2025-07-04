package com.example.goshtflix.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.goshtflix.dao.AppDatabase
import com.example.goshtflix.model.Exercicio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ExercicioViewModel(application: Application) : AndroidViewModel(application) {

    private val exercicioDao = AppDatabase.getDatabase(application).exercicioDao()
    private val context = application.applicationContext

    fun getExerciciosForTreino(treinoId: String): LiveData<List<Exercicio>> {
        return exercicioDao.getExerciciosByTreinoId(treinoId).asLiveData()
    }

    // Salva ou atualiza um exercício (com ou sem nova imagem)
    fun salvarExercicio(exercicio: Exercicio, novaImagemUri: Uri?) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val exercicioFinal: Exercicio
            var imagemCaminhoSalvo: String? = null

            if (novaImagemUri != null) {
                // Salvar a nova imagem localmente e obter o caminho
                imagemCaminhoSalvo = salvarImagemLocalmente(novaImagemUri)
                // Se o exercício já tinha uma imagem antiga e estamos atualizando, deletar a antiga
                if (exercicio.imagemLocalUri.isNotEmpty() && exercicio.imagemLocalUri != imagemCaminhoSalvo) {
                    deletarImagemLocalmente(exercicio.imagemLocalUri)
                }
            } else {
                // Se não há nova imagem, usar o caminho da imagem existente (se houver)
                imagemCaminhoSalvo = exercicio.imagemLocalUri
            }

            exercicioFinal = exercicio.copy(imagemLocalUri = imagemCaminhoSalvo ?: "")

            if (exercicioDao.getExercicioById(exercicioFinal.id) == null) {
                // Novo exercício
                exercicioDao.insertExercicio(exercicioFinal)
            } else {
                // Atualizar exercício existente
                exercicioDao.updateExercicio(exercicioFinal)
            }
        }
    }

    // Deleta um exercício e sua imagem associada localmente
    fun deletarExercicio(exercicio: Exercicio) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            if (exercicio.imagemLocalUri.isNotEmpty()) {
                deletarImagemLocalmente(exercicio.imagemLocalUri)
            }
            exercicioDao.deleteExercicio(exercicio)
        }
    }

    // --- Funções para manipulação de arquivos de imagem ---

    private fun salvarImagemLocalmente(uri: Uri): String? {
        return try {
            val timeStamp = System.currentTimeMillis()
            val fileName = "JPEG_${timeStamp}.jpg"
            val outputDir = File(context.filesDir, "exercicio_images") // Diretório interno
            if (!outputDir.exists()) outputDir.mkdirs()

            val outputFile = File(outputDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                outputFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            outputFile.absolutePath // Retorna o caminho absoluto do arquivo
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun deletarImagemLocalmente(caminhoAbsoluto: String): Boolean {
        return try {
            val file = File(caminhoAbsoluto)
            if (file.exists()) {
                file.delete()
            } else {
                false // Arquivo não existe
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}