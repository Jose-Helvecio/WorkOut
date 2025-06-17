package com.example.goshtflix.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goshtflix.R
import com.example.goshtflix.adapter.TreinoAdapter
import com.example.goshtflix.databinding.ActivityListaTreinosBinding
import com.example.goshtflix.model.Treino
import com.example.goshtflix.viewModel.TreinoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class ListaTreinosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaTreinosBinding
    private val viewModel: TreinoViewModel by viewModels()
    private lateinit var adapter: TreinoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaTreinosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = intent.getStringExtra("USER_NAME")
        userName?.let {
            binding.toolbarText.text = "Bem-vindo(a), $it!"
        }

        binding.fabExc.setOnClickListener {
            finish() // fecha esta Activity e volta para a anterior
        }

        adapter = TreinoAdapter { treino ->
            val intent = Intent(this, ListaExerciciosActivity::class.java)
            intent.putExtra("TREINO", treino)
            startActivity(intent)
        }

        binding.recyclerViewTreinos.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTreinos.adapter = adapter

        // Aqui implementamos o swipe to delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val treinoParaDeletar = adapter.getItem(position)

                viewModel.deletarTreino(treinoParaDeletar.id) { sucesso ->
                    if (sucesso) {
                        // Atualiza a lista local removendo o item deletado
                        val novaLista = adapter.getTreinos().filter { it.id != treinoParaDeletar.id }
                        adapter.submitList(novaLista)
                    } else {
                        Toast.makeText(this@ListaTreinosActivity, "Erro ao deletar treino", Toast.LENGTH_SHORT).show()
                        adapter.notifyItemChanged(position) // Reverte o swipe visual
                    }
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTreinos)

        viewModel.treinos.observe(this) {
            adapter.submitList(it)
        }

        viewModel.carregarTreinos()

        binding.fabAdd.setOnClickListener {
            mostrarDialogAdicionarTreino()
        }
    }

    private fun mostrarDialogAdicionarTreino() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.dialog_add_treino)

        val nome = bottomSheetDialog.findViewById<android.widget.EditText>(R.id.edtNome)
        val descricao = bottomSheetDialog.findViewById<android.widget.EditText>(R.id.edtDescricao)
        val btnSalvar = bottomSheetDialog.findViewById<android.widget.Button>(R.id.btnSalvar)

        btnSalvar?.setOnClickListener {
            val nomeTreino = nome?.text.toString()
            if (nomeTreino.isBlank()) {
                Toast.makeText(this, "O nome do treino é obrigatório", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val treino = Treino(
                nome = nomeTreino,
                descricao = descricao?.text.toString(),
                criadoEm = System.currentTimeMillis()
            )
            viewModel.adicionarTreino(treino) { ok ->
                if (ok) bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.show()
    }
}
