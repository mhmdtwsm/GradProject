package com.example.project1.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.R
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.ui.theme.customColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val messageInput by viewModel.messageInput.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat with AI") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState is ChatUiState.Empty && !isLoading) {
                    EmptyState()
                } else if (uiState is ChatUiState.Conversation) {
                    val conversation = uiState as ChatUiState.Conversation
                    ChatConversation(messages = conversation.messages)
                }

                if (isLoading) {
                    CircularProgressIndicator()
                }
            }

            MessageInput(
                value = messageInput,
                onValueChange = viewModel::onMessageInputChange,
                onSendClick = viewModel::sendMessage,
                isLoading = isLoading
            )
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.robot),
            contentDescription = "AI Chatbot",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ask me anything about cybersecurity!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChatConversation(messages: List<ChatMessage>) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(messages) { message ->
            ChatMessageItem(message)
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val isUserMessage = message.role == "user"
    val horizontalArrangement = if (isUserMessage) Arrangement.Start else Arrangement.End

    val bubbleColor = if (isUserMessage) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val textColor = if (isUserMessage) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    // Define bubble shape
    val bubbleShape = RoundedCornerShape(
        topStart = if (isUserMessage) 4.dp else 16.dp,
        topEnd = if (isUserMessage) 16.dp else 4.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = bubbleShape,
            color = bubbleColor,
            tonalElevation = 2.dp
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                if (isUserMessage) {
                    Text(text = message.content, color = textColor)
                } else {
                    MarkdownText(markdown = message.content, color = textColor)
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Ask a question...") },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.customColors.inputBackground,
                unfocusedContainerColor = MaterialTheme.customColors.inputBackground,
                disabledContainerColor = MaterialTheme.customColors.inputBackground.copy(alpha = 0.5f),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            ),
            shape = MaterialTheme.shapes.extraLarge,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.width(8.dp))

        FilledIconButton(
            onClick = onSendClick,
            enabled = value.isNotEmpty() && !isLoading,
            modifier = Modifier.size(52.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send"
            )
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    Project1Theme {
        ChatScreen(navController = rememberNavController())
    }
}