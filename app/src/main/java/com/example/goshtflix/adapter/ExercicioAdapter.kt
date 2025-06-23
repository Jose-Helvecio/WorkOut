package com.example.goshtflix.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load // Importe Coil
import com.example.goshtflix.R
import com.example.goshtflix.model.Exercicio
import com.google.android.material.button.MaterialButton
import java.io.File // Importe File

class ExercicioAdapter(
    private var lista: MutableList<Exercicio>,
    val onEditar: (Exercicio) -> Unit,
    val onExcluir: (Exercicio) -> Unit,
    private val onRegistrarExecucao: (Exercicio) -> Unit

) : RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder>() {

    fun atualizarLista(novaLista: List<Exercicio>) {
        this.lista.clear()
        this.lista.addAll(novaLista)
        notifyDataSetChanged()
    }

    fun removerItem(exercicio: Exercicio) {
        val posicao = lista.indexOf(exercicio)
        if (posicao != -1) {
            lista.removeAt(posicao)
            notifyItemRemoved(posicao)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercicio, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        val exercicio = lista[position]
        holder.bind(exercicio)
    }

    override fun getItemCount(): Int = lista.size

    inner class ExercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNome: TextView = itemView.findViewById(R.id.tvNome)
        private val tvObs: TextView = itemView.findViewById(R.id.tvObs)
        private val ivImagem: ImageView = itemView.findViewById(R.id.ivImagem)
        private val btnEditar: ImageView = itemView.findViewById(R.id.btnEditar)
        private val btnExcluir: ImageView = itemView.findViewById(R.id.btnDeletar)
        private val btnRegistrarExecucao: MaterialButton = itemView.findViewById(R.id.btnRegistrarExecucao)


        fun bind(exercicio: Exercicio) {
            tvNome.text = exercicio.nome
            tvObs.text = exercicio.observacoes

            // Carrega a imagem usando Coil a partir do caminho do arquivo local
            if (exercicio.imagemLocalUri.isNotEmpty()) {
                val imageFile = File(exercicio.imagemLocalUri)
                if (imageFile.exists()) {
                    ivImagem.load(imageFile) {
                        crossfade(true)
                        placeholder(R.drawable.placeholder)
                        error(R.drawable.ic_error)
                    }
                } else {
                    ivImagem.setImageResource(R.drawable.ic_error) // Caso o arquivo não exista mais
                }
            } else {
                ivImagem.setImageResource(R.drawable.placeholder) // Imagem padrão se não houver
            }

            btnEditar.setOnClickListener { onEditar(exercicio) }
            btnExcluir.setOnClickListener { onExcluir(exercicio) }
            btnRegistrarExecucao.setOnClickListener { onRegistrarExecucao(exercicio) }

        }
    }
}
