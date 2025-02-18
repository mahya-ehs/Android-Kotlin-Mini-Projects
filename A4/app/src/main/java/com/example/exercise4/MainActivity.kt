package com.example.exercise4

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.exercise4.ui.theme.chewy
import com.example.exercise4.ui.theme.sour_gummy


val Blue = Color(0XFF384E77)
val TeaGreen = Color(0XFFD3F9B5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SensorService.createNotificationChannel(this)

        setContent {
            val context = this

            val requestPermissionLauncher = rememberLauncherForActivityResult (
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (!isGranted) {
                        println("Notification permission denied!")
                    }
                }
            )

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            ShakeDetection(context = this)
        }
    }
}

@Composable
fun ShakeDetection(context: Context) {
    val shakeDetected by SensorService.shakeState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.i7),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            if (shakeDetected) {
                Text(
                    text = "Meow! \uD83D\uDC3E",
                    style = TextStyle(
                        fontFamily = chewy,
                        fontSize = 30.sp,
                        color = Blue
                    )
                )
            } else {
                Text(
                    text = "Hit Meow button\nThen shake your phone to start meowing...",
                    style = TextStyle(
                        fontFamily = chewy,
                        fontSize = 20.sp,
                        color = Blue
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val serviceIntent = Intent(context, SensorService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(12.dp))
                    .background(TeaGreen, shape = RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TeaGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Meow",
                    style = TextStyle(
                        fontFamily = sour_gummy,
                        fontSize = 20.sp,
                        color = Blue
                    )
                )
            }

//
//            Button(
//                onClick = {
//                    val serviceIntent = Intent(context, SensorService::class.java)
//                    context.stopService(serviceIntent)
//                },
//                modifier = Modifier.fillMaxWidth().padding(8.dp)
//            ) {
//                Text("Stop Meowing")
//            }
        }
    }

}

