package com.example.convo.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
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
import com.example.convo.R
import com.example.convo.Dest
import com.example.convo.LCViewmodel
import com.example.convo.navigateTo
import com.google.android.gms.common.util.CollectionUtils

@Composable
fun EmotionTracker(navController: NavController, vm: LCViewmodel) {
    val robot = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.robot))

    val happy = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.smiley))
    val nervous = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.nervous))
    val sad = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sad))
    val pendown = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pendown))
    val image = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.images))
    val walk = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.walk))
    val gallery= rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.gallery))
    val music= rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.music))
    val icecream= rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.icecream))
    val youtube= rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.youtube))
    val breathing= rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.breathing))
    val selfcare= rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.selfcare))
    val color1= colorResource(R.color.color1)
    val color2= colorResource(R.color.color2)
    val color3= colorResource(R.color.color3)
    val comicSansFontFamily = FontFamily(
        Font(R.font.comic)
    )
    val Happycard = remember {
        mutableStateOf(false)
    }
    val Sadcard = remember {
        mutableStateOf(false)
    }
    val Nervouscard = remember {
        mutableStateOf(false)
    }

    val cardColors = remember {
        mutableStateListOf(color1, color2, color3) // Default colors
    }
    val gradient = Brush.linearGradient(
        colors = CollectionUtils.listOf(
            colorResource(id = R.color.LightBLue),
            colorResource(id = R.color.StrongPink)
        ))
    Box(
        modifier = Modifier.fillMaxSize().background(gradient)

    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.height / 3f
            val semiCircleCenterX = size.width / 2f
            val semiCircleCenterY = size.height - radius
            val semiCircleStartAngle = 180f
            val semiCircleSweepAngle = 180f




            drawArc(
                color = Color.LightGray,
                startAngle = semiCircleStartAngle,
                sweepAngle = semiCircleSweepAngle,
                useCenter = false,
                size = androidx.compose.ui.geometry.Size(radius * 2, radius),
                topLeft = Offset(semiCircleCenterX - radius, semiCircleCenterY),
                style =
                Stroke(width = 4.dp.toPx()),
            )

        }
LottieAnimation(composition = robot.value,
    modifier = Modifier.size(400.dp).offset(y=10.dp)

)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 310.dp) // Adjust padding to position below the image
                .background(Color.LightGray, shape = CutCornerShape(16.dp))
                .padding(8.dp)
        ) {
            Text(
                text = "Your feelings truly matter to us.          We warmly encourage you to share your Voice.",
                fontFamily = comicSansFontFamily,
                fontSize = (20.sp),
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center)


            )
        }

        LottieAnimation(
            composition = happy.value,
            iterations = Int.MAX_VALUE,
            modifier = Modifier
                .size(100.dp)
                .offset(x = 10.dp, y = 500.dp)
                .clickable {
                    Happycard.value = !Happycard.value
                }

        )

        LottieAnimation(
            composition = nervous.value,
            iterations = Int.MAX_VALUE,
            modifier = Modifier
                .size(108.dp)
                .offset(x = 150.dp, y = 433.dp)
                .clickable {
                    Nervouscard.value = !Nervouscard.value
                }
        )
        LottieAnimation(
            composition = sad.value,
            iterations = Int.MAX_VALUE,
            modifier = Modifier
                .size(100.dp)
                .offset(x = 300.dp, y = 495.dp)
                .clickable {
                    Sadcard.value = !Sadcard.value
                }
        )
        if (Happycard.value) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable {
                    Happycard.value = false
                }
            )
            AnimatedVisibility(
                visible = Happycard.value,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 10000)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 1000)
                ) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .padding(16.dp)
                        .background(Color.Black, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.BottomCenter,

                    ) {

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items(3) { index ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .width(250.dp)
                                    .fillMaxSize()
                                    ,
                                backgroundColor = cardColors[index]


                            ) {
                                when (index) {
                                    0 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "Great to see you happy!        Want to share how was your day? ",
                                                fontFamily = comicSansFontFamily,
                                                fontSize = 17.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier=Modifier.offset(y = 30.dp),
                                            )
                                            LottieAnimation(
                                                composition = pendown.value,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier
                                                    .size(300.dp)
                                                    .offset(x = 10.dp, y = 20.dp)
                                                    .clickable {
                                                        navigateTo(
                                                            navController,
                                                            Dest.Journaling.route
                                                        )
                                                    }
                                            )
                                        }
                                    }
                                    1 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "Create an album of your favorite happy moments to revisit later",
                                                fontFamily = comicSansFontFamily,
                                                fontSize = 17.sp,
                                                textAlign = TextAlign.Center,
                                                modifier=Modifier.offset(y = 10.dp),
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            LottieAnimation(
                                                composition = image.value,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier
                                                    .size(150.dp)
                                                    .offset(x = 7.dp, y = 3.dp)
                                                    .clickable {
                                                        navigateTo(
                                                            navController,
                                                            Dest.memories.route
                                                        )
                                                    }
                                            )
                                        }
                                    }
                                    2 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "Try a mindful walk. Focus on your breathing and the sensations around you",
                                                fontFamily = comicSansFontFamily,
                                                fontSize = 17.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            LottieAnimation(
                                                composition = walk.value,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier
                                                    .size(130.dp) // Adjusted size to a more visible value
                                                    .offset(x = 7.dp, y = 20.dp)
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

        if (Sadcard.value) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable {
                    Sadcard.value = false
                }
            )
            AnimatedVisibility(
                visible = Sadcard.value,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 10000)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 1000)
                ) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .padding(16.dp)
                        .background(Color.Black, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.BottomCenter,

                    ) {

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items(3) { index ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .width(250.dp)
                                    .fillMaxSize(),

                                backgroundColor = cardColors[index]


                            ) {
                                when (index) {
                                    0 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "Remember the great times? Take a trip down memory lane in the gallery",
                                                fontFamily = comicSansFontFamily,
                                                fontSize = 17.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier=Modifier.offset(y = 20.dp),
                                            )
                                            LottieAnimation(
                                                composition = gallery.value,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier
                                                    .size(300.dp)
                                                    .offset(x = 10.dp, y = 20.dp)
                                                    .clickable {
                                                        navigateTo(
                                                            navController,
                                                            Dest.memories.route
                                                        )
                                                    }
                                            )
                                        }
                                    }
                                    1 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "Would you like to explore some soothing tunes together?",
                                                fontFamily = comicSansFontFamily,
                                                fontSize = 17.sp,
                                                textAlign = TextAlign.Center,
                                                modifier=Modifier.offset(y = 20.dp),
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            LottieAnimation(
                                                composition = music.value,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier
                                                    .size(200.dp)
                                                    .offset(x = 7.dp, y = 3.dp)
                                                    .clickable {
                                                        val intent = Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse("https://open.spotify.com/")
                                                        )
                                                        navController.context.startActivity(intent)
                                                    }
                                            )
                                        }
                                    }
                                    2 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "Sometimes a little treat can lift your spirits.",
                                                fontFamily = comicSansFontFamily,
                                                fontSize = 17.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            LottieAnimation(
                                                composition = icecream.value,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier
                                                    .size(150.dp) // Adjusted size to a more visible value
                                                    .offset(x = 7.dp, y = 20.dp)
                                                    .clickable {
                                                        val intent = Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse("https://www.swiggy.com/")
                                                        )
                                                        navController.context.startActivity(intent)
                                                    }
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

        if (Nervouscard.value) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable {
                    Nervouscard.value = false
                }
            )
            AnimatedVisibility(
                visible = Nervouscard.value,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 10000)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 1000)
                ) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .padding(16.dp)
                        .background(Color.Black, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.BottomCenter,

                    ) {

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items(3) { index ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .width(250.dp)
                                    .fillMaxSize(),

                                backgroundColor = cardColors[index]


                            ) {
                                when (index) {
                                    0 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "Feeling anxious?                     Take a moment to relax with this comforting video.",
                                                fontFamily = comicSansFontFamily,
                                                fontSize = 17.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier=Modifier.offset(y = 20.dp),
                                            )
                                            LottieAnimation(
                                                composition = youtube.value,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier
                                                    .size(200.dp)
                                                    .offset(x = 10.dp, y = 30.dp)
                                                    .clickable {
                                                        val intent = Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse("https://www.youtube.com/watch?v=hBzP8MtJf04")
                                                        )
                                                        navController.context.startActivity(intent)
                                                    }
                                            )
                                        }
                                    }
                                    1 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "Take a moment to practice deep breathing.",
                                                fontFamily = comicSansFontFamily,
                                                fontSize = 17.sp,
                                                textAlign = TextAlign.Center,
                                                modifier=Modifier.offset(y = 20.dp),
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            LottieAnimation(
                                                composition = breathing.value,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier
                                                    .size(190.dp)
                                                    .offset(x = 7.dp, y = 20.dp)
                                            )
                                        }
                                    }
                                    2 -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "Be kind to yourself. It's okay to feel nervous; allow yourself to experience your emotions without judgment.",
                                                fontFamily = comicSansFontFamily,
                                                fontSize = 17.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            LottieAnimation(
                                                composition = selfcare.value,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier
                                                    .size(150.dp) // Adjusted size to a more visible value
                                                    .offset(x = 7.dp, y = 20.dp)
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