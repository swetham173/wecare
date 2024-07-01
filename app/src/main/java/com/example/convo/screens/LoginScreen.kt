package com.example.convo.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.convo.*
import com.example.convo.R
import com.google.android.gms.common.util.CollectionUtils


@Composable
fun LoginScreen (navController: NavController, vm: LCViewmodel){
    checkSignin(vm = vm, navController = navController)


    var emailState = remember{
        mutableStateOf(TextFieldValue())
    }
    var passwordState = remember{
        mutableStateOf(TextFieldValue())
    }
    val gradient = Brush.linearGradient(
        colors = CollectionUtils.listOf(
            Color.White,
            colorResource(id = R.color.LightBLue)

        )
    )
    Box( modifier = Modifier.fillMaxSize().background(gradient) )

    {

        Column( modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally)

        {

            Image(painter = painterResource
                (id = R.drawable.greenchat),
                contentDescription =null,
                modifier= Modifier
                    .width(200.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )

            OutlinedTextField(value = emailState.value,
                onValueChange = { newValue-> emailState.value = newValue},
                label={ Text(text = "Email")},
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(value = passwordState.value,
                onValueChange = { newValue-> passwordState.value = newValue},
                label={ Text(text = "Password")},
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(8.dp)
            )


            Button(onClick = {
                             vm.loginIn(emailState.value.text,passwordState.value.text)
               },
                modifier = Modifier.padding(8.dp)) {
                Text(text = "Sign In")
            }
            Text(text = "New user click here to Sign Up",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(navController, Dest.SignUp.route)
                    }
            )
        }
    }
    if(vm.inProcess.value){
        commonProgressBar()
    }
    val currentUserType by vm.userType.observeAsState("")
    LaunchedEffect(currentUserType) {
        Log.d("LoginScreen", "Current user type: $currentUserType")

        when (currentUserType) {
            "Patient" -> {
                navController.navigate(Dest.HomeScreen.route)
                Log.d("LoginScreen", "Navigating to HomeScreen")
            }
            "Doctor" -> {
                navController.navigate(Dest.DoctorProfile.route)
                Log.d("LoginScreen", "Navigating to Profile")
            }
        }

    }
}

