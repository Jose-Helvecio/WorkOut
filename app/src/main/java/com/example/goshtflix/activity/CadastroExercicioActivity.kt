package com.example.goshtflix.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.bumptech.glide.Glide
import com.example.goshtflix.databinding.ActivityCadastroExercicioBinding
import com.example.goshtflix.model.Exercicio
import com.example.goshtflix.utils.FirebaseUtils
import com.example.goshtflix.viewModel.ExercicioViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CadastroExercicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroExercicioBinding
    private val viewModel: ExercicioViewModel by viewModels()
    private val REQUEST_IMAGE_PICK = 101
    private val REQUEST_IMAGE_CAPTURE = 102

    private val REQUEST_CAMERA_PERMISSION = 200

    private var photoFile: File? = null

    private var imagemUri: Uri? = null
    private var exercicio: Exercicio? = null
    private lateinit var treinoId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroExercicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        treinoId = intent.getStringExtra("TREINO_ID") ?: return
        exercicio = intent.getParcelableExtra<Exercicio>("EXERCICIO")

        exercicio?.let {
            binding.etNome.setText(it.nome)
            binding.etObs.setText(it.observacoes)
            binding.ivImagem.load(it.imagemUrl)
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                tirarFoto()
            } else {
                Toast.makeText(this, "Permissão da câmera é necessária para tirar fotos", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun verificarPermissaoCamera(): Boolean {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            return false
        }
        return true
    }

    private fun tirarFoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        photoFile = criarArquivoImagem()

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

    private fun criarArquivoImagem(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
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
                    imagemUri = data?.data
                    binding.ivImagem.setImageURI(imagemUri)
                }
                REQUEST_IMAGE_CAPTURE -> {
                    imagemUri = Uri.fromFile(photoFile)
                    binding.ivImagem.setImageURI(imagemUri)
                }
            }
        }
    }


    private fun salvar() {
        try {
            val nome = binding.etNome.text.toString()
            val obs = binding.etObs.text.toString()

            if (nome.isBlank() || (imagemUri == null && exercicio == null)) {
                Toast.makeText(this, "Preencha tudo", Toast.LENGTH_SHORT).show()
                return
            }

//            fun salvarComImagemUrl(imagemUrl: String) {
//                val novo = Exercicio(
//                    id = exercicio?.id ?: UUID.randomUUID().toString(),
//                    nome = nome,
//                    treinoId = treinoId,
//                    imagemUrl = imagemUrl,
//                    observacoes = obs
//                )
//                if (exercicio == null)
//                    viewModel.salvarExercicio(treinoId, novo)
//                else
//                    viewModel.atualizarExercicio(treinoId, novo)
//
//                finish()
//            }

            fun salvarComImagemBase64(imagemBase64: String) {
                val novo = Exercicio(
                    id = exercicio?.id ?: UUID.randomUUID().toString(),
                    nome = nome,
                    treinoId = treinoId,
                    imagemUrl = imagemBase64, // continua usando o mesmo campo
                    observacoes = obs
                )
                if (exercicio == null)
                    viewModel.salvarExercicio(treinoId, novo)
                else
                    viewModel.atualizarExercicio(treinoId, novo)

                finish()
            }

            if (imagemUri != null) {
                val base64 = converterImagemParaBase64(imagemUri!!)
                salvarComImagemBase64(base64)
            } else {
                // Aqui é edição sem mudar a imagem, salva com a imagem atual do exercício
                val imagemUrlAtual = exercicio?.imagemUrl ?: ""
                salvarComImagemBase64(imagemUrlAtual)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro inesperado: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

//    private fun salvar() {
//        try {
//            val nome = binding.etNome.text.toString()
//            val obs = binding.etObs.text.toString()
//
//            if (nome.isBlank() || (imagemUri == null && exercicio == null)) {
//                Toast.makeText(this, "Preencha tudo", Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            fun salvarComImagemBase64(imagemBase64: String) {
//                val novo = Exercicio(
//                    id = exercicio?.id ?: UUID.randomUUID().toString(),
//                    nome = nome,
//                    treinoId = treinoId,
//                    imagemUrl = imagemBase64, // continua usando o mesmo campo
//                    observacoes = obs
//                )
//                if (exercicio == null)
//                    viewModel.salvarExercicio(treinoId, novo)
//                else
//                    viewModel.atualizarExercicio(treinoId, novo)
//
//                finish()
//            }
//
//            if (imagemUri != null) {
//                val base64 = converterImagemParaBase64(imagemUri!!)
//                salvarComImagemBase64(base64)
//            } else {
//                // Se está editando e não mudou a imagem, usa a imagem base64 atual
//                val imagemBase64Atual = exercicio?.imagemUrl ?: ""
//                salvarComImagemBase64(imagemBase64Atual)
//            }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(this, "Erro inesperado: ${e.message}", Toast.LENGTH_LONG).show()
//        }
//    }


    private fun converterImagemParaBase64(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    }


}