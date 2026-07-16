package com.example.rockstar.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rockstar.R
import com.example.rockstar.ui.components.PasswordVisibilityToggle
import com.example.rockstar.ui.components.RockstarPrimaryButton
import com.example.rockstar.ui.components.RockstarTextField
import com.example.rockstar.ui.theme.RockstarAccent
import com.example.rockstar.ui.theme.RockstarBackground
import com.example.rockstar.ui.theme.RockstarSurface
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary
import com.example.rockstar.viewmodel.AuthUiState
import com.example.rockstar.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    viewModel: UserViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val fillEmailPasswordMessage = stringResource(R.string.error_fill_email_password)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.Success -> {
                onLoginSuccess()
                viewModel.resetAuthState()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetAuthState()
            }
            else -> Unit
        }
    }

    LoginContent(
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        passwordVisible = passwordVisible,
        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
        loading = authState is AuthUiState.Loading,
        onLoginClick = {
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(context, fillEmailPasswordMessage, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(email, password)
            }
        },
        onRegisterClick = onNavigateToRegister,
        onForgotPasswordClick = onNavigateToForgotPassword
    )
}

@Composable
fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit = {},
    loading: Boolean = false
) {
    val passwordFocusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RockstarBackground)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.app_name).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = RockstarAccent
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Surface(
                color = RockstarSurface,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(R.string.login_welcome_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = RockstarTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.login_welcome_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = RockstarTextSecondary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    RockstarTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        placeholder = stringResource(R.string.placeholder_email),
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        onImeAction = { passwordFocusRequester.requestFocus() },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    RockstarTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        placeholder = stringResource(R.string.placeholder_password),
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        onImeAction = onLoginClick,
                        focusRequester = passwordFocusRequester,
                        trailingIcon = {
                            PasswordVisibilityToggle(
                                isVisible = passwordVisible,
                                onToggle = onPasswordVisibilityToggle
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            text = stringResource(R.string.action_forgot_password),
                            color = RockstarAccent,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.clickable(onClick = onForgotPasswordClick)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    RockstarPrimaryButton(
                        text = stringResource(R.string.action_login),
                        onClick = onLoginClick,
                        loading = loading
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(color = RockstarTextSecondary.copy(alpha = 0.2f))

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text(
                            text = stringResource(R.string.prompt_no_account),
                            color = RockstarTextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(R.string.action_create_one),
                            color = RockstarAccent,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.clickable(onClick = onRegisterClick)
                        )
                    }
                }
            }
        }

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = RockstarAccent
            )
        }
    }
}
