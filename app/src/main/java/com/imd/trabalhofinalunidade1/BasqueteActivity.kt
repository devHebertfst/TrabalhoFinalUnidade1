package com.imd.trabalhofinalunidade1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class BasqueteActivity : ComponentActivity() {
    private var pontuacaoTimeA: Int = 0
    private var pontuacaoTimeB: Int = 0

    private var ultimosPontos: Int = 0
    private var ultimoTime: String = ""

    private lateinit var pTimeA: TextView
    private lateinit var pTimeB: TextView
    private lateinit var pDiferenca: TextView

    private var quarto: Int = 1

    private lateinit var tQuarto: TextView

    private lateinit var tHistorico: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basquete)

        val bDesfazer: Button = findViewById(R.id.desfazerPonto)
        bDesfazer.setOnClickListener { desfazerUltimoPonto() }

        tQuarto = findViewById(R.id.quartoAtual)
        tHistorico = findViewById(R.id.historicoQuartos)
        val bEncerrarQuarto: Button = findViewById(R.id.encerrarQuarto)
        bEncerrarQuarto.setOnClickListener { encerrarQuarto() }

        pTimeA = findViewById(R.id.placarTimeA)
        pTimeB = findViewById(R.id.placarTimeB)
        pDiferenca = findViewById(R.id.diferencaPlacar)

        val bTresPontosTimeA: Button = findViewById(R.id.tresPontosA)
        val bDoisPontosTimeA: Button = findViewById(R.id.doisPontosA)
        val bTLivreTimeA: Button = findViewById(R.id.tiroLivreA)
        val bTresPontosTimeB: Button = findViewById(R.id.tresPontosB)
        val bDoisPontosTimeB: Button = findViewById(R.id.doisPontosB)
        val bTLivreTimeB: Button = findViewById(R.id.tiroLivreB)
        val bReiniciar: Button = findViewById(R.id.reiniciarPartida)
        val bVoltarCentral: Button = findViewById(R.id.btnVoltarCentral)


        bTresPontosTimeA.setOnClickListener { adicionarPontos(3, "A") }

        bDoisPontosTimeA.setOnClickListener { adicionarPontos(2, "A") }

        bTLivreTimeA.setOnClickListener { adicionarPontos(1, "A") }

        bTresPontosTimeB.setOnClickListener { adicionarPontos(3, "B") }

        bDoisPontosTimeB.setOnClickListener { adicionarPontos(2, "B") }

        bTLivreTimeB.setOnClickListener { adicionarPontos(1, "B") }

        bReiniciar.setOnClickListener { reiniciarPartida() }
        bVoltarCentral.setOnClickListener { voltarParaCentral() }


    }

    private fun voltarParaCentral() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
        finish()
    }

    fun adicionarPontos(pontos: Int, time: String) {
        ultimosPontos = pontos
        ultimoTime = time
        if(time == "A"){
            pontuacaoTimeA += pontos

        }else {
            pontuacaoTimeB += pontos

        }
        atualizaPlacar(time)
    }

    fun atualizaPlacar(time: String){
        if(time == "A"){
            pTimeA.setText(pontuacaoTimeA.toString())
        }else {
            pTimeB.setText(pontuacaoTimeB.toString())
        }

        val diferenca = pontuacaoTimeA - pontuacaoTimeB
        when {
            diferenca > 0 -> pDiferenca.setText("Time A está vencendo por $diferenca pts")
            diferenca < 0 -> pDiferenca.setText("Time B está vencendo por ${-diferenca} pts")
            else -> pDiferenca.setText("Empate")
        }
    }


    fun reiniciarPartida() {
        pontuacaoTimeA = 0
        pTimeA.setText(pontuacaoTimeA.toString())
        pontuacaoTimeB = 0
        pTimeB.setText(pontuacaoTimeB.toString())
        pDiferenca.setText("Empate")
        Toast.makeText(this,"Placar reiniciado",Toast.LENGTH_SHORT).show()

        quarto = 1
        tQuarto.setText("1º Quarto")
        tHistorico.setText("")
        val bTresPontosTimeA: Button = findViewById(R.id.tresPontosA)
        val bDoisPontosTimeA: Button = findViewById(R.id.doisPontosA)
        val bTLivreTimeA: Button = findViewById(R.id.tiroLivreA)
        val bTresPontosTimeB: Button = findViewById(R.id.tresPontosB)
        val bDoisPontosTimeB: Button = findViewById(R.id.doisPontosB)
        val bTLivreTimeB: Button = findViewById(R.id.tiroLivreB)
        val bEncerrarQuarto: Button = findViewById(R.id.encerrarQuarto)
        val bDesfazer: Button = findViewById(R.id.desfazerPonto)
        bTresPontosTimeA.isEnabled = true
        bDoisPontosTimeA.isEnabled = true
        bTLivreTimeA.isEnabled = true
        bTresPontosTimeB.isEnabled = true
        bDoisPontosTimeB.isEnabled = true
        bTLivreTimeB.isEnabled = true
        bEncerrarQuarto.isEnabled = true
        bDesfazer.isEnabled = true

        ultimosPontos = 0
        ultimoTime = ""

    }

    fun desfazerUltimoPonto() {
        if (ultimosPontos == 0) {
            Toast.makeText(this, "Nada para desfazer", Toast.LENGTH_SHORT).show()
            return
        }

        if (ultimoTime == "A") {
            pontuacaoTimeA -= ultimosPontos
            atualizaPlacar("A")
        } else {
            pontuacaoTimeB -= ultimosPontos
            atualizaPlacar("B")
        }

        Toast.makeText(this, "Último ponto desfeito", Toast.LENGTH_SHORT).show()

        ultimosPontos = 0
        ultimoTime = ""
    }

    fun encerrarQuarto() {
        val resultado = "$quarto º Quarto: Time A $pontuacaoTimeA x $pontuacaoTimeB Time B"
        tHistorico.setText(tHistorico.text.toString() + resultado + "\n")

        when {
            quarto < 4 -> {
                quarto++
                tQuarto.setText("$quarto º Quarto")
            }
            else -> {
                tQuarto.setText("Partida Encerrada!")
                val bTresPontosTimeA: Button = findViewById(R.id.tresPontosA)
                val bDoisPontosTimeA: Button = findViewById(R.id.doisPontosA)
                val bTLivreTimeA: Button = findViewById(R.id.tiroLivreA)
                val bTresPontosTimeB: Button = findViewById(R.id.tresPontosB)
                val bDoisPontosTimeB: Button = findViewById(R.id.doisPontosB)
                val bTLivreTimeB: Button = findViewById(R.id.tiroLivreB)
                val bEncerrarQuarto: Button = findViewById(R.id.encerrarQuarto)
                val bDesfazer: Button = findViewById(R.id.desfazerPonto)
                bTresPontosTimeA.isEnabled = false
                bDoisPontosTimeA.isEnabled = false
                bTLivreTimeA.isEnabled = false
                bTresPontosTimeB.isEnabled = false
                bDoisPontosTimeB.isEnabled = false
                bTLivreTimeB.isEnabled = false
                bEncerrarQuarto.isEnabled = false
                bDesfazer.isEnabled = false
            }
        }
    }
}
