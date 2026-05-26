package com.example.rockstar

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rockstar.ui.theme.RockstarTheme
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RockstarTheme {
                SplashBody(
                    onFinished = {
                        val intent = Intent(this, LoginScreen::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SplashBody(
    onFinished: () -> Unit = {}
) {

    var startAnimation by remember {
        mutableStateOf(false)
    }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(
            durationMillis = 1200
        ),
        label = ""
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1200
        ),
        label = ""
    )

    LaunchedEffect(Unit) {

        startAnimation = true

        delay(2500)

        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070B14),
                        Color.Black
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "ROCKSTAR",
                style = TextStyle(
                    color = Color(0xFFD9B08C),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Everyone is a rockstar and we feel the music",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 18.sp
                ),
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SplashBodyPreview() {
    RockstarTheme {
        SplashBody()
    }
}