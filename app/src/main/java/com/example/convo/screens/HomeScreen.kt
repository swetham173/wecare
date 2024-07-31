package com.example.convo.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.convo.Dest
import com.example.convo.LCViewmodel
import com.example.convo.R
import com.google.android.gms.common.util.CollectionUtils
@Composable
fun HomeScreen(navController: NavController,vm:LCViewmodel) {
    val comicSansFontFamily = FontFamily(
        Font(R.font.comic)
    )
    val doctor = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.doctor))
    val diary = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.diary))
    val camera = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.camera))
    val composition=rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation1))
    val gradient = Brush.linearGradient(
        colors = CollectionUtils.listOf(
            colorResource(id = R.color.StrongBlue),
            colorResource(id = R.color.StrongPink)
        ))
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .background(gradient)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {
                Box(

                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
                        .padding(6.dp)
                        .clickable {
                            navController.navigate(Dest.EmotionTracker.route)
                        }
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(16.dp),
                        )
                )

                {
                    LottieAnimation(
                        composition = composition.value,
                        modifier = Modifier.align(Alignment.TopCenter).size(500.dp),// Position animation at top-center
                        iterations = Int.MAX_VALUE,

                    )
                    Box(
                        modifier = Modifier
                            // Padding around the entire box
                            .background(Color.LightGray, shape = RoundedCornerShape(14.dp))
                            .align(Alignment.BottomCenter) // Background with rounded corners
                    ) {
                        Text(
                            text = "How's it going ?",
                            fontSize = (20.sp),
                            fontFamily = comicSansFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(8.dp)

                        )
                    }
                }


                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
                        .padding(6.dp)
                        .clickable {
                            navController.navigate(Dest.PatientChatList.route)
                        }
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(16.dp)
                        )

                ) {
                    LottieAnimation(
                        composition = doctor.value,
                        modifier = Modifier.align(Alignment.TopCenter).size(2900.dp),
                        iterations = Int.MAX_VALUE
                    )
                    Box(
                        modifier = Modifier
                            // Padding around the entire box
                            .background(Color.LightGray, shape = RoundedCornerShape(14.dp))
                            .align(Alignment.BottomCenter) // Background with rounded corners
                    ) {
                        Text(
                            text = "Let's talk?",
                            fontSize = (20.sp),
                            fontFamily = comicSansFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(8.dp)

                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
                        .padding(6.dp)
                        .clickable {
                            navController.navigate(Dest.Journaling.route)
                        }
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    LottieAnimation(
                        composition = diary.value,
                        modifier = Modifier.align(Alignment.TopCenter).size(700.dp),
                        iterations = Int.MAX_VALUE
                    )
                    Box(
                        modifier = Modifier
                            // Padding around the entire box
                            .background(Color.LightGray, shape = RoundedCornerShape(14.dp))
                            .align(Alignment.BottomCenter) // Background with rounded corners
                    ) {
                        Text(
                            text = "Journaling?",
                            fontSize = (20.sp),
                            fontFamily = comicSansFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(8.dp)

                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
                        .padding(6.dp)
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            navController.navigate(Dest.memories.route)
                        }
                ) {
                    LottieAnimation(
                        composition = camera.value,
                        modifier = Modifier.align(Alignment.TopCenter).size(700.dp),
                        iterations = Int.MAX_VALUE,
                    )
                    Box(
                        modifier = Modifier
                            // Padding around the entire box
                            .background(Color.LightGray, shape = RoundedCornerShape(14.dp))
                            .align(Alignment.BottomCenter) // Background with rounded corners

                    ) {
                        Text(
                            text = "Memories?",
                            fontSize = (20.sp),
                            fontFamily = comicSansFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(8.dp)

                        )
                    }
                }
            }
        }
    }
}

