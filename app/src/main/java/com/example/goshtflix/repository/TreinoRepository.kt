package com.example.goshtflix.repository

import androidx.lifecycle.MutableLiveData
import com.example.goshtflix.model.Treino
import com.example.goshtflix.utils.FirebaseUtils


class TreinoRepository {

    private val db = FirebaseUtils.getFirestoreInstance()
    private fun getUserId(): String? = FirebaseUtils.getCurrentUserId()


    fun addTreino(treino: Treino, onComplete: (Boolean) -> Unit) {
        val uid = getUserId() ?: return onComplete(false)
        val doc = db.collection("users").document(uid).collection("treinos").document()
        val treinoComId = treino.copy(id = doc.id)
        doc.set(treinoComId).addOnCompleteListener {
            onComplete(it.isSuccessful)
        }
    }

    fun getTreinos(liveData: MutableLiveData<List<Treino>>) {
        val uid = getUserId() ?: return
        db.collection("users").document(uid).collection("treinos")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Treino::class.java) }
                    liveData.postValue(list)
                }
            }
    }

    fun deleteTreino(id: String, onComplete: (Boolean) -> Unit) {
        val uid = getUserId()
        if (uid == null) {
            onComplete(false)
            return
        }
        db.collection("users").document(uid).collection("treinos").document(id)
            .delete().addOnCompleteListener { onComplete(it.isSuccessful) }
    }

}
