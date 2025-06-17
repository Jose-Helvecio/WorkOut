package com.example.goshtflix.utils

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

object FirebaseUtils {
    fun getFirestoreInstance() = FirebaseFirestore.getInstance()
    fun getCurrentUserId() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    fun getStorageReference() = Firebase.storage.reference

}
