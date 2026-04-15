package com.imd.trabalhofinalunidade1

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuizHistoricoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_historico)

        val container = findViewById<LinearLayout>(R.id.layoutHistoricoContainer)
        val emptyText = findViewById<TextView>(R.id.txtHistoricoVazio)
        val entries = QuizHistoryStorage.loadEntries(this)

        if (entries.isEmpty()) {
            emptyText.text = "Nenhuma partida registrada ainda."
        } else {
            emptyText.text = ""
            val inflater = LayoutInflater.from(this)
            entries.forEach { entry ->
                val view = inflater.inflate(R.layout.item_quiz_historico, container, false)
                val percentual = if (entry.totalPerguntas > 0) {
                    (entry.pontuacao * 100) / entry.totalPerguntas
                } else {
                    0
                }

                view.findViewById<TextView>(R.id.txtHistoricoCategorias).text =
                    "Categorias: ${entry.categorias.joinToString(", ")}"
                view.findViewById<TextView>(R.id.txtHistoricoPontuacao).text =
                    "Resultado: ${entry.pontuacao}/${entry.totalPerguntas}  •  $percentual%"
                view.findViewById<TextView>(R.id.txtHistoricoTempo).text =
                    "Tempo: ${formatarTempo(entry.tempoTotalMs)}"

                container.addView(view)
            }
        }

        findViewById<Button>(R.id.btnHistoricoVoltarMenu).setOnClickListener {
            finish()
        }
    }

    private fun formatarTempo(tempoTotalMs: Long): String {
        val totalSegundos = (tempoTotalMs / 1000).toInt()
        val minutos = totalSegundos / 60
        val segundos = totalSegundos % 60
        return String.format("%02d:%02d", minutos, segundos)
    }
}
