package com.example.convo.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.convo.LCViewmodel
import com.example.convo.R
import com.google.android.gms.common.util.CollectionUtils
import java.util.*

@Composable
fun AddingNotes(navController: NavController,vm:LCViewmodel,titlee:String,descriptionn:String,noteID:String) {


    var title = rememberSaveable { mutableStateOf( titlee) }
    var description = rememberSaveable { mutableStateOf( descriptionn) }
    val noteId = remember { generateNoteId()}
        val gradient = Brush.linearGradient(
        colors = CollectionUtils.listOf(
            colorResource(id = R.color.LightBLue),
            colorResource(id = R.color.StrongPink)
        )
    )
    LaunchedEffect(key1 = true) {
        vm.fetchNotes()
    }


    var userId=vm.auth.currentUser?.uid
    Box(
        modifier = Modifier
            .fillMaxSize()

            .background(color = Color.LightGray).border(BorderStroke(5.dp, Color.Black), shape = RoundedCornerShape(16.dp))

    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                fun String.isBlank(): Boolean {
                    return this == ""
                }
                val notes by vm.notes.observeAsState()

                    TextField(
                        value = title.value,
                        onValueChange = { title.value = it },
                        label = { Text("Enter Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )

                    // Description TextField
                    TextField(
                        value = description.value,
                        onValueChange = { description.value = it },
                        label = { Text("Enter Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(450.dp)
                            .clip(
                                RoundedCornerShape(10.dp)
                            )
                    )


                Spacer(modifier = Modifier.weight(1f))

                // Save Button
                Button(
                    onClick = {
                        if (noteId=="") {
                            vm.addNote(
                                title = title.value,
                                content = description.value,
                                userId = userId ?: "",
                                noteId = noteId
                            )
                        } else {
                            vm.addNote(
                                title = title.value,
                                content = description.value,
                                userId = userId ?: "",
                                noteId = noteID
                            )
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp)
                ) {
                    Text("Save")
                }
            }
        }
    }
}
 fun generateNoteId(): String {
    return UUID.randomUUID().toString()
}