
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.convo.Dest
import com.example.convo.LCViewmodel
import com.example.convo.R
import com.example.convo.navigateTo
import com.google.android.gms.common.util.CollectionUtils

@Composable
fun DoctorProfile(navController: NavController, vm: LCViewmodel) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("DoctorProfile", Context.MODE_PRIVATE)

    val savedName = remember { sharedPreferences.getString("name", "") ?: "" }
    val savedDescription = remember { sharedPreferences.getString("desc", "") ?: "" }

    val nameState = rememberSaveable { mutableStateOf(savedName) }
    val descState = rememberSaveable { mutableStateOf(savedDescription) }

    val profileImageUrl = vm.doctorData.value?.imageURL
    // Check if the URI can be accessed
    val gradient = Brush.linearGradient(
        colors = CollectionUtils.listOf(
            colorResource(id = R.color.LightBLue),
            colorResource(id = R.color.StrongPink)

        )
    )
    Box(
        modifier = Modifier
            .border(BorderStroke(5.dp, Color.Black), shape = RoundedCornerShape(16.dp))
            .background(gradient) // Added padding for the Box
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally // Ensuring Column fills the Box
        ) {
            Box(
                modifier = Modifier.fillMaxWidth() // Ensuring Box fills the width of the Column
            ) {
                Button(
                    onClick = {
                        vm.createOrUpdateDoctorProfile(
                            name = nameState.value,
                            descri = descState.value,
                            imageURL = profileImageUrl
                        )

                        sharedPreferences.edit()
                            .putString("name", nameState.value)
                            .putString("desc", descState.value)
                            .apply()
                    },
                    modifier = Modifier.align(Alignment.TopEnd).padding(horizontal = 16.dp, vertical = 10.dp) // Align Button within the inner Box
                ) {
                    Text(text = "Save")
                }
            }

                profileimage(profileImageUrl?:"", vm)

            Spacer(modifier = Modifier.height(34.dp))

            // Text field for entering doctor's name
            TextField(
                value = nameState.value, // Initialize with empty string
                onValueChange = { newValue -> nameState.value = newValue },
                label = { Text("Enter your name") },
                modifier = Modifier.width(300.dp),shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Text field for entering doctor's description
            TextField(
                value = descState.value, // Initialize with empty string
                onValueChange = { newValue -> descState.value = newValue },
                label = { Text("Enter your description") },
                modifier = Modifier.width(300.dp),
                maxLines = 3,shape = RoundedCornerShape(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        Button(
            onClick = {
                vm.logout()
                navigateTo(navController,Dest.SignUp.route)
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text("Logout")
        }



    }

    Box(modifier = Modifier.padding(10.dp)) {
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            Surface(color = Color.White, shape = RoundedCornerShape(10.dp), elevation = 10.dp) {
                BottomNavigation(
                    backgroundColor = Color.LightGray,
                    modifier = Modifier.height(40.dp)
                ) {
                    BottomNavigationItem(selected = false, onClick = {
                        navigateTo(navController, Dest.doctorChatlist.route)
                    },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.chaticon),
                                contentDescription = null,
                                modifier = Modifier.width(20.dp)
                            )
                        })
                    BottomNavigationItem(
                        selected = true, onClick = {
                        navigateTo(navController, Dest.DoctorProfile.route)
                    },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = null,
                                modifier = Modifier.width(20.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun profileimage(imageUrl:String,vm: LCViewmodel){
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {

            Log.d("DoctorProfile", "Image URI selected: $uri")
            vm.uploadProfileImage(uri)

        }
    }
    Box(
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl == null) {
            Image(
                painter = painterResource(id = R.drawable.userdoctor),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable {
                        launcher.launch("image/*")
                    }
            )
        } else {
            val painter = rememberImagePainter(
                data = imageUrl,
                builder = {
                    error(R.drawable.userdoctor) // Add a placeholder for error state
                }
            )

            Image(
                painter = painter,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable {
                        launcher.launch("image/*")
                    }
            )
        }
    }
}
