package com.imd.trabalhofinalunidade1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class QuizActivity : AppCompatActivity() {

    private data class Question(
        val categoria: String,
        val enunciado: String,
        val alternativas: List<String>,
        val correta: String,
        val nivel: String
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

    private val bancoPerguntas = listOf(

        // ===== FÁCIL =====
        Question("Geografia", "Qual a capital do Brasil?", listOf("Rio de Janeiro", "Brasília", "São Paulo", "Salvador"), "Brasília", "Fácil"),
        Question("História", "Quem descobriu o Brasil?", listOf("Dom Pedro I", "Pedro Álvares Cabral", "Tiradentes", "Getúlio Vargas"), "Pedro Álvares Cabral", "Fácil"),
        Question("Ciência", "Qual planeta é conhecido como planeta vermelho?", listOf("Vênus", "Marte", "Júpiter", "Saturno"), "Marte", "Fácil"),
        Question("Matemática", "Quanto é 2 + 2?", listOf("3", "4", "5", "6"), "4", "Fácil"),
        Question("Português", "Qual é o plural de 'cão'?", listOf("cãos", "cães", "cões", "caninos"), "cães", "Fácil"),
        Question("Esportes", "Quantos jogadores um time de futebol tem em campo?", listOf("9", "10", "11", "12"), "11", "Fácil"),
        Question("Tecnologia", "Qual empresa criou o iPhone?", listOf("Samsung", "Apple", "Microsoft", "Google"), "Apple", "Fácil"),
        Question("Geografia", "Qual é o maior oceano do mundo?", listOf("Atlântico", "Índico", "Pacífico", "Ártico"), "Pacífico", "Fácil"),
        Question("Entretenimento", "Qual personagem usa um chapéu vermelho e é encanador?", listOf("Luigi", "Mario", "Sonic", "Link"), "Mario", "Fácil"),
        Question("Ciência", "A água ferve a quantos graus Celsius?", listOf("90", "100", "80", "120"), "100", "Fácil"),

        // ===== MÉDIO =====
        Question("História", "Em que ano ocorreu a Proclamação da República no Brasil?", listOf("1889", "1822", "1500", "1930"), "1889", "Médio"),
        Question("Geografia", "Qual é o menor país do mundo?", listOf("Mônaco", "Vaticano", "Malta", "Luxemburgo"), "Vaticano", "Médio"),
        Question("Ciência", "Qual é o elemento químico representado por 'O'?", listOf("Ouro", "Oxigênio", "Ósmio", "Oganessônio"), "Oxigênio", "Médio"),
        Question("Matemática", "Quanto é 15 × 3?", listOf("30", "35", "45", "50"), "45", "Médio"),
        Question("Português", "Qual figura de linguagem é usada em 'o vento sussurrava'?", listOf("Metáfora", "Personificação", "Hipérbole", "Ironia"), "Personificação", "Médio"),
        Question("Esportes", "Em que país nasceu o futebol moderno?", listOf("Brasil", "Espanha", "Inglaterra", "Itália"), "Inglaterra", "Médio"),
        Question("Tecnologia", "O que significa 'HTTP'?", listOf("HyperText Transfer Protocol", "High Tech Transfer Process", "Hyper Transfer Text Program", "Home Tool Transfer Protocol"), "HyperText Transfer Protocol", "Médio"),
        Question("Geografia", "Qual é o rio mais extenso do mundo?", listOf("Nilo", "Amazonas", "Mississipi", "Yangtzé"), "Amazonas", "Médio"),
        Question("História", "Quem foi o primeiro presidente do Brasil?", listOf("Getúlio Vargas", "Deodoro da Fonseca", "Juscelino Kubitschek", "Lula"), "Deodoro da Fonseca", "Médio"),
        Question("Ciência", "Quantos ossos tem o corpo humano adulto?", listOf("206", "210", "180", "250"), "206", "Médio"),

        // ===== DIFÍCIL =====
        Question("História", "Qual tratado encerrou a Primeira Guerra Mundial?", listOf("Tratado de Paris", "Tratado de Versalhes", "Tratado de Tordesilhas", "Tratado de Utrecht"), "Tratado de Versalhes", "Difícil"),
        Question("Geografia", "Qual é a capital da Mongólia?", listOf("Astana", "Ulan Bator", "Tashkent", "Bishkek"), "Ulan Bator", "Difícil"),
        Question("Ciência", "Qual partícula subatômica possui carga negativa?", listOf("Próton", "Nêutron", "Elétron", "Quark"), "Elétron", "Difícil"),
        Question("Matemática", "Qual é o valor de π (pi) aproximadamente?", listOf("2,14", "3,14", "4,13", "3,41"), "3,14", "Difícil"),
        Question("Português", "Qual é o sujeito oculto na frase 'Fui ao mercado'?", listOf("Eu", "Ele", "Nós", "Eles"), "Eu", "Difícil"),
        Question("Esportes", "Quantas Copas do Mundo o Brasil venceu até 2022?", listOf("4", "5", "6", "7"), "5", "Difícil"),
        Question("Tecnologia", "Qual linguagem é usada principalmente para desenvolvimento Android nativo atualmente?", listOf("Java", "Kotlin", "Swift", "Python"), "Kotlin", "Difícil"),
        Question("Geografia", "Qual deserto é o maior do mundo?", listOf("Saara", "Gobi", "Antártico", "Kalahari"), "Antártico", "Difícil"),
        Question("História", "Quem foi o líder da União Soviética durante a Segunda Guerra Mundial?", listOf("Lenin", "Stalin", "Khrushchev", "Trotsky"), "Stalin", "Difícil"),
        Question("Ciência", "Qual é a fórmula química do gás carbônico?", listOf("CO", "CO2", "O2", "CH4"), "CO2", "Difícil")
    )

    private var perguntas = emptyList<Question>()
    private var categoriasSelecionadas = emptyList<String>()

    private var indiceAtual = 0
    private var pontuacao = 0
    private var respondeuAtual = false
    private var qtdPerguntas = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_quiz)


        tvCategoria = findViewById(R.id.txtCategoriaQuiz)
        tvPergunta = findViewById(R.id.txtPerguntaQuiz)
        tvProgresso = findViewById(R.id.txtProgressoQuiz)
        tvPontuacao = findViewById(R.id.txtPontuacaoQuiz)
        tvStatus = findViewById(R.id.txtStatusQuiz)
        txtProgressBar = findViewById(R.id.txtProgressBar)

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
        tvPergunta.text = "${pergunta.enunciado}\n\nNível: ${pergunta.nivel}"
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
        } else {
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
        val filtradas = if (categorias.isEmpty()) {
            bancoPerguntas
        } else {
            bancoPerguntas.filter { it.categoria in categorias }
        }
        return filtradas.shuffled().take(10)
    }

    private fun atualizarProgresso(perguntaAtual: Int) {
        txtProgressBar.progress = perguntaAtual
    }
}
