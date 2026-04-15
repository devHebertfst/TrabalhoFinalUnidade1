package com.imd.trabalhofinalunidade1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.json.JSONArray

class QuizResultadoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_resultado)

        val pontuacao = intent.getIntExtra(EXTRA_PONTUACAO, 0)
        val totalPerguntas = intent.getIntExtra(EXTRA_TOTAL_PERGUNTAS, 0)
        val tempoTotalMs = intent.getLongExtra(EXTRA_TEMPO_TOTAL_MS, 0L)
        val categorias = intent.getStringArrayListExtra(EXTRA_CATEGORIAS).orEmpty()
        val revisaoErradasJson = intent.getStringExtra(EXTRA_REVISAO_ERRADAS_JSON)

        val percentual = if (totalPerguntas > 0) {
            (pontuacao * 100) / totalPerguntas
        } else {
            0
        }

        findViewById<TextView>(R.id.txtResultadoCategorias).text =
            "Categorias: ${categorias.joinToString(", ")}"
        findViewById<TextView>(R.id.txtResultadoPontuacao).text =
            "$pontuacao / $totalPerguntas"
        findViewById<TextView>(R.id.txtResultadoPercentual).text =
            "$percentual%"
        findViewById<TextView>(R.id.txtResultadoTempo).text =
            formatarTempo(tempoTotalMs)

        renderizarRevisaoRespostas(revisaoErradasJson)

        findViewById<Button>(R.id.btnResultadoJogarNovamente).setOnClickListener {
            val intent = Intent(this, QuizCategoriasActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnResultadoVerHistorico).setOnClickListener {
            startActivity(Intent(this, QuizHistoricoActivity::class.java))
        }

        findViewById<Button>(R.id.btnResultadoVoltarMenu).setOnClickListener {
            val intent = Intent(this, QuizMenuActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun formatarTempo(tempoTotalMs: Long): String {
        val totalSegundos = (tempoTotalMs / 1000).toInt()
        val minutos = totalSegundos / 60
        val segundos = totalSegundos % 60
        return String.format("%02d:%02d", minutos, segundos)
    }

    private fun renderizarRevisaoRespostas(raw: String?) {
        if (raw.isNullOrBlank()) return

        val jsonArray = JSONArray(raw)
        if (jsonArray.length() == 0) return

        val titulo = findViewById<TextView>(R.id.txtTituloRevisaoErradas)
        val container = findViewById<LinearLayout>(R.id.layoutRevisaoErradas)
        val inflater = LayoutInflater.from(this)

        titulo.visibility = View.VISIBLE

        for (index in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(index)
            val view = inflater.inflate(R.layout.item_quiz_revisao, container, false)
            val acertou = item.getBoolean("acertou")
            view.findViewById<TextView>(R.id.txtRevisaoPergunta).text =
                "${index + 1}. ${item.getString("pergunta")}"
            view.findViewById<TextView>(R.id.txtRevisaoStatus).apply {
                text = if (acertou) "Acertou" else "Errou"
                setTextColor(
                    ContextCompat.getColor(
                        this@QuizResultadoActivity,
                        if (acertou) R.color.quiz_btn_correct else R.color.quiz_btn_wrong
                    )
                )
            }
            view.findViewById<TextView>(R.id.txtRevisaoRespostaUsuario).text =
                "Sua resposta: ${item.getString("respostaUsuario")}"
            view.findViewById<TextView>(R.id.txtRevisaoRespostaCorreta).text =
                "Correta: ${item.getString("respostaCorreta")}"
            container.addView(view)
        }
    }

    companion object {
        const val EXTRA_PONTUACAO = "pontuacao"
        const val EXTRA_TOTAL_PERGUNTAS = "total_perguntas"
        const val EXTRA_TEMPO_TOTAL_MS = "tempo_total_ms"
        const val EXTRA_CATEGORIAS = "categorias"
        const val EXTRA_REVISAO_ERRADAS_JSON = "revisao_erradas_json"
    }
}
