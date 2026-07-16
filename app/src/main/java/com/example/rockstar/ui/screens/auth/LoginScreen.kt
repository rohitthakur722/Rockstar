package com.example.rockstar.ui.screens.auth

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.testTag
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
import com.example.rockstar.util.Validators
import com.example.rockstar.viewmodel.AuthEvent
import com.example.rockstar.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    viewModel: UserViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val invalidEmailMessage = stringResource(R.string.error_invalid_email)
    val emptyPasswordMessage = stringResource(R.string.error_empty_password)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.NavigateToHome -> onLoginSuccess()
                is AuthEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is AuthEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                else -> Unit
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoginContent(
            email = email,
            onEmailChange = {
                email = it
                emailError = null
            },
            password = password,
            onPasswordChange = {
                password = it
                passwordError = null
            },
            passwordVisible = passwordVisible,
            onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
            emailError = emailError,
            passwordError = passwordError,
            loading = authState.isLoading,
            onLoginClick = {
                val isEmailValid = Validators.isValidEmail(email)
                val isPasswordValid = password.isNotBlank()
                emailError = if (isEmailValid) null else invalidEmailMessage
                passwordError = if (isPasswordValid) null else emptyPasswordMessage
                if (isEmailValid && isPasswordValid) {
                    viewModel.login(email, password)
                }
            },
            onRegisterClick = onNavigateToRegister,
            onForgotPasswordClick = onNavigateToForgotPassword
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
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
    emailError: String? = null,
    passwordError: String? = null,
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
                        isError = emailError != null,
                        supportingText = emailError,
                        enabled = !loading,
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
                        isError = passwordError != null,
                        supportingText = passwordError,
                        enabled = !loading,
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
                        enabled = !loading,
                        loading = loading,
                        modifier = Modifier.testTag("login_button")
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
    }
}
