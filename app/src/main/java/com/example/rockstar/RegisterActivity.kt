package com.example.rockstar

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.rockstar.ui.theme.RockstarTheme

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rockstar.model.UserModel
import com.example.rockstar.repo.UserRepoImpl
import com.example.rockstar.viewmodel.UserViewModel
import com.example.rockstar.viewmodel.UserViewModelFactory

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RockstarTheme {
                val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepoImpl()))
                RegisterScreen(userViewModel)
            }
        }
    }
}

@Composable
fun RegisterScreen(viewModel: UserViewModel) {

    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val loading by viewModel.loading.observeAsState(initial = false)
    val message by viewModel.message.observeAsState(initial = "")

    LaunchedEffect(message) {
        if (!message.isNullOrEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    RegisterContent(
        username = username,
        onUsernameChange = { username = it },
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        passwordVisibility = visibility,
        onPasswordVisibilityToggle = { visibility = !visibility },
        loading = loading ?: false,
        onCreateAccountClick = {
            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@RegisterContent
            }

            val userModel = UserModel(
                username = username,
                email = email
            )

            viewModel.register(email, password, userModel) { success ->
                if (success) {
                    val intent = Intent(context, LoginScreen::class.java)
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
            }
        },
        onLoginClick = {
            val intent = Intent(
                context,
                LoginScreen::class.java
            )

            context.startActivity(intent)

            (context as? Activity)?.finish()
        }
    )
}

@Composable
fun RegisterContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisibility: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onLoginClick: () -> Unit,
    loading: Boolean = false
) {
    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFD9B08C))
        }
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(90.dp))

        Text(
            text = "Create Account",
            style = TextStyle(
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Join Rockstar and enjoy music",
            style = TextStyle(
                color = Color.Gray,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(45.dp))

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Username")
            },
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFF141A24),
                focusedContainerColor = Color(0xFF141A24),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color(0xFFD9B08C)
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Email")
            },
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFF141A24),
                focusedContainerColor = Color(0xFF141A24),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color(0xFFD9B08C)
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,

            visualTransformation =
                if (passwordVisibility)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

            trailingIcon = {

                IconButton(
                    onClick = onPasswordVisibilityToggle
                ) {

                    Icon(
                        imageVector =
                            if (passwordVisibility)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },

            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Password")
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
            onClick = onCreateAccountClick,

            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD9B08C)
            )
        ) {

            Text(
                text = "Create Account",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Row {

            Text(
                text = "Already have an account? ",
                color = Color.Gray
            )

            Text(
                text = "Login",
                color = Color(0xFFD9B08C),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RockstarTheme {
        RegisterScreen()
    }
}