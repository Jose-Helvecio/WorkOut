package com.example.goshtflix.repository

import com.example.goshtflix.model.Exercicio
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ExercicioRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    fun obterExercicios(idTreino: String, callback: (List<Exercicio>) -> Unit) {
        firestore.collection("treinos").document(idTreino).collection("exercicios")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(Exercicio::class.java)?.apply { id = doc.id }
                }
                callback(lista)
            }
    }

    fun removerExercicio(idTreino: String, exercicioId: String, imagemUrl: String, callback: () -> Unit) {
        firestore.collection("treinos").document(idTreino).collection("exercicios")
            .document(exercicioId)
            .delete()
            .addOnSuccessListener {
                // Se quiser, pode deletar a imagem do Storage aqui usando imagemUrl
                callback()
            }
    }

    fun salvarExercicio(idTreino: String, exercicio: Exercicio, callback: () -> Unit) {
        val ref = firestore.collection("treinos")
            .document(idTreino)
            .collection("exercicios")
            .document() // gera um novo ID

        val exercicioComId = exercicio.copy(id = ref.id)

        ref.set(exercicioComId)
            .addOnSuccessListener { callback() }
    }


    fun atualizarExercicio(idTreino: String, exercicio: Exercicio, callback: () -> Unit) {
        val id = exercicio.id ?: return
        firestore.collection("treinos").document(idTreino).collection("exercicios")
            .document(id)
            .set(exercicio)
            .addOnSuccessListener { callback() }
    }
}
