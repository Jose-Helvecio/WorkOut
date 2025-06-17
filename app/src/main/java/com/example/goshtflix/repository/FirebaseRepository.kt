package com.example.goshtflix.repository

import android.net.Uri
import com.example.goshtflix.model.Exercicio
import com.example.goshtflix.model.Treino
import com.example.goshtflix.utils.FirebaseUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.util.UUID

class FirebaseRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    private fun userId() = auth.currentUser?.uid ?: ""

    fun addTreino(treino: Treino): Task<Void> {
        return FirebaseUtils.getFirestoreInstance()
            .collection("treinos")
            .document(treino.id.toString())
            .set(treino)
    }


    fun getTreinos() = db.collection("treinos")
        .whereEqualTo("userId", userId())

    fun updateTreino(treino: Treino, onComplete: (Boolean) -> Unit) {
        val id = treino.id
        if (id.isNullOrEmpty()) {
            onComplete(false)
            return
        }
        db.collection("treinos")
            .document(id)
            .set(treino)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }


    fun deleteTreino(id: String) = db.collection("treinos")
        .document(id)
        .delete()

    fun addExercicio(exercicio: Exercicio) = db.collection("exercicios")
        .add(exercicio)

    fun getExerciciosByTreino(treinoId: String) = db.collection("exercicios")
        .whereEqualTo("treinoId", treinoId)

    fun updateExercicio(exercicio: Exercicio) = db.collection("exercicios")
        .document(exercicio.id)
        .set(exercicio)

    fun deleteExercicio(id: String) = db.collection("exercicios")
        .document(id)
        .delete()

    fun uploadImage(uri: Uri): StorageReference {
        val ref = storage.reference.child("exercicios/${UUID.randomUUID()}")
        ref.putFile(uri)
        return ref
    }
}
