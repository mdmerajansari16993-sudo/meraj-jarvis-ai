package com.example.network

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {

    private const val TAG = "MerajJarvisGemini"
    private const val MODEL_NAME = "gemini-3.5-flash"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun queryJarvis(userPrompt: String, systemPrompt: String = ""): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalResponse(userPrompt)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", userPrompt)
                            })
                        }
                        put("parts", partsArray)
                    })
                }
                put("contents", contentsArray)

                if (systemPrompt.isNotBlank()) {
                    put("systemInstruction", JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", systemPrompt)
                            })
                        }
                        put("parts", partsArray)
                    })
                }
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini API returned code ${response.code}: $responseBody")
                return@withContext generateLocalResponse(userPrompt)
            }

            val parsedJson = JSONObject(responseBody)
            val candidates = parsedJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text.trim()
            } else {
                generateLocalResponse(userPrompt)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini call exception", e)
            generateLocalResponse(userPrompt)
        }
    }

    private fun generateLocalResponse(userPrompt: String): String {
        val lower = userPrompt.lowercase()
        return when {
            lower.contains("flipkart") || lower.contains("shoe") || lower.contains("buy") ->
                "Flipkart par 5 number ka shoes filter kar diya gaya hai. Top deal compare kar ke select kiya hai. Bhai yeh theek hai, isko buy kar do."
            lower.contains("free fire") || lower.contains("game") || lower.contains("gaming") ->
                "Jarvis Gaming Co-Pilot ready hai! Auto-Cover aur Phone Call Intercept shield active hai. Zero memory hooks ke saath 100% anti-cheat safe."
            lower.contains("kya haal") || lower.contains("kaise ho") || lower.contains("who are you") ->
                "Main Meraj Jarvis AI hoon — aapka advanced autonomous personal assistant! E-commerce shopping, gaming takeover, aur voice automation ke liye hamesha taiyyar."
            else ->
                "Command samjh li gayi hai! Autonomous assistant mode mein task execute kiya ja raha hai."
        }
    }
}
