package com.example.goshtflix.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goshtflix.R
import com.example.goshtflix.adapter.ExercicioAdapter
import com.example.goshtflix.databinding.ActivityListaExerciciosBinding
import com.example.goshtflix.model.Exercicio
import com.example.goshtflix.model.Treino
import com.example.goshtflix.viewModel.ExercicioViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class ListaExerciciosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaExerciciosBinding
    private lateinit var adapter: ExercicioAdapter
    private val viewModel: ExercicioViewModel by viewModels()
    private lateinit var treino: Treino

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaExerciciosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        treino = intent.getParcelableExtra("TREINO") ?: return

        binding.toolbarText.text = treino.nome

        supportActionBar?.title = treino.nome

        adapter = ExercicioAdapter(
            lista = mutableListOf(),
            onEditar = { abrirCadastro(it) },
            onExcluir = { excluir(it) }
        )
        binding.rvExercicios.layoutManager = LinearLayoutManager(this)
        binding.rvExercicios.adapter = adapter

        viewModel.exercicios.observe(this) { listaExercicios ->
            adapter.atualizarLista(listaExercicios)

            if (listaExercicios.isNullOrEmpty()) {
                binding.mensagemVazia.visibility = android.view.View.VISIBLE
                binding.rvExercicios.visibility = android.view.View.GONE
            } else {
                binding.mensagemVazia.visibility = android.view.View.GONE
                binding.rvExercicios.visibility = android.view.View.VISIBLE
            }
        }


        viewModel.carregarExercicios(treino.id)

        binding.fabAdicionar.setOnClickListener {
            abrirCadastro(exercicio = null)
        }
        binding.fabExc.setOnClickListener {
            finish() // fecha esta Activity e volta para a anterior
        }
    }

    private fun abrirCadastro(exercicio: Exercicio?) {
        val intent = Intent(this, CadastroExercicioActivity::class.java)
        intent.putExtra("TREINO_ID", treino.id)

        // Copia o exercício sem a imagem para evitar estourar a intent
        exercicio?.let {
            val exercicioSemImagem = it.copy(imagemUrl = "")
            intent.putExtra("EXERCICIO", exercicioSemImagem)
        }

        startActivity(intent)
    }


    private fun excluir(exercicio: Exercicio) {
        adapter.removerItem(exercicio)

        viewModel.deletarExercicio(treino.id, exercicio.id, exercicio.imagemUrl)
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarExercicios(treino.id)
    }
}
