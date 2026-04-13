package com.imd.trabalhofinalunidade1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBasquete: Button = findViewById(R.id.btnAbrirBasquete)
        val btnCalculadora: Button = findViewById(R.id.btnAbrirCalculadora)
        val btnQuiz: Button = findViewById(R.id.btnAbrirQuiz)

        btnBasquete.setOnClickListener {
            val intent = Intent(this, BasqueteActivity::class.java)
            startActivity(intent)
        }

        btnCalculadora.setOnClickListener {
            val intent = Intent(this, CalculadoraActivity::class.java)
            startActivity(intent)
        }

        btnQuiz.setOnClickListener {
            val intent = Intent(this, QuizMenuActivity::class.java)
            startActivity(intent)
        }
    }
}
