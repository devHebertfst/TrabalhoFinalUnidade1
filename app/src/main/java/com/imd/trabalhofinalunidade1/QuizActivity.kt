package com.imd.trabalhofinalunidade1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {

    private data class Question(
        val categoria: String,
        val enunciado: String,
        val alternativas: List<String>,
        val correta: String
    )

    private lateinit var tvCategoria: TextView
    private lateinit var tvPergunta: TextView
    private lateinit var tvProgresso: TextView
    private lateinit var tvPontuacao: TextView
    private lateinit var tvStatus: TextView

    private lateinit var btnOpcao1: Button
    private lateinit var btnOpcao2: Button
    private lateinit var btnOpcao3: Button
    private lateinit var btnOpcao4: Button
    private lateinit var btnProxima: Button
    private lateinit var btnReiniciar: Button

    private lateinit var txtProgressBar: ProgressBar

    private lateinit var time : CountDownTimer

    private lateinit var textTime : TextView

    private val bancoPerguntas = listOf(
        Question(
            categoria = "Geografia",
            enunciado = "Qual e a capital do Brasil?",
            alternativas = listOf("Rio de Janeiro", "Brasilia", "Sao Paulo", "Salvador"),
            correta = "Brasilia"
        ),
        Question(
            categoria = "Matematica",
            enunciado = "Quanto e 7 x 8?",
            alternativas = listOf("54", "56", "58", "64"),
            correta = "56"
        ),
        Question(
            categoria = "Matematica",
            enunciado = "Quanto é 10X10?",
            alternativas = listOf("1000", "10", "35", "100"),
            correta = "100"
        ),
        Question(
            categoria = "Ciencia",
            enunciado = "Qual planeta e conhecido como planeta vermelho?",
            alternativas = listOf("Venus", "Marte", "Jupiter", "Saturno"),
            correta = "Marte"
        )
    )

    private var perguntas = emptyList<Question>()
    private var categoriasSelecionadas = emptyList<String>()

    private var indiceAtual = 0
    private var pontuacao = 0
    private var respondeuAtual = false
    private var qtdPerguntas = 0

    private var timeInicio: Long = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        tvCategoria = findViewById(R.id.txtCategoriaQuiz)
        tvPergunta = findViewById(R.id.txtPerguntaQuiz)
        tvProgresso = findViewById(R.id.txtProgressoQuiz)
        tvPontuacao = findViewById(R.id.txtPontuacaoQuiz)
        tvStatus = findViewById(R.id.txtStatusQuiz)
        txtProgressBar = findViewById(R.id.txtProgressBar)
        textTime = findViewById(R.id.textTime)

        btnOpcao1 = findViewById(R.id.btnOpcao1)
        btnOpcao2 = findViewById(R.id.btnOpcao2)
        btnOpcao3 = findViewById(R.id.btnOpcao3)
        btnOpcao4 = findViewById(R.id.btnOpcao4)
        btnProxima = findViewById(R.id.btnProximaPergunta)
        btnReiniciar = findViewById(R.id.btnReiniciarQuiz)

        categoriasSelecionadas = intent.getStringArrayListExtra("categorias") ?: emptyList()
        perguntas = filtrarPerguntas(categoriasSelecionadas)

        qtdPerguntas = perguntas.size
        txtProgressBar.max=qtdPerguntas

        iniciarTime()

        val botoesOpcoes = listOf(btnOpcao1, btnOpcao2, btnOpcao3, btnOpcao4)
        botoesOpcoes.forEach { botao ->
            botao.setOnClickListener { responder(botao.text.toString()) }
        }

        btnProxima.setOnClickListener { avancarPergunta() }
        btnReiniciar.setOnClickListener { reiniciarQuiz() }
        findViewById<Button>(R.id.btnVoltarCentralQuiz).setOnClickListener { voltarParaCentral() }

        if (savedInstanceState != null) {
            indiceAtual = savedInstanceState.getInt("indiceAtual", 0)
            pontuacao = savedInstanceState.getInt("pontuacao", 0)
            respondeuAtual = savedInstanceState.getBoolean("respondeuAtual", false)
            categoriasSelecionadas = savedInstanceState.getStringArrayList("categoriasSelecionadas") ?: categoriasSelecionadas
            perguntas = filtrarPerguntas(categoriasSelecionadas)
        }

        mostrarPergunta()
    }

    private fun mostrarPergunta() {
        if (perguntas.isEmpty()) {
            tvCategoria.text = "Categorias: nenhuma"
            tvPergunta.text = "Nenhuma pergunta disponivel"
            tvProgresso.text = "Pergunta 0 de 0"
            tvPontuacao.text = "Pontuacao: 0"
            tvStatus.text = "Adicione perguntas para esta categoria"
            listOf(btnOpcao1, btnOpcao2, btnOpcao3, btnOpcao4).forEach { it.isEnabled = false }
            btnProxima.isEnabled = false
            return
        }

        val pergunta = perguntas[indiceAtual]

        tvCategoria.text = "Categorias: ${categoriasSelecionadas.joinToString(", ")}"
        tvPergunta.text = pergunta.enunciado
        tvProgresso.text = "Pergunta ${indiceAtual + 1} de ${perguntas.size}"
        tvPontuacao.text = "Pontuacao: $pontuacao"
        if (!respondeuAtual) {
            tvStatus.text = "Selecione uma alternativa"
        }

        val botoes = listOf(btnOpcao1, btnOpcao2, btnOpcao3, btnOpcao4)
        botoes.forEachIndexed { index, button ->
            button.text = pergunta.alternativas[index]
            button.isEnabled = !respondeuAtual
        }

        btnProxima.isEnabled = respondeuAtual
    }

    private fun responder(respostaEscolhida: String) {
        if (respondeuAtual) return

        time.cancel()

        val perguntaAtual = perguntas[indiceAtual]
        respondeuAtual = true

        if (respostaEscolhida == perguntaAtual.correta) {
            pontuacao++
            tvStatus.text = "Resposta correta"
        } else {
            tvStatus.text = "Resposta errada. Correta: ${perguntaAtual.correta}"
        }

        atualizarProgresso(indiceAtual+1)

        mostrarPergunta()
    }

    private fun avancarPergunta() {

        if (!respondeuAtual) {
            tvStatus.text = "Selecione uma alternativa antes de continuar"
            return
        }

        if (indiceAtual < perguntas.lastIndex) {
            indiceAtual++
            respondeuAtual = false
            mostrarPergunta()
            time.cancel()
            atualizarTime()
        } else {
            time.cancel()
            tvStatus.text = "Quiz finalizado. Pontuacao: $pontuacao de ${perguntas.size}"
            btnProxima.isEnabled = false
            listOf(btnOpcao1, btnOpcao2, btnOpcao3, btnOpcao4).forEach { it.isEnabled = false }
        }
    }

    private fun reiniciarQuiz() {
        indiceAtual = 0
        pontuacao = 0
        respondeuAtual = false
        perguntas = filtrarPerguntas(categoriasSelecionadas)
        mostrarPergunta()
        tvStatus.text = "Quiz reiniciado"
        txtProgressBar.progress = 0
        iniciarTime()
    }

    private fun voltarParaCentral() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("indiceAtual", indiceAtual)
        outState.putInt("pontuacao", pontuacao)
        outState.putBoolean("respondeuAtual", respondeuAtual)
        outState.putStringArrayList("categoriasSelecionadas", ArrayList(categoriasSelecionadas))
    }

    private fun filtrarPerguntas(categorias: List<String>): List<Question> {
        return if (categorias.isEmpty()) {
            bancoPerguntas
        } else {
            bancoPerguntas.filter { it.categoria in categorias }
        }
    }

    private fun atualizarProgresso(perguntaAtual: Int) {
        txtProgressBar.progress = perguntaAtual
    }

    private fun iniciarTime() {
         timeInicio = System.currentTimeMillis()

        atualizarTime()
    }

    private fun atualizarTime() {
        time = object : CountDownTimer(30000L, 1000L) {
            override fun onFinish() {
                avancarPergunta()
            }

            override fun onTick(millisUntilFinished: Long) {
                val segundos = millisUntilFinished / 1000
                textTime.text = segundos.toString()
            }
        }
        time.start()
    }
}
