package com.imd.trabalhofinalunidade1

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit
import android.util.Log

class QuizResultadoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("QUIZ", "ENTREI NA RESULTADO")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_resultado)

        val pontuacao = intent.getIntExtra("pontuacao", 0)
        val total = intent.getIntExtra("total", 0)
        val porcentagem = if (total > 0) (pontuacao * 100) / total else 0

        val tvMensagem       = findViewById<TextView>(R.id.tvMensagemResultado)
        val tvPontuacao      = findViewById<TextView>(R.id.tvPontuacaoResultado)
        val tvTotal          = findViewById<TextView>(R.id.tvTotalResultado)
        val tvAproveitamento = findViewById<TextView>(R.id.tvAproveitamento)
        val progressBar      = findViewById<ProgressBar>(R.id.progressBarResultado)
        val btnJogarNovamente = findViewById<Button>(R.id.btnJogarNovamente)
        val btnVoltar        = findViewById<Button>(R.id.btnVoltarCentralQuizMenu)
        val konfettiView     = findViewById<KonfettiView>(R.id.konfettiView)

        // Mensagem de acordo com desempenho
        tvMensagem.text = when {
            pontuacao == total          -> "Perfeito! Você é um gênio absoluto!"
            porcentagem >= 70           -> "Mandou muito bem! Continue assim!"
            porcentagem >= 40           -> "Nada mal! Continue treinando!"
            else                        -> "Bora estudar mais um pouco..."
        }


        tvTotal.text = "de $total"
        tvAproveitamento.text = "$porcentagem% de aproveitamento"

        animarContador(tvPontuacao, 0, pontuacao, 900)

        animarProgressBar(progressBar, 0, porcentagem, 1000)

        animarEntrada(tvMensagem,        0)
        animarEntrada(tvAproveitamento, 200)
        animarEntrada(progressBar,      350)
        animarEntrada(btnJogarNovamente,450)
        animarEntrada(btnVoltar,        550)

        if (porcentagem >= 40) {
            dispararConfete(konfettiView, porcentagem)
        }

        btnJogarNovamente.setOnClickListener { finish() }
        btnVoltar.setOnClickListener { finish() }
    }

    private fun animarContador(tv: TextView, start: Int, end: Int, duracao: Long) {
        val animator = ValueAnimator.ofInt(start, end).apply {
            duration = duracao
            interpolator = DecelerateInterpolator()
            addUpdateListener { tv.text = it.animatedValue.toString() }
        }
        animator.start()

        tv.scaleX = 0.3f
        tv.scaleY = 0.3f
        tv.alpha  = 0f
        tv.animate()
            .scaleX(1f).scaleY(1f).alpha(1f)
            .setDuration(600)
            .setInterpolator(OvershootInterpolator(1.5f))
            .start()
    }

    private fun animarProgressBar(pb: ProgressBar, start: Int, end: Int, duracao: Long) {
        pb.progress = start
        val animator = ValueAnimator.ofInt(start, end).apply {
            duration = duracao
            interpolator = DecelerateInterpolator()
            addUpdateListener { pb.progress = it.animatedValue as Int }
        }
        animator.startDelay = 300
        animator.start()
    }

    private fun animarEntrada(view: View, delayMs: Long) {
        view.alpha       = 0f
        view.translationY = 24f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(delayMs)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun dispararConfete(konfettiView: KonfettiView, porcentagem: Int) {
        val cores = listOf(
            0xFFE07A1F.toInt(),
            0xFFFFB347.toInt(),
            0xFFFFD08A.toInt(),
            0xFF8C6239.toInt(),
            0xFFC05010.toInt(),
            0xFFFFCC70.toInt(),
            0xFFFFF3DC.toInt()
        )

        val quantidadeMs = when {
            porcentagem == 100 -> 4000L
            porcentagem >= 70  -> 2500L
            else               -> 1200L
        }

        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = cores,
            emitter = Emitter(duration = quantidadeMs, TimeUnit.MILLISECONDS).max(200),
            position = Position.Relative(0.5, 0.0)
        )

        konfettiView.start(party)
    }
}