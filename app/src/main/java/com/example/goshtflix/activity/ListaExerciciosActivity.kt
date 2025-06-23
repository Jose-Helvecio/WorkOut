package com.example.goshtflix.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goshtflix.adapter.ExercicioAdapter
import com.example.goshtflix.databinding.ActivityListaExerciciosBinding
import com.example.goshtflix.model.Exercicio
import com.example.goshtflix.model.Treino
import com.example.goshtflix.viewModel.ExercicioViewModel

class ListaExerciciosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaExerciciosBinding
    private lateinit var adapter: ExercicioAdapter
    private val viewModel: ExercicioViewModel by viewModels() // Use o AndroidViewModel
    private lateinit var treino: Treino

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaExerciciosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        treino = intent.getParcelableExtra("TREINO") ?: run {
            finish()
            return
        }

        binding.toolbarText.text = treino.nome

        adapter = ExercicioAdapter(
            lista = mutableListOf(),
            onEditar = { abrirCadastro(it) },
            onExcluir = { excluir(it) },
            onRegistrarExecucao = { abrirRegistroExecucao(it) }
        )

        binding.rvExercicios.layoutManager = LinearLayoutManager(this)
        binding.rvExercicios.adapter = adapter

        // Observa a lista de exercícios para o treino específico
        viewModel.getExerciciosForTreino(treino.id).observe(this) { listaExercicios ->
            adapter.atualizarLista(listaExercicios)

            if (listaExercicios.isNullOrEmpty()) {
                binding.mensagemVazia.visibility = View.VISIBLE
                binding.rvExercicios.visibility = View.GONE
            } else {
                binding.mensagemVazia.visibility = View.GONE
                binding.rvExercicios.visibility = View.VISIBLE
            }
        }

        binding.fabAdicionar.setOnClickListener {
            abrirCadastro(exercicio = null)
        }
        binding.fabExc.setOnClickListener {
            finish()
        }
    }

    private fun abrirCadastro(exercicio: Exercicio?) {
        val intent = Intent(this, CadastroExercicioActivity::class.java)
        intent.putExtra("TREINO_ID", treino.id)
        if (exercicio != null) {
            intent.putExtra("EXERCICIO", exercicio)
        }
        startActivity(intent)
    }

    private fun excluir(exercicio: Exercicio) {
        adapter.removerItem(exercicio) // Feedback visual imediato
        viewModel.deletarExercicio(exercicio) // Deleta do banco e remove a imagem local
    }

    private fun abrirRegistroExecucao(exercicio: Exercicio) {
        val intent = Intent(this, RegistrarExecucaoActivity::class.java)
        intent.putExtra("EXERCICIO", exercicio)
        startActivity(intent)
    }

}