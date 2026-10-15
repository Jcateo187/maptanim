package com.maptanim.app.features.shared.support

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.maptanim.app.navigation.Routes
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Theme Colors ───────────────────────────────────────────────────────────
private val GreenPrimary = Color(0xFF2E7D32)
private val GreenContainer = Color(0xFFE8F5E9)
private val BlackPrimary = Color(0xFF111813)
private val BlackSecondary = Color(0xFF4B5563)
private val BlackMuted = Color(0xFF6B7280)
private val BorderLight = Color(0xFFE5E7EB)
private val SurfaceWhite = Color(0xFFF9FAF8)

// ─── Data Model ─────────────────────────────────────────────────────────────
data class SupportChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
    val quickActionText: String? = null,
    val quickActionRoute: String? = null,
    val actionType: ActionType? = null,
    val feedbackTicketId: String? = null,
    val serviceRating: Int? = null,
    val wasHelpful: Boolean? = null
)

enum class MessageSender {
    AGENT,
    USER
}

enum class ActionType {
    NAVIGATE,
    DISMISS
}

data class ProblemOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val agentReply: String,
    val actionText: String? = null,
    val actionRoute: String? = null,
    val actionType: ActionType? = null
)

private val PredefinedProblems = listOf(
    ProblemOption(
        id = "forgot_password",
        title = "Nakalimutan ang Password",
        subtitle = "Magpadala ng password reset link sa Gmail",
        icon = Icons.Default.LockReset,
        agentReply = "Maaari mong i-reset ang iyong password gamit ang iyong rehistradong Gmail. Pindutin ang button sa ibaba upang makapunta sa Forgot Password screen.",
        actionText = "Pumunta sa Forgot Password",
        actionRoute = Routes.FORGOT_PASSWORD,
        actionType = ActionType.NAVIGATE
    ),
    ProblemOption(
        id = "no_reset_link",
        title = "Walang Natanggap na Reset Link",
        subtitle = "Hindi dumarating ang email sa inbox",
        icon = Icons.Default.Email,
        agentReply = "Kung hindi pa dumarating ang reset email:\n\n• Tingnan ang Spam o Junk folder sa iyong Gmail.\n• Siguraduhing tama ang spelling ng iyong @gmail.com address.\n• Maghintay ng 1 hanggang 2 minuto bago sumubok muli.",
        actionText = "Subukan Muli",
        actionRoute = Routes.FORGOT_PASSWORD,
        actionType = ActionType.NAVIGATE
    ),
    ProblemOption(
        id = "locked_account",
        title = "Pansamantalang Naka-lock",
        subtitle = "Na-lock dahil sa 3 maling password",
        icon = Icons.Default.Security,
        agentReply = "Naka-lock ang account ng 5 minuto matapos ang sunod-sunod na maling password para sa iyong proteksyon.\n\nKusang mawawala ang lock pagkatapos ng 5 minuto, o maaari mo ring i-reset agad ang password gamit ang iyong Gmail.",
        actionText = "I-reset ang Password",
        actionRoute = Routes.FORGOT_PASSWORD,
        actionType = ActionType.NAVIGATE
    ),
    ProblemOption(
        id = "change_email",
        title = "Pagpalit ng Gmail Address",
        subtitle = "Magpalit o maglipat ng email ng account",
        icon = Icons.Default.Person,
        agentReply = "Upang mapalitan ang iyong Gmail address, i-type sa chat sa ibaba ang iyong kasalukuyang email at ang bagong email na nais gamitin upang mai-update ng aming team.",
        actionText = null
    ),
    ProblemOption(
        id = "registration_trouble",
        title = "Problema sa Pag-sign Up",
        subtitle = "Hindi makagawa ng bagong account",
        icon = Icons.Default.PersonAdd,
        agentReply = "Paalala sa paggawa ng account:\n\n• Siguraduhing may @gmail.com ang email.\n• Hindi bababa sa 8 characters ang password.\n• Kung may dati ka nang account, mag-Log In o i-reset ang password.",
        actionText = "Pumunta sa Login",
        actionRoute = Routes.LOGIN,
        actionType = ActionType.NAVIGATE
    ),
    ProblemOption(
        id = "other_issue",
        title = "Iba Pang Concern o Tanong",
        subtitle = "Mag-iwan ng mensahe sa aming team",
        icon = Icons.Default.SupportAgent,
        agentReply = "I-type sa chat box sa ibaba ang iyong katanungan o problema, at tutugon ang aming admin sa iyong account.",
        actionText = null
    )
)

/**
 * Circular customer service launcher button in MapTanim primary green theme.
 */
@Composable
fun ModernCustomerServiceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(52.dp)
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = GreenPrimary,
            border = BorderStroke(2.dp, Color.White),
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "Customer Service",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Active online indicator dot
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Color(0xFF22C55E))
                .border(2.dp, Color.White, CircleShape)
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
fun CustomerServiceChatDialog(
    navController: NavController,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember {
        mutableStateOf(
            listOf(
                SupportChatMessage(
                    id = "msg_welcome",
                    sender = MessageSender.AGENT,
                    text = "Kumusta! Welcome sa MapTanim Support.\n\nPumili ng concern sa ibaba o mag-iwan ng mensahe para sa aming team."
                )
            )
        )
    }

    var selectedProblemId by remember { mutableStateOf<String?>(null) }
    var inputText by remember { mutableStateOf("") }
    var isAgentTyping by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val user = com.maptanim.app.data.remote.SupabaseClient.client.auth.currentUserOrNull()
            val userId = user?.id
            val pastFeedback = com.maptanim.app.data.repository.ProfileRepository().getFeedbackRepliesForUser(userId)
            if (pastFeedback.isNotEmpty()) {
                val serverMsgs = mutableListOf<SupportChatMessage>()
                pastFeedback.reversed().takeLast(5).forEach { item ->
                    serverMsgs.add(
                        SupportChatMessage(
                            id = "user_${item.id ?: System.currentTimeMillis()}",
                            sender = MessageSender.USER,
                            text = if (item.subject.isNotBlank() && item.subject != "Support In-App Message") "${item.subject}: ${item.message}" else item.message,
                            timestamp = item.createdAt?.take(10) ?: ""
                        )
                    )
                    if (!item.adminReply.isNullOrBlank()) {
                        serverMsgs.add(
                            SupportChatMessage(
                                id = "agent_${item.id ?: System.currentTimeMillis()}",
                                sender = MessageSender.AGENT,
                                text = "Admin: ${item.adminReply}",
                                timestamp = item.resolvedAt?.take(10) ?: "",
                                feedbackTicketId = item.id,
                                serviceRating = item.serviceRating,
                                wasHelpful = item.wasHelpful
                            )
                        )
                    }
                }
                if (serverMsgs.isNotEmpty()) {
                    messages = messages + serverMsgs
                    delay(150)
                    listState.animateScrollToItem(messages.size)
                }
            }
        } catch (_: Exception) {}
    }

    fun rateTicket(ticketId: String, rating: Int, wasHelpful: Boolean) {
        messages = messages.map { msg ->
            if (msg.feedbackTicketId == ticketId) {
                msg.copy(serviceRating = rating, wasHelpful = wasHelpful)
            } else msg
        }
        coroutineScope.launch {
            com.maptanim.app.data.repository.ProfileRepository().rateFeedbackReply(ticketId, rating, wasHelpful)
        }
    }

    fun selectProblem(problem: ProblemOption) {
        selectedProblemId = problem.id
        val userMsg = SupportChatMessage(
            id = "user_${System.currentTimeMillis()}",
            sender = MessageSender.USER,
            text = problem.title
        )
        messages = messages + userMsg

        coroutineScope.launch {
            delay(150)
            listState.animateScrollToItem(messages.size)
            isAgentTyping = true
            delay(400)
            isAgentTyping = false
            val agentMsg = SupportChatMessage(
                id = "agent_${System.currentTimeMillis()}",
                sender = MessageSender.AGENT,
                text = problem.agentReply,
                quickActionText = problem.actionText,
                quickActionRoute = problem.actionRoute,
                actionType = problem.actionType
            )
            messages = messages + agentMsg
            delay(100)
            listState.animateScrollToItem(messages.size)
        }
    }

    fun sendMessage() {
        if (inputText.isBlank()) return
        val textToSend = inputText.trim()
        inputText = ""

        val userMsg = SupportChatMessage(
            id = "user_${System.currentTimeMillis()}",
            sender = MessageSender.USER,
            text = textToSend
        )
        messages = messages + userMsg

        coroutineScope.launch {
            delay(150)
            listState.animateScrollToItem(messages.size)
            isAgentTyping = true

            // Dispatch live ticket to Supabase feedback table
            val isSent = try {
                com.maptanim.app.data.repository.RepositoryProvider.userRepository.sendSupportTicket(
                    subject = "Support In-App Message",
                    message = textToSend,
                    category = "ACCOUNT_SUPPORT"
                )
            } catch (_: Exception) {
                false
            }

            delay(500)
            isAgentTyping = false
            val agentReply = SupportChatMessage(
                id = "agent_${System.currentTimeMillis()}",
                sender = MessageSender.AGENT,
                text = if (isSent) {
                    "Salamat! Naipadala na sa aming admin ang iyong mensahe. Makikita mo ang kanilang tugon sa iyong Notifications tab."
                } else {
                    "Natanggap ang iyong mensahe. Kung offline ka sa ngayon, maipapadala ito sa server kapag nagkaroon ng koneksyon. Maaari ka ring sumulat sa support@maptanim.ph."
                }
            )
            messages = messages + agentReply
            delay(100)
            listState.animateScrollToItem(messages.size)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 480.dp)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderLight),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ─── Header ─────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = "Customer Support",
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            // Online Indicator
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                                    .border(1.5.dp, Color.White, CircleShape)
                                    .align(Alignment.BottomEnd)
                            )
                        }

                        Column {
                            Text(
                                text = "MapTanim Support",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlackPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Text(
                                    text = "Online · Tulong sa Account at Sakahan",
                                    fontSize = 11.5.sp,
                                    color = BlackSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Support Chat",
                            tint = BlackSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = BorderLight, thickness = 1.dp)

                // ─── Chat Thread ────────────────────────────────────────────
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(SurfaceWhite)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatMessageItem(
                            message = msg,
                            onActionClick = {
                                when (msg.actionType) {
                                    ActionType.NAVIGATE -> {
                                        msg.quickActionRoute?.let { route ->
                                            onDismiss()
                                            navController.navigate(route)
                                        }
                                    }
                                    ActionType.DISMISS -> onDismiss()
                                    null -> Unit
                                }
                            },
                            onRate = { ticketId, stars, helpful ->
                                rateTicket(ticketId, stars, helpful)
                            }
                        )
                    }

                    // Problem Selection Options (FAQ tiles)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "MGA KARANIWANG CONCERN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )

                            PredefinedProblems.forEach { problem ->
                                val isSelected = selectedProblemId == problem.id
                                Surface(
                                    onClick = { selectProblem(problem) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) GreenContainer else Color.White,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) GreenPrimary else BorderLight
                                    ),
                                    shadowElevation = if (isSelected) 0.dp else 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(GreenContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = problem.icon,
                                                contentDescription = problem.title,
                                                tint = GreenPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = problem.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = BlackPrimary
                                            )
                                            Text(
                                                text = problem.subtitle,
                                                fontSize = 11.sp,
                                                color = BlackSecondary,
                                                maxLines = 1
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Piliin",
                                            tint = if (isSelected) GreenPrimary else BlackMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Typing Indicator
                    if (isAgentTyping) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .padding(start = 4.dp, top = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(BorderStroke(1.dp, BorderLight), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = GreenPrimary,
                                    strokeWidth = 1.5.dp
                                )
                                Text(
                                    text = "Nagsusulat...",
                                    fontSize = 11.sp,
                                    color = BlackSecondary,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = BorderLight, thickness = 1.dp)

                // ─── Input Bar ──────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "I-type ang iyong mensahe...",
                                fontSize = 13.sp,
                                color = BlackMuted
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF3F4F6),
                            unfocusedContainerColor = Color(0xFFF3F4F6),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = BlackPrimary,
                            unfocusedTextColor = BlackPrimary,
                            cursorColor = GreenPrimary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp),
                        maxLines = 3
                    )

                    IconButton(
                        onClick = { sendMessage() },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank()) GreenPrimary else Color(0xFFE5E7EB)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Ipadala",
                            tint = if (inputText.isNotBlank()) Color.White else BlackMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: SupportChatMessage,
    onActionClick: () -> Unit,
    onRate: (ticketId: String, rating: Int, wasHelpful: Boolean) -> Unit = { _, _, _ -> }
) {
    val isAgent = message.sender == MessageSender.AGENT

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isAgent) Alignment.Start else Alignment.End
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isAgent) 4.dp else 16.dp,
                bottomEnd = if (isAgent) 16.dp else 4.dp
            ),
            color = if (isAgent) Color.White else GreenPrimary,
            border = if (isAgent) BorderStroke(1.dp, BorderLight) else null,
            shadowElevation = if (isAgent) 1.dp else 2.dp,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isAgent) {
                    Text(
                        text = "MapTanim Support",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }

                Text(
                    text = message.text,
                    fontSize = 13.5.sp,
                    color = if (isAgent) BlackPrimary else Color.White,
                    lineHeight = 19.sp
                )

                // Service CSAT Rating prompt for Admin replies
                if (isAgent && !message.feedbackTicketId.isNullOrBlank()) {
                    if (message.serviceRating != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GreenContainer)
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Nakatulong: ${message.serviceRating}/5 Stars",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF9FAFB))
                                .border(BorderStroke(0.5.dp, BorderLight), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Nakatulong ba ang tugon na ito?",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BlackSecondary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                (1..5).forEach { star ->
                                    IconButton(
                                        onClick = { onRate(message.feedbackTicketId, star, star >= 3) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "$star stars",
                                            tint = Color(0xFFFFB74D),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Rate",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenPrimary
                                )
                            }
                        }
                    }
                }

                // Optional Quick Action Button
                if (message.quickActionText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onActionClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = message.quickActionText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = message.timestamp,
                    fontSize = 9.5.sp,
                    color = if (isAgent) BlackMuted else Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

