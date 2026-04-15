package com.imd.trabalhofinalunidade1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class QuizCategoriasActivity : AppCompatActivity() {

    private lateinit var cbHistoria: CheckBox
    private lateinit var cbGeografia: CheckBox

    private lateinit var cbCiencia: CheckBox
    private lateinit var cbMatematica: CheckBox

    private lateinit var cbPortugues: CheckBox

    private lateinit var cbEsporte: CheckBox

    private lateinit var cbTecnologia: CheckBox

    private lateinit var cbEntretenimento: CheckBox


    private lateinit var tvStatusCategorias: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_quiz_categorias)

        cbHistoria = findViewById(R.id.cbCategoriaHistoria)
        cbGeografia = findViewById(R.id.cbCategoriaGeografia)
        cbCiencia = findViewById(R.id.cbCategoriaCiencia)
        cbMatematica = findViewById(R.id.cbCategoriaMatematica)
        cbPortugues = findViewById(R.id.cbCategoriaPortugues)
        cbEsporte = findViewById(R.id.cbCategoriaEsporte)
        cbTecnologia = findViewById(R.id.cbCategoriaTecnologia)
        cbEntretenimento = findViewById(R.id.cbCategoriaEntretenimento)

        findViewById<Button>(R.id.btnIniciarCategorias).setOnClickListener {
            abrirQuiz()
        }

        findViewById<Button>(R.id.btnVoltarMenuQuiz).setOnClickListener {
            finish()
        }
    }

    private fun abrirQuiz() {
        val categoriasSelecionadas = mutableListOf<String>()

        if (cbGeografia.isChecked) categoriasSelecionadas.add("Geografia")
        if (cbMatematica.isChecked) categoriasSelecionadas.add("Matemática")
        if (cbCiencia.isChecked) categoriasSelecionadas.add("Ciência")
        if (cbHistoria.isChecked) categoriasSelecionadas.add("História")
        if (cbPortugues.isChecked) categoriasSelecionadas.add("Português")
        if (cbEsporte.isChecked) categoriasSelecionadas.add("Esportes")
        if (cbTecnologia.isChecked) categoriasSelecionadas.add("Tecnologia")
        if (cbEntretenimento.isChecked) categoriasSelecionadas.add("Entretenimento")

        if (categoriasSelecionadas.isEmpty()) {
            tvStatusCategorias.text = "Marque pelo menos uma categoria"
            return
        }

        val intent = Intent(this, QuizActivity::class.java)
        intent.putStringArrayListExtra("categorias", ArrayList(categoriasSelecionadas))

        startActivity(intent)
    }
}
