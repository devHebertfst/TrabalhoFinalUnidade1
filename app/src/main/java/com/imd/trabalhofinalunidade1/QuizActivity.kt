package com.imd.trabalhofinalunidade1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

    private lateinit var txtProgressBar: ProgressBar
    private lateinit var time: CountDownTimer
    private lateinit var textTime: TextView
    private lateinit var textTempoTotal: TextView

    private val bancoPerguntas = geografiaQuestions() +
        historiaQuestions() +
        cienciaQuestions() +
        matematicaQuestions() +
        portuguesQuestions() +
        esportesQuestions() +
        tecnologiaQuestions() +
        entretenimentoQuestions()

    private var perguntas = emptyList<Question>()
    private var categoriasSelecionadas = emptyList<String>()

    private var indiceAtual = 0
    private var pontuacao = 0
    private var respondeuAtual = false
    private var dialogAberto = false
    private var timeoutPendente = false
    private var quizInicioMs = 0L
    private var dialogConfirmacaoSaida: AlertDialog? = null
    private val tempoTotalHandler = Handler(Looper.getMainLooper())
    private val tempoTotalRunnable = object : Runnable {
        override fun run() {
            atualizarTempoTotal()
            tempoTotalHandler.postDelayed(this, 1000L)
        }
    }

    @SuppressLint("MissingInflatedId")
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
        textTime = findViewById(R.id.textTime)
        textTempoTotal = findViewById(R.id.textTempoTotalQuiz)

        btnOpcao1 = findViewById(R.id.btnOpcao1)
        btnOpcao2 = findViewById(R.id.btnOpcao2)
        btnOpcao3 = findViewById(R.id.btnOpcao3)
        btnOpcao4 = findViewById(R.id.btnOpcao4)
        btnProxima = findViewById(R.id.btnProximaPergunta)

        categoriasSelecionadas = intent.getStringArrayListExtra("categorias") ?: emptyList()
        perguntas = filtrarPerguntas(categoriasSelecionadas)
        txtProgressBar.max = perguntas.size

        val botoesOpcoes = listOf(btnOpcao1, btnOpcao2, btnOpcao3, btnOpcao4)
        botoesOpcoes.forEach { botao ->
            botao.setOnClickListener { responder(botao.text.toString()) }
        }

        btnProxima.setOnClickListener { avancarPergunta() }
        findViewById<Button>(R.id.btnVoltarCentralQuiz).setOnClickListener { confirmarVoltarParaMenu() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmarVoltarParaMenu()
            }
        })

        if (savedInstanceState != null) {
            indiceAtual = savedInstanceState.getInt("indiceAtual", 0)
            pontuacao = savedInstanceState.getInt("pontuacao", 0)
            respondeuAtual = savedInstanceState.getBoolean("respondeuAtual", false)
            quizInicioMs = savedInstanceState.getLong("quizInicioMs", 0L)
            categoriasSelecionadas =
                savedInstanceState.getStringArrayList("categoriasSelecionadas") ?: categoriasSelecionadas
            perguntas = filtrarPerguntas(categoriasSelecionadas)
            txtProgressBar.max = perguntas.size
        }

        iniciarTime()
        mostrarPergunta()
    }

    private fun mostrarPergunta() {
        if (perguntas.isEmpty()) {
            tvCategoria.text = "Categorias: nenhuma"
            tvPergunta.text = "Nenhuma pergunta disponível"
            tvProgresso.text = "Pergunta 0 de 0"
            tvPontuacao.text = "Pontuação: 0"
            tvStatus.text = "Adicione perguntas para esta categoria"
            listOf(btnOpcao1, btnOpcao2, btnOpcao3, btnOpcao4).forEach { it.isEnabled = false }
            btnProxima.isEnabled = false
            return
        }

        val pergunta = perguntas[indiceAtual]
        tvCategoria.text = "Categorias: ${categoriasSelecionadas.joinToString(", ")}"
        tvPergunta.text = "${pergunta.enunciado}\n\nNível: ${pergunta.nivel}"
        tvProgresso.text = "Pergunta ${indiceAtual + 1} de ${perguntas.size}"
        tvPontuacao.text = "Pontuação: $pontuacao"

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

        atualizarProgresso(indiceAtual + 1)
        mostrarPergunta()
    }

    private fun tratarTempoEncerrado() {
        if (respondeuAtual || perguntas.isEmpty()) return

        if (dialogAberto) {
            timeoutPendente = true
            textTime.text = "0 segundos"
            return
        }

        val perguntaAtual = perguntas[indiceAtual]
        respondeuAtual = true
        timeoutPendente = false
        tvStatus.text = "Tempo encerrado. Correta: ${perguntaAtual.correta}"
        atualizarProgresso(indiceAtual + 1)
        mostrarPergunta()

        val textoBotao = if (indiceAtual < perguntas.lastIndex) {
            "Próxima pergunta"
        } else {
            "Ver resultado"
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_quiz_timeout, null)
        dialogView.findViewById<TextView>(R.id.txtDialogRespostaCorreta).text = perguntaAtual.correta
        dialogView.findViewById<TextView>(R.id.txtDialogPergunta).text = "Pergunta: ${perguntaAtual.enunciado}"
        dialogView.findViewById<Button>(R.id.btnDialogProximaPergunta).text = textoBotao

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogAberto = true

        dialog.setOnShowListener {
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        dialog.setOnDismissListener {
            dialogAberto = false
        }

        dialogView.findViewById<Button>(R.id.btnDialogProximaPergunta).setOnClickListener {
            dialog.dismiss()
            avancarPergunta()
        }

        dialog.show()
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
            finalizarQuiz()
        }
    }

    private fun voltarParaMenu() {
        if (::time.isInitialized) {
            time.cancel()
        }
        val intent = Intent(this, QuizMenuActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
        finish()
    }

    private fun confirmarVoltarParaMenu() {
        if (dialogConfirmacaoSaida?.isShowing == true) return

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_quiz_exit, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.setOnShowListener {
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        dialog.setOnDismissListener {
            dialogAberto = false
            dialogConfirmacaoSaida = null
        }

        dialogView.findViewById<Button>(R.id.btnDialogContinuarQuiz).setOnClickListener {
            dialog.dismiss()
            window.decorView.post {
                if (timeoutPendente) {
                    tratarTempoEncerrado()
                }
            }
        }

        dialogView.findViewById<Button>(R.id.btnDialogVoltarMenu).setOnClickListener {
            dialog.dismiss()
            voltarParaMenu()
        }

        dialogConfirmacaoSaida = dialog
        dialog.show()
        dialogAberto = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("indiceAtual", indiceAtual)
        outState.putInt("pontuacao", pontuacao)
        outState.putBoolean("respondeuAtual", respondeuAtual)
        outState.putLong("quizInicioMs", quizInicioMs)
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

    private fun iniciarTime() {
        if (quizInicioMs == 0L) {
            quizInicioMs = SystemClock.elapsedRealtime()
        }
        iniciarTempoTotal()
        atualizarTime()
    }

    private fun iniciarTempoTotal() {
        atualizarTempoTotal()
        tempoTotalHandler.removeCallbacks(tempoTotalRunnable)
        tempoTotalHandler.postDelayed(tempoTotalRunnable, 1000L)
    }

    private fun atualizarTempoTotal() {
        val tempoDecorrido = SystemClock.elapsedRealtime() - quizInicioMs
        val totalSegundos = (tempoDecorrido / 1000).toInt()
        val minutos = totalSegundos / 60
        val segundos = totalSegundos % 60
        textTempoTotal.text = String.format("  Total %02d:%02d", minutos, segundos)
    }

    private fun atualizarTime() {
        time = object : CountDownTimer(30000L, 1000L) {
            override fun onFinish() {
                textTime.text = "0 segundos"
                tratarTempoEncerrado()
            }

            override fun onTick(millisUntilFinished: Long) {
                val segundos = millisUntilFinished / 1000
                textTime.text = " $segundos segundos"
            }
        }
        time.start()
    }

    override fun onDestroy() {
        if (::time.isInitialized) {
            time.cancel()
        }
        tempoTotalHandler.removeCallbacks(tempoTotalRunnable)
        super.onDestroy()
    }

    private fun finalizarQuiz() {
        if (::time.isInitialized) {
            time.cancel()
        }

        val tempoTotalMs = SystemClock.elapsedRealtime() - quizInicioMs
        val intent = Intent(this, QuizResultadoActivity::class.java).apply {
            putExtra(QuizResultadoActivity.EXTRA_PONTUACAO, pontuacao)
            putExtra(QuizResultadoActivity.EXTRA_TOTAL_PERGUNTAS, perguntas.size)
            putExtra(QuizResultadoActivity.EXTRA_TEMPO_TOTAL_MS, tempoTotalMs)
            putStringArrayListExtra(
                QuizResultadoActivity.EXTRA_CATEGORIAS,
                ArrayList(categoriasSelecionadas)
            )
        }
        startActivity(intent)
        finish()
    }

    private fun geografiaQuestions() = listOf(
        Question("Geografia", "Qual é a capital do Brasil?", listOf("Rio de Janeiro", "Brasília", "São Paulo", "Salvador"), "Brasília", "Fácil"),
        Question("Geografia", "Qual é o maior oceano do mundo?", listOf("Atlântico", "Índico", "Pacífico", "Ártico"), "Pacífico", "Fácil"),
        Question("Geografia", "Em qual continente fica o Egito?", listOf("Ásia", "África", "Europa", "América"), "África", "Fácil"),
        Question("Geografia", "Qual país é famoso pelo formato de bota?", listOf("Portugal", "Chile", "Itália", "México"), "Itália", "Fácil"),
        Question("Geografia", "Qual é o menor país do mundo?", listOf("Mônaco", "Vaticano", "Malta", "Luxemburgo"), "Vaticano", "Médio"),
        Question("Geografia", "Qual é o rio mais extenso do mundo?", listOf("Nilo", "Amazonas", "Mississipi", "Yangtzé"), "Amazonas", "Médio"),
        Question("Geografia", "Qual é a capital da Austrália?", listOf("Sydney", "Melbourne", "Camberra", "Perth"), "Camberra", "Médio"),
        Question("Geografia", "Qual linha imaginária divide a Terra em hemisfério norte e sul?", listOf("Trópico de Câncer", "Meridiano de Greenwich", "Linha do Equador", "Trópico de Capricórnio"), "Linha do Equador", "Médio"),
        Question("Geografia", "Qual é a capital da Mongólia?", listOf("Astana", "Ulan Bator", "Tashkent", "Bishkek"), "Ulan Bator", "Difícil"),
        Question("Geografia", "Qual deserto é o maior do mundo?", listOf("Saara", "Gobi", "Antártico", "Kalahari"), "Antártico", "Difícil"),
        Question("Geografia", "Qual país possui o maior número de fusos horários?", listOf("Estados Unidos", "China", "Rússia", "Canadá"), "Rússia", "Difícil"),
        Question("Geografia", "Em qual continente está localizada a Cordilheira dos Andes?", listOf("Ásia", "Europa", "América do Sul", "África"), "América do Sul", "Difícil")
    )

    private fun historiaQuestions() = listOf(
        Question("História", "Quem descobriu o Brasil?", listOf("Dom Pedro I", "Pedro Álvares Cabral", "Tiradentes", "Getúlio Vargas"), "Pedro Álvares Cabral", "Fácil"),
        Question("História", "Em que ano o Brasil declarou independência?", listOf("1500", "1822", "1889", "1930"), "1822", "Fácil"),
        Question("História", "Quem assinou a Lei Áurea?", listOf("Princesa Isabel", "Dom Pedro II", "Deodoro da Fonseca", "Anita Garibaldi"), "Princesa Isabel", "Fácil"),
        Question("História", "Quem proclamou a República no Brasil?", listOf("Tiradentes", "Getúlio Vargas", "Deodoro da Fonseca", "Juscelino Kubitschek"), "Deodoro da Fonseca", "Fácil"),
        Question("História", "Em que ano ocorreu a Proclamação da República no Brasil?", listOf("1889", "1822", "1500", "1930"), "1889", "Médio"),
        Question("História", "Quem foi o primeiro presidente do Brasil?", listOf("Getúlio Vargas", "Deodoro da Fonseca", "Juscelino Kubitschek", "Lula"), "Deodoro da Fonseca", "Médio"),
        Question("História", "Qual civilização construiu as pirâmides de Gizé?", listOf("Romanos", "Egípcios", "Maias", "Persas"), "Egípcios", "Médio"),
        Question("História", "Qual navegador liderou a primeira viagem ao redor do mundo?", listOf("Cristóvão Colombo", "Fernão de Magalhães", "Vasco da Gama", "Américo Vespúcio"), "Fernão de Magalhães", "Médio"),
        Question("História", "Qual tratado encerrou a Primeira Guerra Mundial?", listOf("Tratado de Paris", "Tratado de Versalhes", "Tratado de Tordesilhas", "Tratado de Utrecht"), "Tratado de Versalhes", "Difícil"),
        Question("História", "Quem foi o líder da União Soviética durante a Segunda Guerra Mundial?", listOf("Lenin", "Stalin", "Khrushchev", "Trotsky"), "Stalin", "Difícil"),
        Question("História", "Em que ano caiu o Muro de Berlim?", listOf("1985", "1989", "1991", "1994"), "1989", "Difícil"),
        Question("História", "Qual imperador romano adotou o cristianismo e convocou o Concílio de Niceia?", listOf("Júlio César", "Nero", "Constantino", "Trajano"), "Constantino", "Difícil")
    )

    private fun cienciaQuestions() = listOf(
        Question("Ciência", "Qual planeta é conhecido como planeta vermelho?", listOf("Vênus", "Marte", "Júpiter", "Saturno"), "Marte", "Fácil"),
        Question("Ciência", "A água ferve a quantos graus Celsius?", listOf("90", "100", "80", "120"), "100", "Fácil"),
        Question("Ciência", "Qual órgão humano é responsável pela respiração?", listOf("Fígado", "Pulmão", "Coração", "Estômago"), "Pulmão", "Fácil"),
        Question("Ciência", "Qual estrela está mais próxima da Terra?", listOf("Lua", "Marte", "Sol", "Vênus"), "Sol", "Fácil"),
        Question("Ciência", "Qual é o elemento químico representado por 'O'?", listOf("Ouro", "Oxigênio", "Ósmio", "Oganessônio"), "Oxigênio", "Médio"),
        Question("Ciência", "Quantos ossos tem o corpo humano adulto?", listOf("206", "210", "180", "250"), "206", "Médio"),
        Question("Ciência", "Qual gás as plantas absorvem no processo de fotossíntese?", listOf("Oxigênio", "Hélio", "Gás carbônico", "Nitrogênio"), "Gás carbônico", "Médio"),
        Question("Ciência", "Como se chama a passagem do estado líquido para o gasoso?", listOf("Condensação", "Sublimação", "Evaporação", "Solidificação"), "Evaporação", "Médio"),
        Question("Ciência", "Qual partícula subatômica possui carga negativa?", listOf("Próton", "Nêutron", "Elétron", "Quark"), "Elétron", "Difícil"),
        Question("Ciência", "Qual é a fórmula química do gás carbônico?", listOf("CO", "CO2", "O2", "CH4"), "CO2", "Difícil"),
        Question("Ciência", "Qual estrutura carrega a informação genética dos seres vivos?", listOf("RNA", "Proteína", "DNA", "Enzima"), "DNA", "Difícil"),
        Question("Ciência", "Como se chama a força que atrai os corpos para o centro da Terra?", listOf("Magnetismo", "Gravidade", "Pressão", "Inércia"), "Gravidade", "Difícil")
    )

    private fun matematicaQuestions() = listOf(
        Question("Matemática", "Quanto é 2 + 2?", listOf("3", "4", "5", "6"), "4", "Fácil"),
        Question("Matemática", "Quanto é 10 - 7?", listOf("2", "3", "4", "5"), "3", "Fácil"),
        Question("Matemática", "Quanto é 3 × 3?", listOf("6", "9", "12", "15"), "9", "Fácil"),
        Question("Matemática", "Quanto é 12 ÷ 4?", listOf("2", "3", "4", "6"), "3", "Fácil"),
        Question("Matemática", "Quanto é 15 × 3?", listOf("30", "35", "45", "50"), "45", "Médio"),
        Question("Matemática", "Qual fração representa a metade?", listOf("1/3", "1/4", "1/2", "2/3"), "1/2", "Médio"),
        Question("Matemática", "Qual é a área de um retângulo de 5 por 4?", listOf("9", "18", "20", "25"), "20", "Médio"),
        Question("Matemática", "Qual é a média de 6, 8 e 10?", listOf("7", "8", "9", "10"), "8", "Médio"),
        Question("Matemática", "Qual é o valor de π (pi) aproximadamente?", listOf("2,14", "3,14", "4,13", "3,41"), "3,14", "Difícil"),
        Question("Matemática", "Qual é a solução de x + 7 = 15?", listOf("6", "7", "8", "9"), "8", "Difícil"),
        Question("Matemática", "Quanto é 25% de 200?", listOf("25", "40", "50", "75"), "50", "Difícil"),
        Question("Matemática", "Qual é a raiz quadrada de 144?", listOf("10", "11", "12", "13"), "12", "Difícil")
    )

    private fun portuguesQuestions() = listOf(
        Question("Português", "Qual é o plural de 'cão'?", listOf("cãos", "cães", "cões", "caninos"), "cães", "Fácil"),
        Question("Português", "Qual palavra é sinônimo de 'feliz'?", listOf("Triste", "Contente", "Bravo", "Cansado"), "Contente", "Fácil"),
        Question("Português", "Qual é o antônimo de 'claro'?", listOf("Escuro", "Fraco", "Bonito", "Raso"), "Escuro", "Fácil"),
        Question("Português", "Quantas sílabas tem a palavra 'casa'?", listOf("1", "2", "3", "4"), "2", "Fácil"),
        Question("Português", "Qual figura de linguagem é usada em 'o vento sussurrava'?", listOf("Metáfora", "Personificação", "Hipérbole", "Ironia"), "Personificação", "Médio"),
        Question("Português", "Qual frase está com pontuação correta?", listOf("Vamos embora João.", "Vamos embora, João.", "Vamos, embora João.", "Vamos embora João,"), "Vamos embora, João.", "Médio"),
        Question("Português", "Em 'os alunos estudiosos passaram', a palavra 'estudiosos' é um:", listOf("Substantivo", "Verbo", "Adjetivo", "Pronome"), "Adjetivo", "Médio"),
        Question("Português", "Qual opção está escrita corretamente?", listOf("Excessão", "Exceção", "Excessao", "Exsesão"), "Exceção", "Médio"),
        Question("Português", "Qual é o sujeito oculto na frase 'Fui ao mercado'?", listOf("Eu", "Ele", "Nós", "Eles"), "Eu", "Difícil"),
        Question("Português", "Em qual frase o uso da crase está correto?", listOf("Vou a escola cedo.", "Entreguei o livro à professora.", "Cheguei a tarde.", "Voltei à pé."), "Entreguei o livro à professora.", "Difícil"),
        Question("Português", "Qual oração está na voz passiva?", listOf("Maria comprou o pão.", "O pão foi comprado por Maria.", "Maria está comprando pão.", "Maria comprará o pão."), "O pão foi comprado por Maria.", "Difícil"),
        Question("Português", "Na frase 'Quando cheguei, eles já tinham saído', a expressão 'Quando cheguei' indica:", listOf("Causa", "Tempo", "Condição", "Finalidade"), "Tempo", "Difícil")
    )

    private fun esportesQuestions() = listOf(
        Question("Esportes", "Quantos jogadores um time de futebol tem em campo?", listOf("9", "10", "11", "12"), "11", "Fácil"),
        Question("Esportes", "Quantos pontos vale um tiro livre no basquete?", listOf("1", "2", "3", "4"), "1", "Fácil"),
        Question("Esportes", "Quantos jogadores cada equipe tem em quadra no vôlei?", listOf("5", "6", "7", "8"), "6", "Fácil"),
        Question("Esportes", "De quantos em quantos anos acontecem os Jogos Olímpicos?", listOf("2", "3", "4", "5"), "4", "Fácil"),
        Question("Esportes", "Em que país nasceu o futebol moderno?", listOf("Brasil", "Espanha", "Inglaterra", "Itália"), "Inglaterra", "Médio"),
        Question("Esportes", "Qual piloto brasileiro ficou famoso com três títulos mundiais na Fórmula 1?", listOf("Rubens Barrichello", "Felipe Massa", "Ayrton Senna", "Nelson Piquet Filho"), "Ayrton Senna", "Médio"),
        Question("Esportes", "No tênis, quantos games um tenista precisa vencer normalmente para fechar um set?", listOf("4", "5", "6", "7"), "6", "Médio"),
        Question("Esportes", "Em qual esporte se usa uma rede, uma peteca e uma raquete?", listOf("Tênis", "Badminton", "Squash", "Beisebol"), "Badminton", "Médio"),
        Question("Esportes", "Quantas Copas do Mundo o Brasil venceu até 2022?", listOf("4", "5", "6", "7"), "5", "Difícil"),
        Question("Esportes", "Qual esporte utiliza o termo 'scrum'?", listOf("Rugby", "Hóquei", "Polo aquático", "Handebol"), "Rugby", "Difícil"),
        Question("Esportes", "Qual atleta conquistou 23 medalhas de ouro olímpicas?", listOf("Usain Bolt", "Michael Phelps", "Carl Lewis", "Simone Biles"), "Michael Phelps", "Difícil"),
        Question("Esportes", "No futebol, como se chama a infração em que o atacante recebe a bola à frente da linha defensiva adversária?", listOf("Tiro de meta", "Escanteio", "Impedimento", "Pênalti"), "Impedimento", "Difícil")
    )

    private fun tecnologiaQuestions() = listOf(
        Question("Tecnologia", "Qual empresa criou o iPhone?", listOf("Samsung", "Apple", "Microsoft", "Google"), "Apple", "Fácil"),
        Question("Tecnologia", "Qual dispositivo é usado para mover o cursor na tela do computador?", listOf("Teclado", "Mouse", "Monitor", "Scanner"), "Mouse", "Fácil"),
        Question("Tecnologia", "Quais números formam o sistema binário?", listOf("0 e 1", "1 e 2", "2 e 3", "5 e 10"), "0 e 1", "Fácil"),
        Question("Tecnologia", "Qual empresa desenvolve o sistema Android?", listOf("Apple", "Google", "Intel", "Sony"), "Google", "Fácil"),
        Question("Tecnologia", "O que significa 'HTTP'?", listOf("HyperText Transfer Protocol", "High Tech Transfer Process", "Hyper Transfer Text Program", "Home Tool Transfer Protocol"), "HyperText Transfer Protocol", "Médio"),
        Question("Tecnologia", "O que é computação em nuvem?", listOf("Guardar dados apenas no celular", "Usar recursos de servidores pela internet", "Imprimir arquivos pela rede", "Criar jogos em 3D"), "Usar recursos de servidores pela internet", "Médio"),
        Question("Tecnologia", "O que significa a sigla RAM?", listOf("Random Access Memory", "Remote Access Machine", "Readable Application Module", "Rapid Action Monitor"), "Random Access Memory", "Médio"),
        Question("Tecnologia", "Para que serve o HTML?", listOf("Criar a estrutura de páginas web", "Editar fotos", "Montar planilhas", "Compactar arquivos"), "Criar a estrutura de páginas web", "Médio"),
        Question("Tecnologia", "Qual linguagem é usada principalmente para desenvolvimento Android nativo atualmente?", listOf("Java", "Kotlin", "Swift", "Python"), "Kotlin", "Difícil"),
        Question("Tecnologia", "Qual ferramenta é um sistema de controle de versão distribuído?", listOf("Figma", "Git", "Photoshop", "MySQL"), "Git", "Difícil"),
        Question("Tecnologia", "Qual linguagem é mais usada para consultar bancos de dados relacionais?", listOf("HTML", "CSS", "SQL", "JSON"), "SQL", "Difícil"),
        Question("Tecnologia", "O que significa API?", listOf("Application Programming Interface", "Automated Program Integration", "Advanced Process Instruction", "Applied Protocol Internet"), "Application Programming Interface", "Difícil")
    )

    private fun entretenimentoQuestions() = listOf(
        Question("Entretenimento", "Qual personagem usa um chapéu vermelho e é encanador?", listOf("Luigi", "Mario", "Sonic", "Link"), "Mario", "Fácil"),
        Question("Entretenimento", "Em qual escola Harry Potter estuda?", listOf("Nárnia", "Hogwarts", "Xavier", "Nevermore"), "Hogwarts", "Fácil"),
        Question("Entretenimento", "Qual cowboy é um dos protagonistas de Toy Story?", listOf("Buzz", "Woody", "Shrek", "Relâmpago McQueen"), "Woody", "Fácil"),
        Question("Entretenimento", "Qual personagem tem uma irmã chamada Elsa?", listOf("Moana", "Anna", "Rapunzel", "Merida"), "Anna", "Fácil"),
        Question("Entretenimento", "Quem dirigiu o filme Titanic?", listOf("Steven Spielberg", "James Cameron", "Christopher Nolan", "Peter Jackson"), "James Cameron", "Médio"),
        Question("Entretenimento", "Qual personagem é o professor de química em Breaking Bad?", listOf("Jesse Pinkman", "Saul Goodman", "Walter White", "Hank Schrader"), "Walter White", "Médio"),
        Question("Entretenimento", "Qual prêmio é considerado o mais famoso do cinema?", listOf("Grammy", "Tony", "Oscar", "Emmy"), "Oscar", "Médio"),
        Question("Entretenimento", "Qual herói da Marvel tem o alter ego Tony Stark?", listOf("Capitão América", "Homem de Ferro", "Thor", "Gavião Arqueiro"), "Homem de Ferro", "Médio"),
        Question("Entretenimento", "Qual diretor japonês é um dos fundadores do Studio Ghibli?", listOf("Akira Kurosawa", "Hayao Miyazaki", "Mamoru Hosoda", "Makoto Shinkai"), "Hayao Miyazaki", "Difícil"),
        Question("Entretenimento", "Quem escreveu a trilogia O Senhor dos Anéis?", listOf("George R. R. Martin", "C. S. Lewis", "J. R. R. Tolkien", "Neil Gaiman"), "J. R. R. Tolkien", "Difícil"),
        Question("Entretenimento", "Qual foi o primeiro filme do Universo Cinematográfico da Marvel?", listOf("Thor", "Homem de Ferro", "Capitão América", "Hulk"), "Homem de Ferro", "Difícil"),
        Question("Entretenimento", "Qual filme venceu o Oscar de Melhor Filme em 2020 e foi o primeiro em língua não inglesa a conseguir isso?", listOf("1917", "Coringa", "Parasita", "Era uma Vez em Hollywood"), "Parasita", "Difícil")
    )
}
