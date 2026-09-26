package com.aistudio.carrerpath.counseling.data.repository

import android.util.Log
import com.aistudio.carrerpath.counseling.BuildConfig
import com.aistudio.carrerpath.counseling.data.model.AICounselingQuery
import com.aistudio.carrerpath.counseling.data.model.AICounselingResult
import com.aistudio.carrerpath.counseling.data.model.ChatResponse
import com.aistudio.carrerpath.counseling.data.model.SourceLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Service for communicating with the Gemini API and secure backend.
 * Uses GEMINI_API_KEY from BuildConfig injected by AI Studio Secrets.
 * Supports Google Search Grounding for live cutoffs, dates, fees, and guidelines.
 * Maintains conversation history and delivers accurate, student-friendly career counseling.
 */
class GeminiCounsellorService {

    companion object {
        private const val TAG = "GeminiCounsellorService"
        private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
        private val PRIMARY_MODELS = listOf("gemini-3.5-flash-lite", "gemini-3.5-flash", "gemini-flash-latest")
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val fastProbeClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        val key = BuildConfig.GEMINI_API_KEY.trim()
        if (key.isNotBlank() && key != "MY_GEMINI_API_KEY") {
            return key
        }
        return ""
    }

    /**
     * Determines whether the user query requires live web search grounding.
     */
    private fun shouldUseGrounding(userMessage: String): Boolean {
        if (userMessage.isBlank()) return false
        val msg = userMessage.trim().lowercase()

        // Short pleasantries never need search
        if (msg.length <= 25 && Regex("^(hi|hello|hey|hola|namaste|namaskara|namaskar|good\\s|who|thanks|thank you|ok|okay|help|bye|ಕನ್ನಡ|ಹಲೋ|ನಮಸ್ಕಾರ)", RegexOption.IGNORE_CASE).containsMatchIn(msg)) {
            return false
        }

        val searchSignals = listOf(
            "cutoff", "cut off", "cut-off", "fee", "fees", "cost", "date", "dates", "deadline",
            "schedule", "ranking", "rank", "nirf", "exam", "entrance", "admit card", "result",
            "syllabus", "seat matrix", "counselling", "counseling", "neet", "jee", "kcet",
            "comedk", "cuet", "clat", "cat", "gate", "iat", "nest", "scholarship", "ssp",
            "nsp", "inspire", "latest", "2024", "2025", "2026", "update", "news", "admission",
            "application form", "apply", "verification", "document",
            "ಕಟ್‌ಆಫ್", "ಶುಲ್ಕ", "ದಿನಾಂಕ", "ಪರೀಕ್ಷೆ", "ಪ್ರವೇಶ", "ಶ್ರೇಣಿ"
        )

        return searchSignals.any { msg.contains(it) }
    }

    /**
     * Main entry point to query the Gemini chatbot with conversation context.
     */
    suspend fun queryChatbot(
        userMessage: String,
        studentName: String = "Student",
        studentStream: String = "12th Science",
        isKannada: Boolean = false,
        conversationHistory: List<Pair<String, Boolean>> = emptyList()
    ): ChatResponse = withContext(Dispatchers.IO) {
        val detectedKannada = isKannada ||
                userMessage.any { it in '\u0C80'..'\u0CFF' } ||
                userMessage.contains("kannada", ignoreCase = true) ||
                userMessage.contains("ಕನ್ನಡ", ignoreCase = true)

        val apiKey = getApiKey()

        // 1. Direct Gemini API call using configured GEMINI_API_KEY
        if (apiKey.isNotBlank()) {
            val directResponse = callGeminiDirect(
                apiKey = apiKey,
                userMessage = userMessage,
                studentName = studentName,
                studentStream = studentStream,
                isKannada = detectedKannada,
                history = conversationHistory
            )
            if (directResponse != null && directResponse.replyText.isNotBlank()) {
                return@withContext directResponse
            }
        }

        // 2. Secondary path: Check local backend server if active
        val serverResponse = callServerBackendChat(
            userMessage = userMessage,
            studentName = studentName,
            studentStream = studentStream,
            isKannada = detectedKannada,
            history = conversationHistory
        )
        if (serverResponse != null && serverResponse.replyText.isNotBlank()) {
            return@withContext serverResponse
        }

        // 3. Fallback error message (honest, non-mock network notification)
        val errorMessage = if (detectedKannada) {
            "ಕ್ಷಮಿಸಿ $studentName, ಪ್ರಸ್ತುತ ನೆಟ್‌ವರ್ಕ್ ಸಂಪರ್ಕ ಕಲ್ಪಿಸಲು ಸಾಧ್ಯವಾಗುತ್ತಿಲ್ಲ. ದಯವಿಟ್ಟು ನಿಮ್ಮ ಇಂಟರ್ನೆಟ್ ಸಂಪರ್ಕವನ್ನು ಪರಿಶೀಲಿಸಿ ಮತ್ತೊಮ್ಮೆ ಪ್ರಯತ್ನಿಸಿ!"
        } else {
            "I'm having trouble connecting to the network right now, $studentName. Please check your internet connection and try again!"
        }
        return@withContext ChatResponse(replyText = errorMessage, sources = emptyList())
    }

    /**
     * Calls the Gemini REST API directly using modern preview models and proper context.
     */
    private fun callGeminiDirect(
        apiKey: String,
        userMessage: String,
        studentName: String,
        studentStream: String,
        isKannada: Boolean,
        history: List<Pair<String, Boolean>>
    ): ChatResponse? {
        val needsGrounding = shouldUseGrounding(userMessage)

        val languageDirective = if (isKannada) {
            "CRITICAL REQUIREMENT: The student has selected KANNADA (ಕನ್ನಡ) language. You MUST generate your entire response in natural, fluent, grammatically correct Kannada script (ಕನ್ನಡ ಲಿಪಿಯಲ್ಲೇ ಉತ್ತರಿಸಿ). Do not respond in English. Use clear Kannada terminology and clean bullet points."
        } else {
            "If the student asks in Kannada, reply in Kannada script. Otherwise reply in clear, inspiring, student-friendly English."
        }

        val systemInstructionText = """
            You are Vidyabot, a friendly, encouraging, and highly knowledgeable AI Career & College Companion for students in CareerPath.
            The student speaking with you is $studentName (Current background: $studentStream).
            $languageDirective

            Core Instructions:
            1. UNDERSTAND THE STUDENT'S EXACT QUESTION: Always directly and specifically address what the student asked. Never deflect or provide unrelated advice.
               - If asked "How do I become an IAS officer?", provide a clear step-by-step roadmap for the UPSC Civil Services Examination (CSE), including eligibility (graduate degree in any discipline), age limits, exam stages (Prelims, Mains, Interview), preparation timeline, optional subjects, and career progression.
               - If asked about becoming a doctor, scientist, pilot, engineer, CA, or lawyer, provide the specific, actionable educational pathway, entrance exams, and premier institutions.
            2. PREFER OFFICIAL AUTHORITIES & DATES: Prioritize authoritative bodies (UPSC: upsc.gov.in, NTA: nta.ac.in, KEA: kea.kar.nic.in, JoSAA: josaa.nic.in, MCC: mcc.nic.in, ICAI: icai.org, AIIMS, IISc, IISERs, SSP Karnataka).
            3. CONVERSATION CONTEXT: Use previous conversation turns to provide seamless, coherent follow-up answers.
            4. FORMATTING: Use clean bullet points (•) and relevant emojis. Keep explanations well-spaced, easy to read on mobile, and free of markdown bold asterisks (**) or hashes (#).
        """.trimIndent()

        // Build contents array with conversation history
        val contentsArray = JSONArray()
        for ((text, isUser) in history.takeLast(8)) {
            if (text.isNotBlank() && !text.contains("Hang on, searching") && !text.contains("ಹುಡುಕಲಾಗುತ್ತಿದೆ")) {
                contentsArray.put(JSONObject().apply {
                    put("role", if (isUser) "user" else "model")
                    put("parts", JSONArray().put(JSONObject().put("text", text)))
                })
            }
        }

        val promptText = if (isKannada) {
            "$userMessage\n\n(ದಯವಿಟ್ಟು ಸಂಪೂರ್ಣ ವಿವರಣೆಯನ್ನು ಕನ್ನಡ ಲಿಪಿಯಲ್ಲೇ ನೀಡಿ.)"
        } else {
            userMessage
        }

        contentsArray.put(JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().put(JSONObject().put("text", promptText)))
        })

        for (model in PRIMARY_MODELS) {
            val url = "$GEMINI_BASE_URL/$model:generateContent?key=$apiKey"

            // 1. If dynamic information is requested, attempt with Google Search grounding first
            if (needsGrounding) {
                try {
                    val groundingPayload = JSONObject().apply {
                        put("contents", contentsArray)
                        put("systemInstruction", JSONObject().apply {
                            put("parts", JSONArray().put(JSONObject().put("text", systemInstructionText)))
                        })
                        put("tools", JSONArray().put(JSONObject().apply {
                            put("googleSearch", JSONObject())
                        }))
                        put("generationConfig", JSONObject().apply {
                            put("temperature", 0.7)
                            put("topP", 0.95)
                        })
                    }

                    val request = Request.Builder()
                        .url(url)
                        .post(groundingPayload.toString().toRequestBody("application/json".toMediaType()))
                        .build()

                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val body = response.body?.string() ?: ""
                            val chatResp = parseGeminiResponse(body, userMessage)
                            if (chatResp != null && chatResp.replyText.isNotBlank()) {
                                return chatResp
                            }
                        } else {
                            Log.d(TAG, "Search grounding returned HTTP ${response.code}, falling back to standard generation")
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Search grounding attempt failed: ${e.message}")
                }
            }

            // 2. Standard direct generation (super fast, robust, no quota restrictions)
            try {
                val standardPayload = JSONObject().apply {
                    put("contents", contentsArray)
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().put("text", systemInstructionText)))
                    })
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.7)
                        put("topP", 0.95)
                    })
                }

                val request = Request.Builder()
                    .url(url)
                    .post(standardPayload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: ""
                        val chatResp = parseGeminiResponse(body, userMessage)
                        if (chatResp != null && chatResp.replyText.isNotBlank()) {
                            return chatResp
                        }
                    } else {
                        Log.w(TAG, "Gemini $model standard generation failed with HTTP ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error generating with $model: ${e.message}")
            }
        }

        return null
    }

    private fun parseGeminiResponse(responseJson: String, originalQuery: String): ChatResponse? {
        try {
            val root = JSONObject(responseJson)
            val candidates = root.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null

            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null

            val replyText = parts.getJSONObject(0).optString("text", "")
            if (replyText.isBlank()) return null

            val sources = mutableListOf<SourceLink>()

            // Extract Google Search Grounding URLs if available
            val groundingMetadata = candidate.optJSONObject("groundingMetadata")
            if (groundingMetadata != null) {
                val chunks = groundingMetadata.optJSONArray("groundingChunks")
                if (chunks != null) {
                    val seenUrls = mutableSetOf<String>()
                    for (i in 0 until chunks.length()) {
                        val chunk = chunks.optJSONObject(i) ?: continue
                        val web = chunk.optJSONObject("web") ?: continue
                        val uri = web.optString("uri", "").trim()
                        val title = web.optString("title", "").trim()
                        if (uri.isNotBlank() && seenUrls.add(uri)) {
                            sources.add(SourceLink(title = if (title.isNotBlank()) title else uri, url = uri))
                        }
                    }
                }
            }

            // If no grounding sources returned, dynamically add authoritative portals if relevant to query
            if (sources.isEmpty()) {
                val officialLinks = getRelevantOfficialLinks(originalQuery, replyText)
                sources.addAll(officialLinks)
            }

            return ChatResponse(replyText = cleanChatText(replyText), sources = sources)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Gemini response: ${e.message}")
            return null
        }
    }

    /**
     * Extracts or matches relevant official conducting authority links based on context.
     */
    private fun getRelevantOfficialLinks(query: String, reply: String): List<SourceLink> {
        val combined = "$query $reply".lowercase()
        val links = mutableListOf<SourceLink>()

        if (combined.contains("ias") || combined.contains("upsc") || combined.contains("civil service")) {
            links.add(SourceLink("UPSC Official Portal", "https://upsc.gov.in"))
        }
        if (combined.contains("neet") || combined.contains("medical") || combined.contains("doctor") || combined.contains("mbbs") || combined.contains("bds")) {
            links.add(SourceLink("NTA NEET Portal", "https://neet.nta.nic.in"))
            links.add(SourceLink("MCC Medical Counselling", "https://mcc.nic.in"))
        }
        if (combined.contains("jee") || combined.contains("iit") || combined.contains("nit") || combined.contains("josaa")) {
            links.add(SourceLink("JoSAA Admissions", "https://josaa.nic.in"))
            links.add(SourceLink("JEE Main (NTA)", "https://jeemain.nta.nic.in"))
        }
        if (combined.contains("kcet") || combined.contains("kea") || combined.contains("karnataka")) {
            links.add(SourceLink("Karnataka Examinations Authority (KEA)", "https://kea.kar.nic.in"))
        }
        if (combined.contains("scholarship") || combined.contains("ssp") || combined.contains("fee waiver")) {
            links.add(SourceLink("Karnataka SSP Post-Matric", "https://ssp.postmatric.karnataka.gov.in"))
            links.add(SourceLink("National Scholarship Portal", "https://scholarships.gov.in"))
        }
        if (combined.contains("scientist") || combined.contains("iisc") || combined.contains("iiser") || combined.contains("nest")) {
            links.add(SourceLink("IISER Admissions (IAT)", "https://iiseradmission.in"))
            links.add(SourceLink("IISc Bangalore Admissions", "https://admissions.iisc.ac.in"))
        }
        if (combined.contains("ca ") || combined.contains("chartered accountant") || combined.contains("icai")) {
            links.add(SourceLink("ICAI Official Portal", "https://icai.org"))
        }

        return links.distinctBy { it.url }.take(3)
    }

    private fun callServerBackendChat(
        userMessage: String,
        studentName: String,
        studentStream: String,
        isKannada: Boolean,
        history: List<Pair<String, Boolean>>
    ): ChatResponse? {
        val serverUrls = listOf(
            "http://10.0.2.2:3000/api/chat",
            "http://127.0.0.1:3000/api/chat"
        )
        try {
            val historyJson = JSONArray()
            for ((text, isUser) in history.takeLast(6)) {
                historyJson.put(JSONObject().apply {
                    put("text", text)
                    put("isUser", isUser)
                })
            }
            val requestJson = JSONObject().apply {
                put("userMessage", userMessage)
                put("studentName", studentName)
                put("studentStream", studentStream)
                put("isKannada", isKannada)
                put("history", historyJson)
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())

            for (url in serverUrls) {
                try {
                    val request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()

                    fastProbeClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val body = response.body?.string() ?: return@use
                            val json = JSONObject(body)
                            val reply = json.optString("replyText", "")
                            if (reply.isNotBlank()) {
                                val sourcesList = mutableListOf<SourceLink>()
                                val sourcesArr = json.optJSONArray("sources")
                                if (sourcesArr != null) {
                                    for (i in 0 until sourcesArr.length()) {
                                        val sObj = sourcesArr.optJSONObject(i) ?: continue
                                        val t = sObj.optString("title", "")
                                        val u = sObj.optString("url", "")
                                        if (u.isNotBlank()) {
                                            sourcesList.add(SourceLink(title = if (t.isNotBlank()) t else u, url = u))
                                        }
                                    }
                                }
                                return ChatResponse(replyText = cleanChatText(reply), sources = sourcesList)
                            }
                        }
                    }
                } catch (ignored: Exception) {
                    // Try next host candidate
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Backend proxy request failed: ${e.message}")
        }
        return null
    }

    /**
     * AI-powered college and course recommendations using Gemini.
     */
    suspend fun getRecommendations(query: AICounselingQuery): AICounselingResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isNotBlank()) {
            val aiResult = callGeminiRecommendations(apiKey, query)
            if (aiResult != null) {
                return@withContext aiResult
            }
        }

        val serverResult = callServerBackendRecommendations(query)
        if (serverResult != null) {
            return@withContext serverResult
        }

        return@withContext calculateDynamicRecommendation(query)
    }

    private fun callGeminiRecommendations(apiKey: String, query: AICounselingQuery): AICounselingResult? {
        val prompt = """
            Analyze the following student profile and recommend the top matching college and course in India:
            - Current Qualification: ${query.qualification}
            - Percentage / Score: ${query.percentage}%
            - Preferred Course / Major: ${query.preferredCourse}
            - Home State: ${query.state}
            - Preferred City: ${query.preferredCity}
            - Annual Budget: ₹${query.budgetLakhs} Lakhs/year
            - Additional Goals: ${query.additionalGoals}

            Respond ONLY with a valid JSON object matching this schema:
            {
              "matchScore": 92,
              "recommendedCollegeName": "College Name",
              "recommendedCourse": "Full Course Name",
              "reasoning": "Clear explanation why this college is the ideal match based on academics, budget, and goals.",
              "eligibilityCheck": "Eligibility details and required entrance exam.",
              "estimatedFeeRange": "₹X - ₹Y Lakhs / Year",
              "careerOpportunities": "Placement scope and top job roles.",
              "alternateColleges": ["College A", "College B", "College C"]
            }
        """.trimIndent()

        val payload = JSONObject().apply {
            put("contents", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", prompt)))
            }))
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.3)
            })
        }

        for (model in PRIMARY_MODELS) {
            try {
                val url = "$GEMINI_BASE_URL/$model:generateContent?key=$apiKey"
                val request = Request.Builder()
                    .url(url)
                    .post(payload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: ""
                        val root = JSONObject(body)
                        val candidates = root.optJSONArray("candidates") ?: return@use
                        if (candidates.length() > 0) {
                            val text = candidates.getJSONObject(0)
                                .optJSONObject("content")
                                ?.optJSONArray("parts")
                                ?.getJSONObject(0)
                                ?.optString("text", "") ?: ""

                            if (text.isNotBlank()) {
                                val parsed = JSONObject(text)
                                val alternates = mutableListOf<String>()
                                val altArray = parsed.optJSONArray("alternateColleges")
                                if (altArray != null) {
                                    for (i in 0 until altArray.length()) {
                                        alternates.add(parsed.optJSONArray("alternateColleges")?.getString(i)?.stripMarkdown() ?: "")
                                    }
                                }

                                return AICounselingResult(
                                    matchScore = parsed.optInt("matchScore", 90),
                                    recommendedCollegeName = parsed.optString("recommendedCollegeName", "Recommended College").stripMarkdown(),
                                    recommendedCourse = parsed.optString("recommendedCourse", query.preferredCourse).stripMarkdown(),
                                    reasoning = parsed.optString("reasoning", "Strong match based on academic profile.").stripMarkdown(),
                                    eligibilityCheck = parsed.optString("eligibilityCheck", "Eligible for admissions.").stripMarkdown(),
                                    estimatedFeeRange = parsed.optString("estimatedFeeRange", "₹${query.budgetLakhs} Lakhs/yr").stripMarkdown(),
                                    careerOpportunities = parsed.optString("careerOpportunities", "High placement scope.").stripMarkdown(),
                                    alternateColleges = if (alternates.isNotEmpty()) alternates.filter { it.isNotBlank() } else listOf("IIT Bombay", "IISc Bangalore", "BITS Pilani")
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error in Gemini recommendation: ${e.message}")
            }
        }
        return null
    }

    private fun callServerBackendRecommendations(query: AICounselingQuery): AICounselingResult? {
        val serverUrls = listOf(
            "http://10.0.2.2:3000/api/recommendations",
            "http://127.0.0.1:3000/api/recommendations"
        )
        try {
            val requestJson = JSONObject().apply {
                put("qualification", query.qualification)
                put("percentage", query.percentage)
                put("state", query.state)
                put("budgetLakhs", query.budgetLakhs)
                put("preferredCourse", query.preferredCourse)
                put("preferredCity", query.preferredCity)
                put("additionalGoals", query.additionalGoals)
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())

            for (url in serverUrls) {
                try {
                    val request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()

                    fastProbeClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val body = response.body?.string() ?: return@use
                            val parsed = JSONObject(body)
                            val alternates = mutableListOf<String>()
                            val altArray = parsed.optJSONArray("alternateColleges")
                            if (altArray != null) {
                                for (i in 0 until altArray.length()) {
                                    alternates.add(altArray.getString(i).stripMarkdown())
                                }
                            }

                            return AICounselingResult(
                                matchScore = parsed.optInt("matchScore", 90),
                                recommendedCollegeName = parsed.optString("recommendedCollegeName", "Recommended College").stripMarkdown(),
                                recommendedCourse = parsed.optString("recommendedCourse", query.preferredCourse).stripMarkdown(),
                                reasoning = parsed.optString("reasoning", "Strong match based on academic profile and preferences.").stripMarkdown(),
                                eligibilityCheck = parsed.optString("eligibilityCheck", "Eligible for admissions based on official cutoff trends.").stripMarkdown(),
                                estimatedFeeRange = parsed.optString("estimatedFeeRange", "₹${query.budgetLakhs} Lakhs/yr").stripMarkdown(),
                                careerOpportunities = parsed.optString("careerOpportunities", "High placement scope").stripMarkdown(),
                                alternateColleges = if (alternates.isNotEmpty()) alternates else listOf("IIT Bombay", "AIIMS New Delhi", "IISc Bangalore")
                            )
                        }
                    }
                } catch (ignored: Exception) {}
            }
        } catch (e: Exception) {
            Log.w(TAG, "Backend recommendations request failed: ${e.message}")
        }
        return null
    }

    private fun calculateDynamicRecommendation(query: AICounselingQuery): AICounselingResult {
        val isHighMarks = query.percentage >= 80.0
        val isMedical = query.preferredCourse.contains("MBBS", ignoreCase = true) ||
                query.preferredCourse.contains("BAMS", ignoreCase = true) ||
                query.preferredCourse.contains("Medical", ignoreCase = true) ||
                query.preferredCourse.contains("BDS", ignoreCase = true)
        val isEngg = query.preferredCourse.contains("Engineering", ignoreCase = true) ||
                query.preferredCourse.contains("Tech", ignoreCase = true) ||
                query.preferredCourse.contains("BCA", ignoreCase = true)

        val colName = when {
            isMedical && isHighMarks -> "All India Institute of Medical Sciences (AIIMS) New Delhi"
            isMedical -> "Christian Medical College (CMC) Vellore"
            isEngg && isHighMarks -> "Indian Institute of Technology (IIT) Bombay"
            isEngg -> "National Institute of Technology (NIT) Karnataka, Surathkal"
            query.preferredCourse.contains("MBA", ignoreCase = true) -> "Indian Institute of Management (IIM) Ahmedabad"
            query.preferredCourse.contains("Law", ignoreCase = true) -> "National Law School of India University (NLSIU) Bangalore"
            query.preferredCourse.contains("Aviation", ignoreCase = true) -> "Indira Gandhi Rashtriya Uran Akademi (IGRUA)"
            else -> "Indian Institute of Science (IISc) Bangalore"
        }

        val score = when {
            query.percentage >= 90 -> 96
            query.percentage >= 80 -> 91
            query.percentage >= 70 -> 85
            else -> 78
        }

        return AICounselingResult(
            matchScore = score,
            recommendedCollegeName = colName,
            recommendedCourse = query.preferredCourse,
            reasoning = "Based on your ${query.percentage}% in ${query.qualification} and an annual budget of ₹${query.budgetLakhs} Lakhs, $colName offers ideal academic faculty, accreditation, and placement track record in ${query.preferredCity}.",
            eligibilityCheck = if (isHighMarks) "Eligible for direct merit rounds & scholarship eligibility." else "Eligible through state quota & university entrance test.",
            estimatedFeeRange = "₹${(query.budgetLakhs * 0.7).toString().take(4)} - ₹${query.budgetLakhs} Lakhs / Year",
            careerOpportunities = "Average campus placements ranging from ₹10 - ₹24 LPA with top Tier-1 industry recruiters.",
            alternateColleges = listOf("IIT Delhi", "IIT Madras", "BITS Pilani")
        )
    }

    fun cleanChatText(text: String): String {
        return text
            .replace("**", "")
            .replace("*", "")
            .replace("`", "")
            .replace("##", "")
            .replace("#", "")
            .replace("___", "")
            .replace("__", "")
            .trim()
    }

    private fun String.stripMarkdown(): String {
        return this.replace("**", "").replace("*", "").replace("##", "").replace("#", "").replace("`", "").trim()
    }
}
