package com.example.convo.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.convo.Dest
import com.example.convo.LCViewmodel
import com.example.convo.R
import com.example.convo.navigateTo
import com.google.android.gms.common.util.CollectionUtils
import java.util.Collections.emptyList

@Composable
fun PatientChatList(navController: NavController,vm:LCViewmodel) {
    val doctors by vm.doctors.observeAsState(emptyList()) // Observe the list of doctors
    val doctorSearching= rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.doctorsearching))

    val comicSansFontFamily = FontFamily(
        Font(R.font.comic)
    )
    val gradient = Brush.linearGradient(
        colors = CollectionUtils.listOf(
            colorResource(id = R.color.LightBLue),
            colorResource(id = R.color.StrongPink)
        ))
if(!doctors.isEmpty()){

    Box(
        modifier = Modifier

            .border(
                BorderStroke(5.dp, Color.Black),
                shape = RoundedCornerShape(16.dp)
            ).background(gradient)

    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.TopCenter)
        ) {

            Box(
                modifier = Modifier


            ) {
                Text(
                    text = "Pick the Perfect Doctor Today!",
                    fontSize = (20.sp),
                    fontFamily = comicSansFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(8.dp)
                )
            }

            doctors.forEach { doctor ->
                DoctorCard(Name = doctor.name ?: "", Image = doctor.imageURL ?: "", Description = doctor.description ?: "", onClick = {
                    val chatId=doctor.duid?:""
                    val currentUser = vm.auth.currentUser
                    val patientID = currentUser?.uid ?: ""
                    navigateTo(navController,Dest.SingleChat.createRouteForEachUser(chatId,patientID))
                })
            }
        }
    }
    Box(modifier = Modifier.padding(9.dp)) {
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            Surface(color = Color.White, shape = RoundedCornerShape(10.dp), elevation = 10.dp) {
                BottomNavigation(
                    backgroundColor = Color.LightGray,
                    modifier = Modifier.height(40.dp)
                ) {
                    BottomNavigationItem(selected = false, onClick = {
                        navigateTo(navController, Dest.PatientChatList.route)
                    },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.chaticon),
                                contentDescription = null,
                                modifier = Modifier.width(20.dp)
                            )
                        }
                    )
                    BottomNavigationItem(selected = true, onClick = {
                        navigateTo(navController, Dest.PatientProfile.route)
                    },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = null,
                                modifier = Modifier.width(20.dp)
                            )
                        })
                }
            }
        }
    }

}else{
    Box(
        modifier = Modifier

            .border(
                BorderStroke(5.dp, Color.Black),
                shape = RoundedCornerShape(16.dp)
            ).background(gradient)

    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = "Waiting for the doctor to connect with you",
                fontSize = (20.sp),
                fontFamily = comicSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(30.dp) ,textAlign = TextAlign.Center
            )

            LottieAnimation(
                composition = doctorSearching.value,
                modifier = Modifier.size(500.dp),
                iterations = Int.MAX_VALUE
            )

        }}
    Box(modifier = Modifier.padding(9.dp)) {
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            Surface(color = Color.White, shape = RoundedCornerShape(10.dp), elevation = 10.dp) {
                BottomNavigation(
                    backgroundColor = Color.LightGray,
                    modifier = Modifier.height(40.dp)
                ) {
                    BottomNavigationItem(selected = false, onClick = {
                        navigateTo(navController, Dest.PatientChatList.route)
                    },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.chaticon),
                                contentDescription = null,
                                modifier = Modifier.width(20.dp)
                            )
                        }
                    )
                    BottomNavigationItem(selected = true, onClick = {
                        navigateTo(navController, Dest.PatientProfile.route)
                    },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = null,
                                modifier = Modifier.width(20.dp)
                            )
                        })
                }
            }
        }

}}}

@Composable
fun DoctorCard(Name:String,Image:String,Description:String,onClick:()->Unit) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .border(
                BorderStroke(0.5.dp, Color.Black),
                shape = RoundedCornerShape(10.dp)
            )    .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.Transparent

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile picture
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            ) {
                if (Image!=null) {
                    Image(
                        painter = rememberImagePainter(data = Image),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).clip(CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = Name, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = Description)
            }
        }
    }
}