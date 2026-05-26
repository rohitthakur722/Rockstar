package com.example.rockstar

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rockstar.ui.theme.RockstarTheme

class ForgotPasswordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RockstarTheme {
                ForgotPasswordScreen()
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen() {

    val context = LocalContext.current
    val activity = context.findActivity()

    var email by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070B14),
                        Color.Black
                    )
                )
            )
            .padding(24.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        IconButton(
            onClick = {
                activity?.finish()
            }
        ) {

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Forgot Password",
            style = TextStyle(
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Enter your email address and we will send you password reset instructions.",
            style = TextStyle(
                color = Color.Gray,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Enter your email")
            },
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFF141A24),
                focusedContainerColor = Color(0xFF141A24),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color(0xFFD9B08C)
            )
        )

        Spacer(modifier = Modifier.height(35.dp))

        Button(
            onClick = {

                val sharedPreferences =
                    context.getSharedPreferences(
                        "User",
                        Context.MODE_PRIVATE
                    )

                val savedEmail =
                    sharedPreferences.getString(
                        "email",
                        ""
                    )

                if (email == savedEmail) {

                    Toast.makeText(
                        context,
                        "Reset link sent to your email",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    Toast.makeText(
                        context,
                        "Email not found",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD9B08C)
            )
        ) {

            Text(
                text = "Send Reset Link",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Remember password? ",
                color = Color.Gray
            )

            Text(
                text = "Login",
                color = Color(0xFFD9B08C),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    activity?.finish()
                }
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    RockstarTheme {
        ForgotPasswordScreen()
    }
}

/**
 * Extension function to safely find the Activity from a Context,
 * especially useful in Compose Previews where the context might be a BridgeContext.
 */
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}