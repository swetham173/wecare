package com.example.convo

import DoctorProfile
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.convo.screens.*
import com.example.convo.ui.theme.ConvoTheme
import com.google.android.gms.common.util.CollectionUtils.listOf
import dagger.hilt.android.AndroidEntryPoint

sealed class Dest(var route:String){
    object Splash:Dest(route = "Splash")
    object HomeScreen:Dest(route = "Home")
    object Login:Dest(route = "Login")
    object PatientProfile:Dest(route = "PatientProfile")
    object DoctorProfile:Dest(route = "Profile")
    object SignUp:Dest(route = "SignUp")
    object PatientChatList:Dest(route = "PatientChatList")
    object SingleChat:Dest(route = "SingleChat/{chatId}/{patientID}"){
        fun createRouteForEachUser(chatId:String,patientID:String)="SingleChat/$chatId/$patientID"
    }
    object EmotionTracker:Dest(route = "EmotionTracker")
    object Journaling:Dest(route = "Journaling")
    object memories:Dest(route="memories")
    object doctorChatlist:Dest(route="doctorChatlist")
    object addnotes : Dest("addnotes?title={title}&description={description}&noteId={noteId}") {
        fun createRoute(title: String, description: String, noteId: String = "") =
            "addnotes?title=$title&description=$description&noteId=$noteId"
    }
}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConvoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavigationHandler()
                }
            }
        }
    }
}

@Composable
fun NavigationHandler(){
    var navController= rememberNavController()
    var vm=hiltViewModel<LCViewmodel>() //function of hilt and class we want to retrieve
    //postman
    NavHost(navController = navController, startDestination = Dest.Splash.route ){
            //letter
        composable(Dest.Splash.route){
            SplashScreen(navController,vm)
        }
            composable(Dest.HomeScreen.route){
                HomeScreen(navController,vm)
            }
            composable(Dest.SignUp.route ){
                    SignUpScreen(navController,vm)
                }
            composable(Dest.Login.route ){
                LoginScreen(navController=navController,vm=vm)
            }
        composable(Dest.PatientChatList.route ){
           PatientChatList(navController, vm)
            }
        composable(Dest.EmotionTracker.route ){
            EmotionTracker(navController, vm)
        }
        composable(Dest.memories.route ){
            Memories(navController,vm)
        }
        composable(Dest.Journaling.route ){
            Journaling(navController, vm)
        }
        composable(Dest.PatientProfile.route ){
            PatientProfile(navController,vm)
        }
        composable(Dest.DoctorProfile.route ){
            DoctorProfile(navController,vm)
        }
        composable(Dest.doctorChatlist.route ){
            DoctorsChatList(navController,vm)
        }
        composable(Dest.SingleChat.route){
            val chatID=it.arguments?.getString("chatId",)
            val patientID=it.arguments?.getString("patientID",)?:""
            if(chatID!=null){
                SingleChat(navController = navController, vm = vm, chatId =chatID, patientID = patientID )
            }
        }
            composable(Dest.Journaling.route) {
                Journaling(navController, vm)
            }
        composable(
            route = Dest.addnotes.route,
            arguments = listOf(
                navArgument("title") { defaultValue = "" },
                navArgument("description") { defaultValue = "" },
                navArgument("noteId") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            AddingNotes(navController, vm, title, description, noteId)
        }
    }


}

