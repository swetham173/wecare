package com.example.convo

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

fun navigateTo(navController: NavController,route:String){
    navController.navigate(route){
        popUpTo(route)
        launchSingleTop=true
    }
}
@Composable
fun commonProgressBar(){
Row(modifier = Modifier
    .alpha(0.5f)
    .background(Color.LightGray)
    .clickable(enabled = false) {}
    .fillMaxSize(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center){
        CircularProgressIndicator()
    }

}
@Composable
fun checkSignin(vm: LCViewmodel, navController: NavController) {
    val sign = vm.inSignUp.value
    val alreadySignIn = remember { mutableStateOf(false) }
    val currentUserType by vm.userType.observeAsState()

    Log.d("checkSignin", "sign: $sign, alreadySignIn: ${alreadySignIn.value}, currentUserType: $currentUserType")

    if (sign && !alreadySignIn.value) {
        if (currentUserType == null) {
            Log.d("checkSignin", "Waiting for currentUserType to be set")
            return // Exit the function if currentUserType is not yet set
        }

        alreadySignIn.value = true
        Log.d("checkSignin", "Value of sign: $sign")
        when (currentUserType) {
            "Patient" -> {
                Log.d("checkSignin", "Navigating to HomeScreen for Patient")
                navController.navigate(Dest.HomeScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                }
            }
            "Doctor" -> {
                Log.d("checkSignin", "Navigating to DoctorProfile for Doctor")
                navController.navigate(Dest.DoctorProfile.route) {
                    popUpTo(navController.graph.startDestinationId)
                }
            }
            else -> {
                Log.w("checkSignin", "Unexpected user type: $currentUserType")
            }
        }
    }
}
