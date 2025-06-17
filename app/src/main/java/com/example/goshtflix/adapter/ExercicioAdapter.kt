package com.example.goshtflix.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.goshtflix.R
import com.example.goshtflix.databinding.ItemExercicioBinding
import com.example.goshtflix.model.Exercicio

class ExercicioAdapter(
    private var lista: MutableList<Exercicio>,
    private val onEditar: (Exercicio) -> Unit,
    private val onExcluir: (Exercicio) -> Unit
) : RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder>() {

    inner class ExercicioViewHolder(val binding: ItemExercicioBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val binding = ItemExercicioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExercicioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        val exercicio = lista[position]
        holder.binding.tvNome.text = exercicio.nome
        holder.binding.tvObs.text = exercicio.observacoes

        // Decodifica a imagem Base64, se existir
        if (!exercicio.imagemUrl.isNullOrBlank()) {
            try {
                val decodedBytes =
                    android.util.Base64.decode(exercicio.imagemUrl, android.util.Base64.DEFAULT)
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(
                    decodedBytes,
                    0,
                    decodedBytes.size
                )
                holder.binding.ivImagem.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                holder.binding.ivImagem.setImageResource(R.drawable.placeholder)
            }
        } else {
            holder.binding.ivImagem.setImageResource(R.drawable.placeholder)
        }

        holder.binding.btnEditar.setOnClickListener {
            onEditar(exercicio)
        }

        holder.binding.btnDeletar.setOnClickListener {
            onExcluir(exercicio)
        }
    }

    fun atualizarLista(novaLista: List<Exercicio>) {
        lista = novaLista.toMutableList()
        notifyDataSetChanged()
    }


    fun removerItem(exercicio: Exercicio) {
        val pos = lista.indexOfFirst { it.id == exercicio.id }
        if (pos != -1) {
            lista.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }




    override fun getItemCount(): Int = lista.size
}
