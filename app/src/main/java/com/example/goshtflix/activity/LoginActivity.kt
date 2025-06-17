package com.example.goshtflix.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goshtflix.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.buttonSubmit.setOnClickListener {
            val email = binding.etLogin.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.reload()?.addOnCompleteListener { reloadTask ->
                            if (reloadTask.isSuccessful) {
                                val name = user.displayName ?: "Usuário"
                                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                                navigateToMainActivity(name)
                            } else {
                                Toast.makeText(this, "Erro ao carregar perfil.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "E-mail ou senha incorretos", Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.tvCadastro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToMainActivity(name: String) {
        val intent = Intent(this, ListaTreinosActivity::class.java)
        intent.putExtra("USER_NAME", name)
        startActivity(intent)
        finish()
    }
}
