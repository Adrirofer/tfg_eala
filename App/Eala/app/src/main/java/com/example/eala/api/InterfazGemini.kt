package com.example.eala.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers
import retrofit2.http.Query

interface InterfazGemini {

    interface GeminiService {
        @POST("models/gemini-2.0-flash:generateContent")
        @Headers("Content-Type: application/json")
        fun generateContent(
            @Query("key") apiKey: String,
            @Body request: GeminiRequest
        ): Call<GeminiResponse>

    }
}

// Gemini Request
data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val role: String,
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

// Gemini response
data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: GeminiContent
)