package com.example.goshtflix.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goshtflix.R
import com.example.goshtflix.model.Treino

import androidx.recyclerview.widget.DiffUtil // Import DiffUtil
import androidx.recyclerview.widget.ListAdapter // Import ListAdapter
import com.example.goshtflix.databinding.ItemTreinoBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TreinoAdapter(
    private val onClick: (Treino) -> Unit
) : RecyclerView.Adapter<TreinoAdapter.TreinoViewHolder>() {

    private val treinos = mutableListOf<Treino>()

    fun submitList(newList: List<Treino>) {
        treinos.clear()
        treinos.addAll(newList)
        notifyDataSetChanged()
    }

    inner class TreinoViewHolder(private val binding: ItemTreinoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        fun bind(treino: Treino) {
            binding.tvNome.text = treino.nome
            binding.tvDescricao.text = treino.descricao

            val dataFormatada = sdf.format(treino.criadoEm)

            binding.tvData.text = dataFormatada

            binding.root.setOnClickListener { onClick(treino) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreinoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTreinoBinding.inflate(inflater, parent, false)
        return TreinoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TreinoViewHolder, position: Int) {
        holder.bind(treinos[position])
    }

    fun getItem(position: Int): Treino {
        return treinos[position]
    }
    fun getTreinos(): List<Treino> = treinos


    override fun getItemCount() = treinos.size
}

