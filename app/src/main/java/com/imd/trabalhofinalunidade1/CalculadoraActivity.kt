package com.imd.trabalhofinalunidade1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CalculadoraActivity : AppCompatActivity() {
    private lateinit var tvDisplay: TextView

    private var currentInput: String = ""
    private var operand: Double? = null
    private var pendingOp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)

        tvDisplay = findViewById(R.id.txtResultado)

        val digits = listOf(
            "0" to R.id.btn0,
            "1" to R.id.btn1,
            "2" to R.id.btn2,
            "3" to R.id.btn3,
            "4" to R.id.btn4,
            "5" to R.id.btn5,
            "6" to R.id.btn6,
            "7" to R.id.btn7,
            "8" to R.id.btn8,
            "9" to R.id.btn9,
            "." to R.id.btnPonto
        )
        digits.forEach { (digit, id) ->
            findViewById<Button>(id).setOnClickListener { appendDigit(digit) }
        }

        val ops = listOf(
            "+" to R.id.btnSomar,
            "-" to R.id.btnSubtrair,
            "×" to R.id.btnMultiplicar,
            "÷" to R.id.btnDividir
        )
        ops.forEach { (op, id) ->
            findViewById<Button>(id).setOnClickListener { onOperator(op) }
        }

        findViewById<Button>(R.id.btnIgual).setOnClickListener { onEquals() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { backspace() }
        findViewById<Button>(R.id.btnVoltarCentral).setOnClickListener { voltarParaCentral() }

        updateDisplay()
    }

    private fun voltarParaCentral() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
        finish()
    }

    private fun appendDigit(d: String) {
        if (d == "." && currentInput.contains(".")) return
        currentInput = if (currentInput == "0") d else currentInput + d
        updateDisplay()
    }

    private fun onOperator(op: String) {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                operand = if (operand == null) value
                else performOperation(operand!!, value, pendingOp)
            }
            currentInput = ""
        }
        pendingOp = op
        updateDisplay()
    }

    private fun onEquals() {
        if (operand != null && currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull() ?: return
            val result = performOperation(operand!!, value, pendingOp)
            operand = null
            pendingOp = null
            currentInput = result.toString()
            updateDisplay()
        }
    }

    private fun performOperation(a: Double, b: Double, op: String?): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) {
                Toast.makeText(this, "Divisão por zero", Toast.LENGTH_SHORT).show()
                a
            } else a / b
            else -> b
        }
    }

    private fun clearAll() {
        currentInput = ""
        operand = null
        pendingOp = null
        updateDisplay()
    }

    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        tvDisplay.text = if (currentInput.isNotEmpty()) currentInput
        else (operand?.toString() ?: "0")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentInput", currentInput)
        outState.putDouble("operand", operand ?: Double.NaN)
        outState.putString("pendingOp", pendingOp)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentInput = savedInstanceState.getString("currentInput", "")
        val opnd = savedInstanceState.getDouble("operand", Double.NaN)
        operand = if (opnd.isNaN()) null else opnd
        pendingOp = savedInstanceState.getString("pendingOp")
        updateDisplay()
    }
}
