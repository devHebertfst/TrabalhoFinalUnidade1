package com.imd.trabalhofinalunidade1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_main)

        val btnBasquete: View = findViewById(R.id.btnAbrirBasquete)
        val btnCalculadora: View = findViewById(R.id.btnAbrirCalculadora)
        val btnQuiz: View = findViewById(R.id.btnAbrirQuiz)

        val btnTema: ImageButton = findViewById(R.id.btnAlternarTema)

        val isNightMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        btnTema.setImageResource(
            if (isNightMode) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        )

        btnTema.setOnClickListener {
            val nightModeAtivo = resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES

            if (nightModeAtivo) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        btnBasquete.setOnClickListener {
            startActivity(Intent(this, BasqueteActivity::class.java))
        }

        btnCalculadora.setOnClickListener {
            startActivity(Intent(this, CalculadoraActivity::class.java))
        }

        btnQuiz.setOnClickListener {
            startActivity(Intent(this, QuizMenuActivity::class.java))
        }
    }
}
