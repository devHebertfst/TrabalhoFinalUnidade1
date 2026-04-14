package com.imd.trabalhofinalunidade1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class QuizMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_menu)

        findViewById<Button>(R.id.btnIniciarQuiz).setOnClickListener {
            startActivity(Intent(this, QuizCategoriasActivity::class.java))
        }

        findViewById<Button>(R.id.btnVoltarCentralQuizMenu).setOnClickListener {
            voltarParaCentral()
        }
    }

    private fun voltarParaCentral() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
        finish()
    }
}
