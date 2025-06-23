package com.example.goshtflix.activity

import ExecucaoAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goshtflix.databinding.ActivityRegistrarExecucaoBinding
import com.example.goshtflix.model.Exercicio
import com.example.goshtflix.viewModel.RegistrarExecucaoViewModel

class RegistrarExecucaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarExecucaoBinding
    private lateinit var viewModel: RegistrarExecucaoViewModel
    private lateinit var exercicio: Exercicio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarExecucaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        exercicio = intent.getParcelableExtra("EXERCICIO") ?: run {
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[RegistrarExecucaoViewModel::class.java]

        binding.tvTitulo.text = exercicio.nome

        binding.btnSalvarExecucao.setOnClickListener {
            val serie = binding.etSerie.text.toString().toIntOrNull()
            val reps = binding.etReps.text.toString().toIntOrNull()
            val peso = binding.etPeso.text.toString().toDoubleOrNull()

            if (serie == null || reps == null || peso == null) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.salvarExecucao(exercicio, serie, reps, peso)
            limparCampos()
        }

        val adapter = ExecucaoAdapter()
        binding.rvHistorico.adapter = adapter
        binding.rvHistorico.layoutManager = LinearLayoutManager(this)

        viewModel.getExecucoes(exercicio.id).observe(this) {
            adapter.atualizarLista(it)
        }
    }

    private fun limparCampos() {
        binding.etSerie.text?.clear()
        binding.etReps.text?.clear()
        binding.etPeso.text?.clear()
    }
}
