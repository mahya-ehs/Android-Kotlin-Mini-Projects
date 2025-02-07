package com.example.exercise3

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exercise3.ui.theme.Exercise3Theme
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.exercise3.ui.theme.sour_gummy
import com.example.exercise3.ui.theme.chewy
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.*
import androidx.core.content.FileProvider
import java.io.File
import coil.compose.rememberAsyncImagePainter
import android.Manifest
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.room.Room
import com.google.accompanist.permissions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.InputStream


val SoftBeige = Color(0xFFF7E6CA)
val LightPeach = Color(0xFFF8D49A)
val MintGreen = Color(0xFFBACB95)
val Green = Color(0XFF038175)
val CutePink = Color(0XFFEE897F)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Exercise3Theme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SoftBeige),
                    contentAlignment = Alignment.Center
                ){
                    MyApp()
                }

            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val catInfo = loadDataFromJson(context)

    NavHost(
        navController = navController,
        startDestination = "main_view"
    ) {
        composable("main_view") {
            MainMenu(navController = navController)
        }
        composable("about_cats_view") {
            AllSections(navController = navController, catInfo = catInfo)
        }
        composable("new_view") {
            NewView(navController = navController)
        }
    }
}

data class CatBreed(
    val name: String,
    val description: String,
    val imageResId: String
)

fun loadDataFromJson(context: Context): List<CatBreed> {
    val inputStream = context.resources.openRawResource(R.raw.cats_info)
    val json = inputStream.bufferedReader().use {
        it.readText()
    }
    val type = object : TypeToken<List<CatBreed>>() {}.type

    return Gson().fromJson(json, type)
}


@Composable
fun MainMenu(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "my-db"
    ).build()
    val userDao = remember { db.userDao() }
    var user by remember { mutableStateOf<User?>(null) }


    LaunchedEffect(Unit) {
        user = withContext(Dispatchers.IO) { userDao.getLatestUser() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.i2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(50.dp).padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            user?.let { currentUser ->
                Image(
                    painter = rememberAsyncImagePainter(currentUser.imagePath),
                    contentDescription = "User Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(220.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                )
                Text(
                    text = "Welcome, ${currentUser.username}!",
                )
            } ?: run {
                Text("No user found. Please sign up!")
            }

            Button(
                onClick = {
                    navController.navigate("new_view") {
                        popUpTo("main_menu") { inclusive = false }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightPeach, // Background color
                    contentColor = CutePink         // Text color
                )
            ) {
                Text("Profile",
                    style = TextStyle(
                        fontFamily = sour_gummy,
                        fontSize = 18.sp)
                )
            }
            Button(
                onClick = {
                    navController.navigate("about_cats_view") {
                        popUpTo("main_view") { inclusive = false }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightPeach, // Background color
                    contentColor = CutePink         // Text color
                )
            ) {
                Text("About Cats",
                    style = TextStyle(
                        fontFamily = sour_gummy,
                        fontSize = 18.sp)
                )
            }
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightPeach, // Background color
                    contentColor = CutePink         // Text color
                )
            ) {
                Text("Options",
                    style = TextStyle(
                        fontFamily = sour_gummy,
                        fontSize = 18.sp)
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NewView(navController: NavController) {
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "my-db"
    ).build()

    val userDao = remember { db.userDao() }

    var text by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }

    var submit by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }

    var userImage by remember { mutableStateOf<Uri?>(null) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        user = withContext(Dispatchers.IO) { userDao.getLatestUser() }
        user?.let {
            text = it.username
            userImage = Uri.parse(it.imagePath)
        }
    }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            userImage = uri
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success: Boolean ->
            if (success) {
                userImage = cameraImageUri
            }
        }
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Default
            Image(
                painter = rememberAsyncImagePainter(userImage),
                contentDescription = "User Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(220.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )
            Text(text)


            if (editMode) {
                // textfield
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Username") },
                    placeholder = {
                        if (!isValid) Text("This field is required!")
                    },
                    readOnly = submit
                )

                // add photo button
                ExtendedFloatingActionButton(
                    onClick = {
                        chooseImage(
                            context = context,
                            gallerySelect = {
                                galleryLauncher.launch("image/*") },
                            cameraSelect = {
                                if (cameraPermissionState.status.isGranted) {
                                    val uri = createImageUri(context)
                                    if (uri != null) {
                                        cameraImageUri = uri
                                        cameraLauncher.launch(uri)
                                    }
                                } else {
                                    cameraPermissionState.launchPermissionRequest()
                                }
                            }
                        )
                    },
                    icon = { Icon(Icons.Filled.AddCircle, contentDescription = "Pick Image") },
                    text = { Text(text = "Add Photo") },
                )
                Button(
                    onClick = {
                        if (text.isNotEmpty() && userImage != null) {
                            val imagePath = saveImageToInternalStorage(context, userImage!!)
                            val user = User(username = text, imagePath = imagePath)

                            CoroutineScope(Dispatchers.IO).launch {
                                userDao.insertUser(user)
                            }
                            isValid = true
                            submit = true
                            editMode = false
                        }

                        else {
                            isValid = false
                        }
                    }
                ) {
                    Text("Submit")
                }


            }
            else {
                Button(
                    onClick = {
                        editMode = true
                    }
                ) {
                    Text("Edit")
                }
            }






        }
    }
}

fun saveImageToInternalStorage(context: Context, imageUri: Uri): String {
    val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
    val file = File(context.filesDir, "image_${System.currentTimeMillis()}.jpg")
    var path = ""
    inputStream?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    path = file.absolutePath

    return path
}


fun createImageUri(context: Context): Uri? {
    var counter = 0
    val storageDir = File(context.getExternalFilesDir(null), "Pictures")

    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }

    val file = File(storageDir, "IMG_$counter.jpg")

    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

fun chooseImage(
    context: Context,
    gallerySelect: () -> Unit,
    cameraSelect: () -> Unit
) {

    AlertDialog.Builder(context).apply {
        setTitle("Choose Image Source")
        setItems(arrayOf("Gallery", "Camera")) { _, which ->
            if (which == 0) {
                gallerySelect()
            } else {
                cameraSelect()
            }
        }
        show()
    }
}



@Composable
fun AllSections(
    navController: NavController,
    catInfo: List<CatBreed>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top=30.dp, bottom = 15.dp)

    ) {
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart).padding(start = 15.dp, end=15.dp)
        ) {
            Text("Back")
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 50.dp, bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                AppHeader()
            }

            items(catInfo) { catBreed ->
                AppSection(catBreed = catBreed)
            }
        }
    }
}


@Composable
fun AppHeader() {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "About Cats",
            style = TextStyle(
                fontFamily = chewy,
                fontSize = 32.sp,
                color = Green
            )
        )
        Image(
            painter = painterResource(R.drawable.mainmenu),
            contentDescription = null,
            modifier = Modifier
                .size(250.dp).clip(RoundedCornerShape(16.dp))
        )
    }

}

@Composable
fun AppSection(catBreed: CatBreed) {
    // hoist state
    val expanded = remember { mutableStateOf(false) }
    Surface (
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 1.dp,
        color = MintGreen,
        modifier = Modifier.padding(15.dp)
    ) {
        Column{
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(15.dp)
            ){
                Text(
                    text = catBreed.name,
                    style = TextStyle(
                        fontFamily = sour_gummy,
                        fontSize = 20.sp,
                        color = Green
                    ),
                    modifier = Modifier.weight(1f)
                )

                ElevatedButton(
                    onClick = { expanded.value = !expanded.value },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightPeach, // Background color
                        contentColor = CutePink         // Text color
                    )
                ) {
                    Text(
                        text = if (expanded.value)
                            "close mew"
                        else
                            "about mew",
                        style = TextStyle(
                            fontFamily = sour_gummy,
                            fontSize = 14.sp
                        )
                    )
                }
            }
            if (expanded.value) {
                CatDetails(catBreed = catBreed)
            }
        }


    }

}

@Composable
fun CatDetails(catBreed: CatBreed) {
    val context = LocalContext.current
    val imageResId = remember(catBreed.imageResId) {
        context.resources.getIdentifier(catBreed.imageResId, "drawable", context.packageName)
    }

    Row(
        modifier = Modifier.padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = catBreed.description,
            modifier = Modifier.weight(1f)
        )
        Image(
            painter = painterResource(imageResId),
            contentDescription = catBreed.name,
            modifier = Modifier.size(150.dp).clip(RoundedCornerShape(12.dp))
        )
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun PreviewAllSections() {
//    val sampleCats = listOf(
//        CatBreed(
//            name = "Ragdoll",
//            description = "The human-loving Ragdoll is a relatively young cat breed, with origins dating back only as far as the 1960s.",
//            imageResId = "ragdoll"
//        ),
//        CatBreed(
//            name = "Persian",
//            description = "The Persian cat is a long-haired breed of cat characterized by its round face and short muzzle.",
//            imageResId = "persian"
//        )
//    )
//    AllSections(catInfo = sampleCats)
//}
//
//
//
//
