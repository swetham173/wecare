package com.example.convo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.common.util.CollectionUtils.listOf
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController,vm:LCViewmodel) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(brush = Brush.horizontalGradient(
                listOf(
                    colorResource(id = R.color.light_blue),
                    colorResource(id = R.color.lightPink)
                )
            )),
        contentAlignment = Alignment.Center,

    ) {
        Image(painter = painterResource(id = R.drawable.splash), contentDescription =null,
        modifier = Modifier.fillMaxSize().padding(16.dp))
        LaunchedEffect(Unit) {
            delay(5000)
            navController.navigate(Dest.SignUp.route)
        }
    }
    checkSignin(vm = vm, navController = navController)
}