package com.example.convo.screens
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.convo.R
import com.example.convo.LCViewmodel
import com.google.android.gms.common.util.CollectionUtils
import java.io.File
import java.io.IOException

@Composable
fun Memories(navController: NavController, vm: LCViewmodel) {
    val context = LocalContext.current
    val capturedImageUri = remember { mutableStateOf<Uri?>(null) }
    val photoUris = remember { mutableStateListOf<Uri>() }

    LaunchedEffect(Unit) {
        vm.fetchPhotos { uris ->
            photoUris.addAll(uris)
        }
    }

    // Callback for handling the result of taking a picture
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val uri = capturedImageUri.value
            if (uri != null) {
                vm.uploadPhoto(uri) { isSuccess, downloadUri ->
                    if (isSuccess && downloadUri != null) {
                        // Update  with the new downloadUri
                        photoUris.add(downloadUri)

                    }
                }
            }
        }
    }
    val onDeletePhoto: (Uri) -> Unit = { imageUri ->
        vm.deletePhoto(imageUri,
            onSuccess = {
                photoUris.remove(imageUri)
            },
            onFailure = { message ->
                Toast.makeText(context, "Failed to delete image: $message", Toast.LENGTH_SHORT).show()
            }
        )
    }
    val gradient = Brush.linearGradient(
        colors = CollectionUtils.listOf(
            colorResource(id = R.color.LightBLue),
            colorResource(id = R.color.StrongPink)

        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .border(
                BorderStroke(5.dp, Color.Black), shape = RoundedCornerShape(16.dp)
            )
            .background(gradient)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (photoUris.isEmpty()) {

                Text("No Images Available")
            } else {
                // Show uploaded photos
                PhotoGrid(photoUris, onDeletePhoto)
            }
        }


    }
    Box()
    {
        CameraSelectionCard(
            takePicture,
            context,
            capturedImageUri,
            Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun PhotoGrid(  photoUris: List<Uri>,
                onDeletePhoto: (Uri) -> Unit) {
    Column {
        photoUris.forEach { uri ->
            CapturedImageCard(
                imageUri = uri,
                onDeleteClicked = { onDeletePhoto(uri) }
            )
        }
    }
}

@Composable
fun CameraSelectionCard(
    takePicture: ActivityResultLauncher<Uri>,
    context: Context,
    capturedImageUri: MutableState<Uri?>,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.linearGradient(
        colors = CollectionUtils.listOf(
            colorResource(id = R.color.LightBLue),
            colorResource(id = R.color.StrongPink)

        )
    )
    Box(modifier=Modifier.height(200.dp)) {}

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .border(BorderStroke(5.dp, Color.Black), shape = RoundedCornerShape(16.dp))
            .background(gradient)
        ,
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                val photoFile: File? = try {
                    createImageFile(context)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }

                if (photoFile != null) {
                    //FileProvider.getUriForFile generates a content URI for the created file.
                    // This URI is necessary to give the camera app permission to write to the file.
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        photoFile
                    )
                    capturedImageUri.value = photoURI
                    takePicture.launch(photoURI)
                } else {
                    // Handle the error case where the file could not be created
                    Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Take Picture",
                    tint = Color.White,
                    modifier = Modifier.size(45.dp)
                )
                Text(text = "  Take picture")
            }
        }
    }
}

@Composable
fun CapturedImageCard(imageUri: Uri, onDeleteClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Log.d("CapturedImageCard", "Image URI: $imageUri")
        Box {
            Image(
                painter = rememberImagePainter(data = imageUri),
                contentDescription = "Captured Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(1.5f)
            )
            IconButton(
                onClick = onDeleteClicked,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                // Replace R.drawable.ic_delete with your actual delete icon drawable
                Icon(
                    painter = painterResource(id = R.drawable.deleteicon),
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.size(45.dp)
                )
            }
        }
    }
    }
        private fun createImageFile(context: Context): File {
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${System.currentTimeMillis()}_",
                ".jpg",
                storageDir
            )
        }
