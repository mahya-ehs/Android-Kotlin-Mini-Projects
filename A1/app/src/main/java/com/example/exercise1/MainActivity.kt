package com.example.exercise1

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
import com.example.exercise1.ui.theme.Exercise1Theme
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.exercise1.ui.theme.sour_gummy
import com.example.exercise1.ui.theme.sour_gummy_bold
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
            Exercise1Theme {
                val catInfo = loadDataFromJson(this)
                Box(Modifier.background(SoftBeige)){
                    AllSections(catInfo)
                }

            }
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
fun AllSections(
    catInfo: List<CatBreed>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(top=100.dp, bottom = 50.dp),
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


@Composable
fun AppHeader() {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "About Cats",
            style = TextStyle(
                fontFamily = sour_gummy_bold,
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
