package com.moodlog.ai.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.moodlog.ai.BuildConfig
import com.moodlog.ai.data.local.MoodEntry
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin wrapper around the Gemini SDK that produces empathetic, Indonesian-language
 * weekly insights from a user's mood/journal entries.
 *
 * IMPORTANT: never log raw journal contents in production. This is sensitive data.
 */
@Singleton
class GeminiService @Inject constructor() {

    private val model: GenerativeModel? by lazy {
        val key = BuildConfig.GEMINI_API_KEY
        if (key.isBlank()) null
        else GenerativeModel(modelName = "gemini-1.5-flash", apiKey = key)
    }

    suspend fun weeklyInsight(entries: List<MoodEntry>): Result<String> {
        val m = model ?: return Result.failure(IllegalStateException("GEMINI_API_KEY belum diset"))
        if (entries.isEmpty()) return Result.success("Belum ada catatan minggu ini.")

        val prompt = buildPrompt(entries)
        return runCatching {
            val response = m.generateContent(prompt)
            response.text?.trim().orEmpty()
        }
    }

    private fun buildPrompt(entries: List<MoodEntry>): String {
        val summary = entries.joinToString("\n") { e ->
            "- skor=${e.moodScore} emoji=${e.emoji} tags=[${e.tags}] catatan=${e.journal.take(200)}"
        }
        return """
            Kamu adalah teman pendamping mental health berbahasa Indonesia.
            Berdasarkan data check-in mood pengguna minggu ini di bawah, berikan:
            1. Ringkasan pola mood (3 kalimat).
            2. Tema/pemicu yang sering muncul.
            3. 2-3 saran self-care yang konkret dan ramah.
            Hindari diagnosis medis. Sampaikan dengan hangat, tidak menggurui.

            Data:
            $summary
        """.trimIndent()
    }
}
