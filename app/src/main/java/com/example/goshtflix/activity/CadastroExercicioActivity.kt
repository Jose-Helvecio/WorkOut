package com.example.goshtflix.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import coil.load
import com.example.goshtflix.databinding.ActivityCadastroExercicioBinding
import com.example.goshtflix.model.Exercicio
import com.example.goshtflix.viewModel.ExercicioViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CadastroExercicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroExercicioBinding
    private val viewModel: ExercicioViewModel by viewModels() // Use 'by viewModels()'
    private val REQUEST_IMAGE_PICK = 101
    private val REQUEST_IMAGE_CAPTURE = 102
    private val REQUEST_CAMERA_PERMISSION = 200

    private var photoFile: File? = null
    private var imagemNovaUri: Uri? = null // URI da imagem recém-selecionada/tirada
    private var exercicioAtual: Exercicio? =
        null // Usar um nome mais claro para o exercício sendo editado
    private lateinit var treinoId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroExercicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        treinoId = intent.getStringExtra("TREINO_ID") ?: run {
            Toast.makeText(this, "ID do treino não encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        exercicioAtual = intent.getParcelableExtra("EXERCICIO")

        exercicioAtual?.let {
            binding.etNome.setText(it.nome)
            binding.etObs.setText(it.observacoes)
            // Carrega a imagem existente do caminho local
            if (it.imagemLocalUri.isNotEmpty()) {
                binding.ivImagem.load(File(it.imagemLocalUri)) // Carrega a imagem a partir de um arquivo
            }
            // Se estiver editando, pré-preencha o campo de contagem de séries se você tivesse um
            // Caso contrário, a lógica de parsing abaixo será usada ao salvar
        }

        binding.ivImagem.setOnClickListener {
            mostrarEscolhaImagem()
        }

        binding.btnSalvar.setOnClickListener {
            salvar()
        }
    }

    private fun mostrarEscolhaImagem() {
        val options = arrayOf("Tirar Foto", "Escolher da Galeria")
        AlertDialog.Builder(this)
            .setTitle("Escolha uma opção")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        if (verificarPermissaoCamera()) {
                            tirarFoto()
                        }
                    }

                    1 -> selecionarImagemGaleria()
                }
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tirarFoto()
            } else {
                Toast.makeText(
                    this,
                    "Permissão da câmera é necessária para tirar fotos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun verificarPermissaoCamera(): Boolean {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
            return false
        }
        return true
    }

    private fun tirarFoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "JPEG_${UUID.randomUUID()}.jpg"
        )
        photoFile?.let {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun selecionarImagemGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    imagemNovaUri = data?.data
                    binding.ivImagem.setImageURI(imagemNovaUri)
                }
                REQUEST_IMAGE_CAPTURE -> {
                    photoFile?.let { originalFile ->
                        val compressedFile = compressImageFile(originalFile, compressQuality = 50)
                        if (compressedFile != null) {
                            photoFile = compressedFile
                            imagemNovaUri = Uri.fromFile(compressedFile)
                            binding.ivImagem.setImageURI(imagemNovaUri)
                        } else {
                            // Se falhar compressão, mantém original
                            imagemNovaUri = Uri.fromFile(originalFile)
                            binding.ivImagem.setImageURI(imagemNovaUri)
                        }
                    }
                }
            }
        }
    }

    private fun salvar() {
        try {
            val nome = binding.etNome.text.toString().trim()
            val obs = binding.etObs.text.toString().trim()

            if (nome.isBlank()) {
                Toast.makeText(this, "O nome do exercício não pode ser vazio.", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            // --- INÍCIO DA SOLUÇÃO: EXTRAIR seriesCount ---
            val seriesCount = extractSeriesCountFromObservations(obs)
            // --- FIM DA SOLUÇÃO ---

            val id = exercicioAtual?.id ?: UUID.randomUUID().toString()
            val imagemLocalUri = exercicioAtual?.imagemLocalUri
                ?: "" // Será atualizada no ViewModel se houver nova imagem

            val exercicioParaSalvar = Exercicio(
                id = id,
                nome = nome,
                treinoId = treinoId,
                imagemLocalUri = imagemLocalUri,
                observacoes = obs,
                seriesCount = seriesCount // <-- ATRIBUÍDO AQUI
            )

            viewModel.salvarExercicio(exercicioParaSalvar, imagemNovaUri)
            Toast.makeText(this, "Salvando exercício...", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro inesperado ao salvar: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }
    }

    // Função para extrair o número de séries da string de observações
    private fun extractSeriesCountFromObservations(observations: String): Int {
        // Esta regex busca por um número (um ou mais dígitos) seguido de 'x' ou 'X'.
        // Ex: "4x10", "4 X 12", "3x8-10"
        val regex =
            "(\\d+)\\s*[xX]".toRegex() // \\d+ (um ou mais dígitos), \\s* (zero ou mais espaços), [xX] (x ou X)

        val matchResult = regex.find(observations)

        // Pega o primeiro grupo de captura (o número antes do 'x')
        return matchResult?.groups?.get(1)?.value?.toIntOrNull() ?: 0
    }

    private fun compressImageFile(originalFile: File, compressQuality: Int = 70): File? {
        // Decodifica o arquivo em Bitmap
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath) ?: return null

        // Cria arquivo temporário para imagem comprimida
        val compressedFile = File(originalFile.parent, "COMPRESSED_${originalFile.name}")

        try {
            val outputStream = FileOutputStream(compressedFile)
            // Comprime JPEG com qualidade menor (0-100, onde 100 = melhor qualidade)
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream)
            outputStream.flush()
            outputStream.close()
            return compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}