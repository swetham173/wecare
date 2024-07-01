package com.example.convo.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.convo.Dest
import com.example.convo.LCViewmodel
import com.google.android.gms.common.util.CollectionUtils
import com.example.convo.R

@Composable
fun Journaling(navController: NavController, vm: LCViewmodel) {
    LaunchedEffect(key1 = true) {
        vm.fetchNotes()
    }

    val notes by vm.notes.observeAsState()
    val comicSansFontFamily = FontFamily(
        Font(R.font.comic)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(BorderStroke(5.dp, Color.Black), shape = RoundedCornerShape(16.dp))
    ) {
        Column {
            TopBoxWithSortButton()
            if (notes?.isEmpty() == true) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.notes))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                      
                ) {

                    LottieAnimation(
                        composition = composition,
                        modifier = Modifier
                            .size(400.dp)
                            .align(Alignment.Center)
                            .padding(vertical = 38.dp),
                        iterations = Int.MAX_VALUE
                    )
                    Text(text =
                    "Tap the button to add a new note!",
                        fontSize = (20.sp),
                        fontFamily = comicSansFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(30.dp) ,textAlign = TextAlign.End)
                }
            } else {
                notes?.forEach { note ->
                    NoteCard(
                        title = note.title,
                        description = note.content,
                        id = note.noteId,
                        onDeleteClick = {
                            vm.deleteNote(note.noteId)
                        },
                        navController = navController,
                        onClick = {
                            navController.navigate(Dest.addnotes.createRoute(note.title, note.content, note.noteId))
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate(Dest.Splash.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(55.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.addnotes),
                contentDescription = "Add Note",
                modifier = Modifier.clickable {
                    navController.navigate(Dest.addnotes.createRoute("", "", ""))
                }
            )
        }
    }
}



@Composable
fun TopBoxWithSortButton() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.LightGray)
    ) {
        Text(
            text = "Journaling",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 19.dp, vertical = 10.dp)
        )
    }
}
@Composable
fun NoteCard(
    title: String,
    description: String,
    id:String,
    onDeleteClick: () -> Unit,
    navController: NavController,
    onClick: () -> Unit,
) {
    val gradient = Brush.linearGradient(
        colors = CollectionUtils.listOf(
            colorResource(id = R.color.LightBLue),
            colorResource(id = R.color.StrongPink)
        )
    )
    Card(

        backgroundColor = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(brush = gradient)
            .clickable {
                onClick()
            },
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),

    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title in bold text
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Description in regular text
                Text(
                    text = description,
                    style = MaterialTheme.typography.body2,
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            // Delete icon
            Icon(
                painter = painterResource(id = R.drawable.deleteicon),
                contentDescription = "Delete",
                modifier = Modifier
                    .clickable { onDeleteClick() }
                    .size(35.dp)
            )
        }
    }
}