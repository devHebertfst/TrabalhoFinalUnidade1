package com.imd.trabalhofinalunidade1

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class QuizHistoryEntry(
    val timestamp: Long,
    val categorias: List<String>,
    val pontuacao: Int,
    val totalPerguntas: Int,
    val tempoTotalMs: Long
)

object QuizHistoryStorage {
    private const val PREFS_NAME = "quiz_history"
    private const val KEY_ENTRIES = "entries"
    private const val MAX_ENTRIES = 20

    fun saveEntry(context: Context, entry: QuizHistoryEntry) {
        val current = loadEntries(context).toMutableList()
        current.add(0, entry)
        val trimmed = current.take(MAX_ENTRIES)

        val jsonArray = JSONArray()
        trimmed.forEach { item ->
            jsonArray.put(
                JSONObject().apply {
                    put("timestamp", item.timestamp)
                    put("categorias", JSONArray(item.categorias))
                    put("pontuacao", item.pontuacao)
                    put("totalPerguntas", item.totalPerguntas)
                    put("tempoTotalMs", item.tempoTotalMs)
                }
            )
        }

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ENTRIES, jsonArray.toString())
            .apply()
    }

    fun loadEntries(context: Context): List<QuizHistoryEntry> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_ENTRIES, null) ?: return emptyList()
        val jsonArray = JSONArray(raw)

        return buildList {
            for (index in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(index)
                val categoriasArray = item.getJSONArray("categorias")
                val categorias = buildList {
                    for (catIndex in 0 until categoriasArray.length()) {
                        add(categoriasArray.getString(catIndex))
                    }
                }
                add(
                    QuizHistoryEntry(
                        timestamp = item.getLong("timestamp"),
                        categorias = categorias,
                        pontuacao = item.getInt("pontuacao"),
                        totalPerguntas = item.getInt("totalPerguntas"),
                        tempoTotalMs = item.getLong("tempoTotalMs")
                    )
                )
            }
        }
    }
}
