package com.imd.trabalhofinalunidade1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnCalculadora).setOnClickListener {
            startActivity(Intent(this, CalculadoraActivity::class.java))
        }

        findViewById<Button>(R.id.btnBasquete).setOnClickListener {
            startActivity(Intent(this, BasqueteActivity::class.java))
        }
    }
}