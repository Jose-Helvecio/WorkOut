package com.example.goshtflix.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.goshtflix.databinding.ActivityCadastroTreinoBinding
import com.example.goshtflix.model.Treino
import com.example.goshtflix.viewModel.TreinoViewModel

class CadastroTreinoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroTreinoBinding
    private lateinit var viewModel: TreinoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroTreinoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[TreinoViewModel::class.java]

        binding.btnSalvar.setOnClickListener {
            val nome = binding.edtNomeTreino.text.toString().trim()
            val desc = binding.edtDescricaoTreino.text.toString().trim()

            if (nome.isEmpty()) {
                Toast.makeText(this, "Informe o nome do treino", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val treino = Treino(nome = nome, descricao = desc)
            viewModel.adicionarTreino(treino) { sucesso ->
                runOnUiThread {
                    if (sucesso) {
                        Toast.makeText(this, "Treino salvo com sucesso!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ListaTreinosActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish() // Apenas fecha a tela e volta para a anterior
                    } else {
                        Toast.makeText(this, "Erro ao salvar treino.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}