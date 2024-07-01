package com.example.convo.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.convo.*
import com.example.convo.R
import com.google.android.gms.common.util.CollectionUtils.listOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import com.google.android.gms.common.util.CollectionUtils


@Composable
fun SignUpScreen(navController: NavController, vm: LCViewmodel) {
    checkSignin(vm = vm, navController = navController)
        var nameState = remember{
        mutableStateOf(TextFieldValue())
        }
        var phoneState = remember{
            mutableStateOf(TextFieldValue())
        }
        var emailState = remember{
            mutableStateOf(TextFieldValue())
        }
        var passwordState = remember{
            mutableStateOf(TextFieldValue())
        }
        var userType =listOf("Doctor","Patient")

        var ExpandingOption=remember{
            mutableStateOf(false)
        }

        val icon = if (ExpandingOption.value) {
            Icons.Filled.KeyboardArrowUp
        } else
            Icons.Filled.KeyboardArrowDown

        var mSelectedText = remember { mutableStateOf("") }
        val focus= LocalFocusManager.current
        val gradient = Brush.linearGradient(
            colors = CollectionUtils.listOf(
                Color.White,
                colorResource(id = R.color.lightPink)

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
            OutlinedTextField(value = nameState.value,
                onValueChange = { newValue-> nameState.value = newValue },
                label={ Text(text = "Name")},
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(value = phoneState.value,
                onValueChange = { newValue-> phoneState.value = newValue},
                label={ Text(text = "Number")},
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(value = emailState.value,
            onValueChange = { newValue-> emailState.value = newValue},
            label={ Text(text = "Email")},
            modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(value = passwordState.value,
            onValueChange = { newValue-> passwordState.value = newValue},
            label={ Text(text = "Password")},
            modifier = Modifier.padding(8.dp)
        )
                OutlinedTextField(
                    value = mSelectedText.value,
                    modifier = Modifier.padding(8.dp),
                    onValueChange = { }, // Disable value change
                    readOnly = true,

                    label = {Text("Select ur role:)")},
                    trailingIcon = {
                        Icon(icon,"contentDescription",
                            Modifier.clickable { ExpandingOption.value = !ExpandingOption.value })
                        //when you click the UI element associated with this code,
                    // it toggles the state represented by ExpandingOption
                    }
                )
                DropdownMenu(
                    expanded = ExpandingOption.value,
                    onDismissRequest = { ExpandingOption.value = false }


                ){
                    userType.forEach{label->
                        DropdownMenuItem( onClick = {
                            mSelectedText.value = label
                            ExpandingOption.value = false
                            Log.d("Choice", "Selected option: $label")}) {
                            Text(text = label)
                        }
                    }
                }

               Button(onClick = {
                   Log.d("Choice", "mChoice before signup: ${mSelectedText.value}")
                   vm.signUp(nameState.value.text,
                phoneState.value.text,
                emailState.value.text,
                passwordState.value.text,
               mSelectedText.value) },
            modifier = Modifier.padding(8.dp)) {
                Text(text = "Sign up")
            }
            Text(text = "Already a user click here to Login",
            color = Color.Blue,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    navController.navigate( Dest.Login.route)
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

