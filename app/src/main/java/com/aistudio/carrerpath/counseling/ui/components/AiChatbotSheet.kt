package com.aistudio.carrerpath.counseling.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.R
import com.aistudio.carrerpath.counseling.data.model.SourceLink
import com.aistudio.carrerpath.counseling.data.repository.GeminiCounsellorService
import com.aistudio.carrerpath.counseling.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isThinking: Boolean = false,
    val timestamp: String = "Just now",
    val sources: List<SourceLink> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatbotSheet(
    studentName: String = "Student",
    studentStream: String = "12th Science",
    initialPrompt: String? = null,
    isInitialKannada: Boolean = false,
    onDismiss: () -> Unit,
    onNavigateToCourses: () -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    var isBotThinking by remember { mutableStateOf(false) }
    var isKannadaMode by remember { mutableStateOf(isInitialKannada) }
    val context = LocalContext.current

    val welcomeEnglish = "Hi $studentName, I am Vidyabot! 🌟 I'm powered by Gemini with Google Search Grounding to help you with cutoffs, entrance exams, fees, college admissions, and RIASEC career matches. What would you like to explore?"
    val welcomeKannada = "ನಮಸ್ಕಾರ $studentName! ನಾನು ವಿದ್ಯಾಬಾಟ್ 🌟. ಅಧಿಕೃತ Google Search ಶೋಧನೆಯೊಂದಿಗೆ ಕಾಲೇಜು ಪ್ರವೇಶಗಳು, RIASEC ವೃತ್ತಿ ಆಯ್ಕೆಗಳು, ಕಟ್‌ಆಫ್‌ಗಳು, ಶುಲ್ಕಗಳು ಮತ್ತು ವಿದ್ಯಾರ್ಥಿವೇತನಗಳ ಬಗ್ಗೆ ನಿಖರ ಮಾಹಿತಿ ನೀಡಲು ಇಲ್ಲಿದ್ದೇನೆ. ನೀವು ಯಾವ ಮಾಹಿತಿ ತಿಳಿಯಲು ಬಯಸುತ್ತೀರಿ?"

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = if (isInitialKannada) welcomeKannada else welcomeEnglish,
                isUser = false
            )
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val geminiService = remember { GeminiCounsellorService() }

    val quickQuestions = if (isKannadaMode) {
        listOf(
            "IAS ಅಧಿಕಾರಿ ಆಗುವುದು ಹೇಗೆ?",
            "BDS & MBBS ಅತ್ಯುತ್ತಮ ಕಾಲೇಜುಗಳು ಮತ್ತು ಕಟ್‌ಆಫ್",
            "ಟಾಪ್ ಇಂಜಿನಿಯರಿಂಗ್ (KCET/COMEDK) ಕಾಲೇಜುಗಳ ಶುಲ್ಕ",
            "12ನೇ ತರಗತಿ ನಂತರ ಲಭ್ಯವಿರುವ ಸರ್ಕಾರಿ ವಿದ್ಯಾರ್ಥಿವೇತನಗಳು"
        )
    } else {
        listOf(
            "How do I become an IAS officer?",
            "Which colleges are best for BDS & MBBS?",
            "Top engineering colleges cutoff & fees",
            "Government scholarships for 12th students"
        )
    }

    fun sendMessage(userMsg: String) {
        if (userMsg.isBlank() || isBotThinking) return
        messages.add(ChatMessage(text = userMsg, isUser = true))
        inputText = ""
        isBotThinking = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
        }

        val thinkingMsg = if (isKannadaMode) "ವಿದ್ಯಾಬಾಟ್ ಯೋಚಿಸುತ್ತಿದೆ..." else "Vidyabot is thinking..."
        messages.add(ChatMessage(text = thinkingMsg, isUser = false, isThinking = true))

        coroutineScope.launch {
            val history = messages
                .filter { !it.isThinking && it.text.isNotBlank() }
                .map { it.text to it.isUser }

            val response = geminiService.queryChatbot(
                userMessage = userMsg,
                studentName = studentName,
                studentStream = studentStream,
                isKannada = isKannadaMode,
                conversationHistory = history
            )
            // Remove thinking message
            if (messages.isNotEmpty() && messages.last().isThinking) {
                messages.removeAt(messages.size - 1)
            }
            messages.add(ChatMessage(text = response.replyText, isUser = false, sources = response.sources))
            isBotThinking = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(initialPrompt) {
        if (!initialPrompt.isNullOrBlank()) {
            delay(300)
            sendMessage(initialPrompt)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(640.dp)
        ) {
            // Top Header with Vidyabot Mascot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GeoGradientStart, GeoGradientMiddle, GeoGradientEnd)
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.95f),
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                VidyabotAvatar(size = 42.dp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Vidyabot",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = VidyabotGreenBubble,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TravelExplore,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = if (isKannadaMode) "Google ಶೋಧನೆ" else "GROUNDED AI",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (isKannadaMode) "ನಿಮ್ಮ ಆಪ್ತ ಶೈಕ್ಷಣಿಕ ಒಡನಾಡಿ • $studentStream" else "Your Friendly Career Companion • $studentStream",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Language Toggle Chip inside sheet
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable {
                                isKannadaMode = !isKannadaMode
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isKannadaMode) "🇮🇳 ಕನ್ನಡ" else "🇬🇧 ENG",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }
            }

            // Quick Questions & Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    Text(
                        text = if (isKannadaMode) "ವಿದ್ಯಾಬಾಟ್ ಅವರೊಂದಿಗೆ ಕೇಳಬಹುದಾದ ಪ್ರಶ್ನೆಗಳು:" else "Suggested Questions for Vidyabot:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 6.dp, top = 4.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        quickQuestions.forEach { q ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EduBlueContainer,
                                border = BorderStroke(1.dp, EduBluePrimary.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { sendMessage(q) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EduBluePrimary, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = q,
                                        fontSize = 12.sp,
                                        color = EduBluePrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(messages) { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (!msg.isUser) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                border = BorderStroke(1.dp, EduCardBorder),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    VidyabotAvatar(size = 30.dp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                bottomEnd = if (msg.isUser) 4.dp else 16.dp
                            ),
                            color = if (msg.isUser) EduBluePrimary else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (msg.isUser) EduBluePrimary else EduCardBorder),
                            shadowElevation = 2.dp,
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (msg.isThinking) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = EduBluePrimary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(msg.text, fontSize = 12.sp, color = Color.Gray)
                                    }
                                } else {
                                    val cleanedText = remember(msg.text) {
                                        msg.text.replace("**", "").replace("*", "").trim()
                                    }
                                    Text(
                                        text = cleanedText,
                                        color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp
                                    )

                                    if (!msg.isUser && msg.sources.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = EduCardBorder.copy(alpha = 0.6f), thickness = 0.5.dp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.TravelExplore,
                                                contentDescription = null,
                                                tint = EduBluePrimary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isKannadaMode) "ಅಧಿಕೃತ ಮೂಲಗಳು:" else "Official Sources:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = EduBluePrimary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            msg.sources.take(3).forEach { source ->
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = EduBlueContainer.copy(alpha = 0.5f),
                                                    border = BorderStroke(0.5.dp, EduBluePrimary.copy(alpha = 0.2f)),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            try {
                                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                                                                context.startActivity(intent)
                                                            } catch (e: Exception) { }
                                                        }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.OpenInNew,
                                                            contentDescription = null,
                                                            tint = EduBluePrimary,
                                                            modifier = Modifier.size(11.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(
                                                            text = source.title,
                                                            fontSize = 10.sp,
                                                            color = EduBluePrimary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { 
                            Text(
                                if (isKannadaMode) "ಕೋರ್ಸ್‌ಗಳು, ಕಟ್‌ಆಫ್, ಕಾಲೇಜುಗಳ ಬಗ್ಗೆ ಕೇಳಿ..." else "Ask Vidyabot about courses, careers, cutoffs...", 
                                fontSize = 12.sp
                            ) 
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { sendMessage(inputText) },
                        enabled = inputText.isNotBlank() && !isBotThinking,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = EduBluePrimary),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
