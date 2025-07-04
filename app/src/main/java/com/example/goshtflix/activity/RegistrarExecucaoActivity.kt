package com.example.goshtflix.activity

import ExecucaoAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels // Mantenha esta importação para 'by viewModels()'
import androidx.appcompat.app.AppCompatActivity
// import androidx.lifecycle.ViewModelProvider // REMOVA esta importação, não é mais necessária
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goshtflix.R
import com.example.goshtflix.databinding.ActivityRegistrarExecucaoBinding
import com.example.goshtflix.databinding.ItemSerieInputBinding
import com.example.goshtflix.model.Execucao
import com.example.goshtflix.model.Exercicio
import com.example.goshtflix.viewModel.RegistrarExecucaoViewModel
import com.google.android.material.textfield.TextInputEditText

class RegistrarExecucaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarExecucaoBinding
    // Inicialização do ViewModel usando o delegate by viewModels()
    private val viewModel: RegistrarExecucaoViewModel by viewModels()
    private lateinit var exercicio: Exercicio

    private val serieInputFields = mutableListOf<SerieInputRefs>()
    private var lastExecutionsBySerie: Map<Int, Execucao> = emptyMap()

    data class SerieInputRefs(
        val tvSerieNumber: TextView,
        val tvAnterior: TextView,
        val etPeso: TextInputEditText,
        val etReps: TextInputEditText,
        val ivCheck: ImageView
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarExecucaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        exercicio = intent.getParcelableExtra("EXERCICIO") ?: run {
            Toast.makeText(this, "Erro: Exercício não encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvTitulo.text = exercicio.nome
        binding.tvDescricaoExercicio.text = exercicio.observacoes

        // 1. Configura o RecyclerView do histórico ANTES de observar os dados
        // Isso garante que o adaptador esteja pronto quando os dados chegarem
        binding.rvHistorico.layoutManager = LinearLayoutManager(this)
        binding.rvHistorico.adapter = ExecucaoAdapter()

        // 2. Observe o histórico de execuções
        // Agora, use 'it.dataExecucao' em vez de 'it.data'
        viewModel.getExecucoes(exercicio.id).observe(this) { historicoList ->
            lastExecutionsBySerie = historicoList
                .groupBy { it.serie }
                .mapValues { (_, executions) ->
                    executions.maxByOrNull { it.data } // <-- CORRIGIDO: Use 'it.dataExecucao'
                }
                .filterValues { it != null }
                .mapValues { it.value!! }

            gerarLinhasDeSerie() // Gera as linhas de entrada após ter o histórico

            // Atualiza o RecyclerView do histórico
            (binding.rvHistorico.adapter as? ExecucaoAdapter)?.atualizarLista(historicoList)
        }

        binding.btnSalvarExecucao.setOnClickListener {
            salvarTodasAsExecucoes()
        }
    }

    private fun gerarLinhasDeSerie() {
        binding.layoutSeriesInputContainer.removeAllViews()
        serieInputFields.clear()

        if (exercicio.seriesCount > 0) {
            for (i in 1..exercicio.seriesCount) {
                adicionarLinhaSerie(i)
            }
        } else {
            val noSeriesText = TextView(this).apply {
                text = "Nenhuma série configurada para este exercício."
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    // CORRIGIDO: Adiciona o topMargin novamente usando o recurso @dimen/default_margin
                }
                gravity = android.view.Gravity.CENTER
                // CORRIGIDO: Usa o estilo MaterialComponents para texto
                setTextAppearance(R.font.roboto)
            }
            binding.layoutSeriesInputContainer.addView(noSeriesText)
        }
    }

    private fun adicionarLinhaSerie(serieNumber: Int) {
        val seriesBinding = ItemSerieInputBinding.inflate(LayoutInflater.from(this), binding.layoutSeriesInputContainer, false)

        seriesBinding.tvSerieNumber.text = serieNumber.toString()

        val lastExec = lastExecutionsBySerie[serieNumber]
        seriesBinding.tvAnterior.text = lastExec?.let {
            "${it.peso}kg x ${it.repeticoes}"
        } ?: "N/A"

        // Cria referência
        val refs = SerieInputRefs(
            tvSerieNumber = seriesBinding.tvSerieNumber,
            tvAnterior = seriesBinding.tvAnterior,
            etPeso = seriesBinding.etPeso,
            etReps = seriesBinding.etReps,
            ivCheck = seriesBinding.ivCheck // <-- Aqui
        )

        // Lógica de clique no botão de salvar individual
        refs.ivCheck.setOnClickListener {
            val peso = refs.etPeso.text?.toString()?.replace(",", ".")?.toDoubleOrNull()
            val reps = refs.etReps.text?.toString()?.toIntOrNull()

            if (peso == null || reps == null) {
                Toast.makeText(this, "Preencha peso e repetições corretamente.", Toast.LENGTH_SHORT).show()
                refs.etPeso.error = if (peso == null) "Obrigatório" else null
                refs.etReps.error = if (reps == null) "Obrigatório" else null
                return@setOnClickListener
            }

            val execucao = Execucao(
                exercicioId = exercicio.id,
                treinoId = exercicio.treinoId,
                serie = serieNumber,
                peso = peso,
                repeticoes = reps
            )

            viewModel.salvarExecucao(exercicio, serieNumber, reps, peso)

            Toast.makeText(this, "Série $serieNumber salva com sucesso!", Toast.LENGTH_SHORT).show()

            // (Opcional) Mudar o ícone para "check verde"
            refs.ivCheck.setImageResource(R.drawable.ic_favorito_seelcionado) // Ex: ✅
            refs.ivCheck.setColorFilter(getColor(R.color.green))
        }

        serieInputFields.add(refs)
        binding.layoutSeriesInputContainer.addView(seriesBinding.root)
    }

    private fun salvarTodasAsExecucoes() {
        val execucoesParaSalvar = mutableListOf<Execucao>()
        var allFieldsValid = true

        serieInputFields.forEachIndexed { index, refs ->
            val serieNumber = refs.tvSerieNumber.text.toString().toIntOrNull() ?: (index + 1)
            val peso = refs.etPeso.text?.toString()?.replace(",", ".")?.toDoubleOrNull()
            val reps = refs.etReps.text?.toString()?.toIntOrNull()

            if (peso == null || reps == null) {
                allFieldsValid = false
                refs.etPeso.error = "Campo obrigatório"
                refs.etReps.error = "Campo obrigatório"
            } else {
                refs.etPeso.error = null
                refs.etReps.error = null
            }

            if (peso != null && reps != null) {
                execucoesParaSalvar.add(
                    Execucao(
                        exercicioId = exercicio.id,
                        treinoId = exercicio.treinoId,
                        serie = serieNumber,
                        repeticoes = reps,
                        peso = peso
                    )
                )
            }
        }

        if (allFieldsValid && execucoesParaSalvar.isNotEmpty()) {
            viewModel.salvarVariasExecucoes(execucoesParaSalvar)
            Toast.makeText(this, "Execuções registradas com sucesso!", Toast.LENGTH_SHORT).show()
            limparTodosOsCampos()
        } else if (!allFieldsValid) {
            Toast.makeText(this, "Preencha todos os campos de peso e repetições.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Nenhuma série para salvar ou campos incompletos.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limparTodosOsCampos() {
        serieInputFields.forEach { refs ->
            refs.etPeso.text?.clear()
            refs.etReps.text?.clear()
            refs.etPeso.error = null
            refs.etReps.error = null
        }
    }
}