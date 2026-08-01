package com.example.data.network

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

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(
        systemInstruction: String,
        userPrompt: String,
        targetLanguage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                Exception("Gemini API key is missing. Please enter your key in the AI Studio Secrets panel.")
            )
        }

        val fullSystemInstruction = "$systemInstruction\n\nIMPORTANT: Respond primarily in the target language requested ($targetLanguage) unless specified otherwise. Format the output with clear headings, bullet points, or clean Markdown formatting for max readability."

        try {
            // Build Request JSON using org.json
            val requestJson = JSONObject().apply {
                // contents
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        put("role", "user")
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", userPrompt)
                            })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                // systemInstruction
                val sysInstObj = JSONObject().apply {
                    val sysPartsArray = JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", fullSystemInstruction)
                        })
                    }
                    put("parts", sysPartsArray)
                }
                put("systemInstruction", sysInstObj)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestJson.toString().toRequestBody(mediaType)

            val url = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("API Error (${response.code}): ${if (responseString.isNotBlank()) responseString else response.message}")
                )
            }

            val responseJson = JSONObject(responseString)
            
            if (responseJson.has("candidates")) {
                val candidates = responseJson.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    if (candidate.has("content")) {
                        val content = candidate.getJSONObject("content")
                        if (content.has("parts")) {
                            val parts = content.getJSONArray("parts")
                            if (parts.length() > 0) {
                                val text = parts.getJSONObject(0).optString("text", "")
                                if (text.isNotBlank()) {
                                    return@withContext Result.success(text)
                                }
                            }
                        }
                    }
                }
            }

            if (responseJson.has("error")) {
                val errorObj = responseJson.getJSONObject("error")
                val msg = errorObj.optString("message", "Unknown Gemini API error")
                return@withContext Result.failure(Exception("Gemini Error: $msg"))
            }

            Result.failure(Exception("No content generated. Please try again with a different prompt."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
