package com.imd.trabalhofinalunidade1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuizResultadoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_resultado)

        val pontuacao = intent.getIntExtra(EXTRA_PONTUACAO, 0)
        val totalPerguntas = intent.getIntExtra(EXTRA_TOTAL_PERGUNTAS, 0)
        val tempoTotalMs = intent.getLongExtra(EXTRA_TEMPO_TOTAL_MS, 0L)
        val categorias = intent.getStringArrayListExtra(EXTRA_CATEGORIAS).orEmpty()

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

        findViewById<Button>(R.id.btnResultadoJogarNovamente).setOnClickListener {
            val intent = Intent(this, QuizCategoriasActivity::class.java)
            startActivity(intent)
            finish()
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

    companion object {
        const val EXTRA_PONTUACAO = "pontuacao"
        const val EXTRA_TOTAL_PERGUNTAS = "total_perguntas"
        const val EXTRA_TEMPO_TOTAL_MS = "tempo_total_ms"
        const val EXTRA_CATEGORIAS = "categorias"
    }
}
