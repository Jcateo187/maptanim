package com.maptanim.app.features.shared.support

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.maptanim.app.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Data Model ─────────────────────────────────────────────────────────────
data class SupportChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
    val quickActionText: String? = null,
    val quickActionRoute: String? = null,
    val actionType: ActionType? = null
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
        title = "Forgot Password",
        subtitle = "Send a password reset link to your registered Gmail",
        icon = Icons.Default.LockReset,
        agentReply = "Don't worry! You can easily reset your password. Tap the button below to open the Forgot Password screen, enter your registered @gmail.com address, and we'll send a secure password reset link directly to your inbox.",
        actionText = "Go to Forgot Password Screen",
        actionRoute = Routes.FORGOT_PASSWORD,
        actionType = ActionType.NAVIGATE
    ),
    ProblemOption(
        id = "no_reset_link",
        title = "Didn't Receive Reset Link",
        subtitle = "Reset email is not arriving in your Gmail inbox",
        icon = Icons.Default.Email,
        agentReply = "If you haven't received your password reset email:\n\n1. Check your Spam, Junk, and Promotions folders in Gmail.\n2. Ensure your email is spelled correctly and ends with @gmail.com.\n3. Wait 60 seconds before requesting another reset email.\n4. Check if your Google account storage is full.",
        actionText = "Open Forgot Password",
        actionRoute = Routes.FORGOT_PASSWORD,
        actionType = ActionType.NAVIGATE
    ),
    ProblemOption(
        id = "locked_account",
        title = "Account Temporarily Locked",
        subtitle = "Security cooldown from failed login attempts",
        icon = Icons.Default.Security,
        agentReply = "For your account and farm security, logins are temporarily locked for 5 minutes after 3 consecutive failed password attempts.\n\nThe lockout automatically expires after the 5-minute cooldown. You can also reset your password now using your registered Gmail.",
        actionText = "Reset Password Now",
        actionRoute = Routes.FORGOT_PASSWORD,
        actionType = ActionType.NAVIGATE
    ),
    ProblemOption(
        id = "change_email",
        title = "Update Registered Gmail",
        subtitle = "Change or transfer your account email address",
        icon = Icons.Default.Person,
        agentReply = "To update or transfer your registered Gmail address, our support team can assist you after verifying your farm profile.\n\nPlease type your current registered email and the requested new Gmail address in the chat box below.",
        actionText = null
    ),
    ProblemOption(
        id = "registration_trouble",
        title = "Trouble Creating Account",
        subtitle = "Sign-up error or email already registered",
        icon = Icons.Default.PersonAdd,
        agentReply = "Common registration requirements:\n\n1. Your email must be a valid @gmail.com address.\n2. Passwords must be at least 8 characters.\n3. If your email is already registered, please log in or request a password reset instead.",
        actionText = "Go to Login",
        actionRoute = Routes.LOGIN,
        actionType = ActionType.NAVIGATE
    ),
    ProblemOption(
        id = "other_issue",
        title = "Other Account Concern",
        subtitle = "Chat directly with MapTanim Support Desk",
        icon = Icons.Default.SupportAgent,
        agentReply = "Please type your account concern in the chat box below. Our MapTanim agricultural support team will review your inquiry and help you access your farm.",
        actionText = null
    )
)

/**
 * Circular customer service launcher button featuring the head with headphone icon.
 * Clean circle with no extra container or text names.
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
            color = Color(0xF2122317),
            border = BorderStroke(1.5.dp, Color(0xFF4CAF50)),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF2E7D32), Color(0xFF142B1A))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "Customer Service",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Active online indicator dot
        Box(
            modifier = Modifier
                .size(11.dp)
                .clip(CircleShape)
                .background(Color(0xFF00E676))
                .border(1.5.dp, Color(0xFF122317), CircleShape)
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
                    text = "Kumusta! 👋 Welcome to MapTanim Support Desk.\n\nAre you having trouble with your account? Please select your issue below so we can assist you immediately."
                )
            )
        )
    }

    var selectedProblemId by remember { mutableStateOf<String?>(null) }
    var inputText by remember { mutableStateOf("") }
    var isAgentTyping by remember { mutableStateOf(false) }

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
            delay(500)
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
            
            delay(600)
            isAgentTyping = false
            val agentReply = SupportChatMessage(
                id = "agent_${System.currentTimeMillis()}",
                sender = MessageSender.AGENT,
                text = if (isSent) {
                    "Thank you for your message. Your support request has been logged and sent to the MapTanim administrative desk.\n\nOur agronomic support team reviews inquiries regularly. When an admin replies, you will receive an advisory notification in your Notifications tab."
                } else {
                    "Your message has been received. If you are currently offline, your request will be reviewed once connection is restored. You can also reach our team directly at support@maptanim.ph."
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
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF101912),
            border = BorderStroke(1.2.dp, Color(0xFF2E4D3E)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ─── Header ─────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF162419))
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
                                    .background(Color(0xFF233B29)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = "Customer Support",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            // Online Indicator
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                                    .border(1.5.dp, Color(0xFF162419), CircleShape)
                                    .align(Alignment.BottomEnd)
                            )
                        }

                        Column {
                            Text(
                                text = "MapTanim Support Desk",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                )
                                Text(
                                    text = "Online · Account Assistance",
                                    fontSize = 11.sp,
                                    color = Color(0xFFA5D6A7),
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
                            .background(Color(0xFF203324))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Support Chat",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF263D2E), thickness = 1.dp)

                // ─── Chat Thread ────────────────────────────────────────────
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
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
                            }
                        )
                    }

                    // Problem Selection Options (always accessible or after welcome)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "SELECT YOUR ACCOUNT ISSUE:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784),
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )

                            PredefinedProblems.forEach { problem ->
                                val isSelected = selectedProblemId == problem.id
                                Surface(
                                    onClick = { selectProblem(problem) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) Color(0xFF1E3827) else Color(0xFF142217),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFF4CAF50) else Color(0xFF2A4232)
                                    ),
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
                                                .background(Color(0xFF1B3323)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = problem.icon,
                                                contentDescription = problem.title,
                                                tint = Color(0xFFA5D6A7),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = problem.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = problem.subtitle,
                                                fontSize = 11.sp,
                                                color = Color(0xFF9E9E9E),
                                                maxLines = 1
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Select",
                                            tint = Color(0xFF81C784),
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
                                    .background(Color(0xFF162419))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = Color(0xFF81C784),
                                    strokeWidth = 1.5.dp
                                )
                                Text(
                                    text = "Support Specialist is typing...",
                                    fontSize = 11.sp,
                                    color = Color(0xFFA5D6A7),
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF263D2E), thickness = 1.dp)

                // ─── Input Bar ──────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF162419))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Describe your account problem...",
                                fontSize = 13.sp,
                                color = Color(0xFF758578)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF101912),
                            unfocusedContainerColor = Color(0xFF101912),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF81C784)
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
                                if (inputText.isNotBlank()) Color(0xFF2E7D32) else Color(0xFF233527)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            tint = if (inputText.isNotBlank()) Color.White else Color(0xFF616161),
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
    onActionClick: () -> Unit
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
            color = if (isAgent) Color(0xFF18281C) else Color(0xFF2E7D32),
            border = if (isAgent) BorderStroke(1.dp, Color(0xFF2B4733)) else null,
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
                        color = Color(0xFF81C784)
                    )
                }

                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )

                // Optional Quick Action Button
                if (message.quickActionText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onActionClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
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
                    fontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
