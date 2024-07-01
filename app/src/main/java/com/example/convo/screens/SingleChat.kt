package com.example.convo.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.convo.LCViewmodel
import com.example.convo.R
import java.util.Collections.emptyList


@Composable
fun SingleChat(navController: NavController, vm: LCViewmodel, chatId: String, patientID: String) {
    var replyText by rememberSaveable { mutableStateOf("") }
    var chatName by remember { mutableStateOf("User Name") }
    var chatImage by remember { mutableStateOf("") }
    val usertype by vm.userType.observeAsState()
    var isDoctor = usertype == "Doctor"

    val recipientId by vm.recipientId.observeAsState(initial = null)
    LaunchedEffect(Unit) {
        vm.clearSentMessage()
    }
    LaunchedEffect(chatId, patientID) {
        Log.d("SingleChat", "chatId: $chatId, patientID: $patientID")
        vm.fetchChatMessages(chatId, patientID)

        val doctorData = vm.fetchDoctor(chatId)
        val patientData = vm.fetchPatient(patientID)

        chatName = if (doctorData != null && patientData != null) {
            if (vm.userType.value == "Doctor") patientData.name?:"" else doctorData.name?:""
        } else {
            "User Name"
        }

         chatImage = if (doctorData != null && patientData != null) {
            if (vm.userType.value == "Doctor") patientData.imageURL ?: "" else doctorData.imageURL ?: ""
        } else {
            ""
        }
    }

    val chatmsgs by vm.chatMessages.observeAsState(emptyList())
    val sentMessage by vm.sentMessage.observeAsState(initial = null)
    val receivedMessage by vm.receivedMessage.observeAsState(initial = null)
    var currentUserID = vm.auth.currentUser?.uid ?: ""
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Image(
            painter = painterResource(id = R.drawable.wall),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp), // Adjust padding as needed
            contentScale = ContentScale.FillBounds // Fill the bounds of the BoxWithConstraints
        )
        Column(modifier = Modifier.fillMaxSize()) {
            ChatHeader(
                name = chatName,
                imageUrl = chatImage,
                onBackClick = { navController.popBackStack() }
            )

            LazyColumn(
                modifier = Modifier.weight(7f),
                contentPadding = PaddingValues(8.dp),
                reverseLayout = true
            ) {
                // Display database messages in reverse order
                if (sentMessage != null && ((isDoctor && recipientId == patientID) || (!isDoctor && recipientId == chatId))) {
                    item {
                        ChatBubble(
                            message = sentMessage!!,
                            isSentByCurrentUser = true

                        )
                    }


                }

                // Display database messages
                items(chatmsgs) { msg ->
                    if (sentMessage == null || msg.message != sentMessage) {
                        ChatBubble(
                            message = msg.message.toString(),
                            isSentByCurrentUser = msg.sentBy == currentUserID
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // Reply box at the bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ReplyBox(
                reply = replyText,
                onReplyChange = { replyText = it },
                onSendReply = {
                    try {
                        vm.onSendReply(chatId, replyText, patientID) { success ->
                            if (success) {
                                replyText = ""
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ReplyBox", "Error sending reply: ${e.message}", e)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit,modifier: Modifier) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
            TextField(
                value = reply,
                onValueChange = onReplyChange,

                modifier = Modifier.width(340.dp),
                placeholder = { Text("Type your reply...") },

                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.LightGray
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSendReply()
                    }
                )

            )
        Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSendReply,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow), // Replace with your send icon resource
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
@Composable
fun ChatHeader(name: String, imageUrl: String, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button (Icon)
        IconButton(
            onClick = { onBackClick() },
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.backarrow),
                contentDescription = "Back",
                tint = Color.LightGray
            )
        }

        // Spacer between back button and doctor's image
        Spacer(modifier = Modifier.width(16.dp))

        // Doctor's image with background
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
        ) {
            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = "Doctor's Image",


                modifier = Modifier.fillMaxSize()
            )
        }

        // Spacer between doctor's image and doctor's name
        Spacer(modifier = Modifier.width(16.dp))

        // Doctor's name with background
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.subtitle1.copy(
                fontWeight = FontWeight.Bold),
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
@Composable
fun ChatBubble(message: String, isSentByCurrentUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSentByCurrentUser) colorResource(id = R.color.teal_700) else colorResource(id = R.color.color1))
                .padding(12.dp)
        ) {
            Text(
                text = message,
                color = if (isSentByCurrentUser) Color.White else Color.Black
            )
        }
    }
}