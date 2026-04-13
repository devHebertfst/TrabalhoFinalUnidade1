package com.imd.trabalhofinalunidade1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuizCategoriasActivity : AppCompatActivity() {

    private lateinit var cbGeografia: CheckBox
    private lateinit var cbMatematica: CheckBox
    private lateinit var cbCiencia: CheckBox
    private lateinit var tvStatusCategorias: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_categorias)

        cbGeografia = findViewById(R.id.cbCategoriaGeografia)
        cbMatematica = findViewById(R.id.cbCategoriaMatematica)
        cbCiencia = findViewById(R.id.cbCategoriaCiencia)
        tvStatusCategorias = findViewById(R.id.txtStatusCategorias)

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
        if (cbMatematica.isChecked) categoriasSelecionadas.add("Matematica")
        if (cbCiencia.isChecked) categoriasSelecionadas.add("Ciencia")

        if (categoriasSelecionadas.isEmpty()) {
            tvStatusCategorias.text = "Marque pelo menos uma categoria"
            return
        }

        val intent = Intent(this, QuizActivity::class.java)
        intent.putStringArrayListExtra("categorias", ArrayList(categoriasSelecionadas))
        startActivity(intent)
    }
}
