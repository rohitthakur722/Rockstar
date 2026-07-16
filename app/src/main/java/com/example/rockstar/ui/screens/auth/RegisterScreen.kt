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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary
import com.example.rockstar.util.Validators
import com.example.rockstar.viewmodel.AuthEvent
import com.example.rockstar.viewmodel.UserViewModel

@Composable
fun RegisterScreen(
    viewModel: UserViewModel,
    onRegistrationSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val invalidNameMessage = stringResource(R.string.error_invalid_full_name)
    val invalidEmailMessage = stringResource(R.string.error_invalid_email)
    val weakPasswordMessage = stringResource(R.string.error_weak_password)
    val passwordMismatchMessage = stringResource(R.string.error_password_mismatch)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.NavigateToHome -> onRegistrationSuccess()
                is AuthEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is AuthEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                else -> Unit
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RegisterContent(
            fullName = fullName,
            onFullNameChange = {
                fullName = it
                fullNameError = null
            },
            email = email,
            onEmailChange = {
                email = it
                emailError = null
            },
            password = password,
            onPasswordChange = {
                password = it
                passwordError = null
                confirmPasswordError = null
            },
            confirmPassword = confirmPassword,
            onConfirmPasswordChange = {
                confirmPassword = it
                confirmPasswordError = null
            },
            passwordVisible = passwordVisible,
            onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
            fullNameError = fullNameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            loading = authState.isLoading,
            onCreateAccountClick = {
                val isNameValid = Validators.isValidFullName(fullName)
                val isEmailValid = Validators.isValidEmail(email)
                val isPasswordValid = Validators.isValidPassword(password)
                val doPasswordsMatch = Validators.doPasswordsMatch(password, confirmPassword)

                fullNameError = if (isNameValid) null else invalidNameMessage
                emailError = if (isEmailValid) null else invalidEmailMessage
                passwordError = if (isPasswordValid) null else weakPasswordMessage
                confirmPasswordError = if (isPasswordValid && !doPasswordsMatch) {
                    passwordMismatchMessage
                } else {
                    null
                }

                if (isNameValid && isEmailValid && isPasswordValid && doPasswordsMatch) {
                    viewModel.register(fullName, email, password)
                }
            },
            onLoginClick = onNavigateToLogin,
            onBack = onBack
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun RegisterContent(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBack: () -> Unit = {},
    fullNameError: String? = null,
    emailError: String? = null,
    passwordError: String? = null,
    confirmPasswordError: String? = null,
    loading: Boolean = false
) {
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }

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
                .padding(24.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = RockstarTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineLarge,
                color = RockstarTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.register_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = RockstarTextSecondary
            )

            Spacer(modifier = Modifier.height(36.dp))

            RockstarTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                placeholder = stringResource(R.string.label_full_name),
                leadingIcon = Icons.Default.Person,
                imeAction = ImeAction.Next,
                onImeAction = { emailFocusRequester.requestFocus() },
                isError = fullNameError != null,
                supportingText = fullNameError,
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            RockstarTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = stringResource(R.string.placeholder_email),
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onImeAction = { passwordFocusRequester.requestFocus() },
                focusRequester = emailFocusRequester,
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
                imeAction = ImeAction.Next,
                onImeAction = { confirmPasswordFocusRequester.requestFocus() },
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

            Spacer(modifier = Modifier.height(16.dp))

            RockstarTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                placeholder = stringResource(R.string.placeholder_confirm_password),
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                passwordVisible = passwordVisible,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onImeAction = onCreateAccountClick,
                focusRequester = confirmPasswordFocusRequester,
                isError = confirmPasswordError != null,
                supportingText = confirmPasswordError,
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            RockstarPrimaryButton(
                text = stringResource(R.string.action_create_account),
                onClick = onCreateAccountClick,
                enabled = !loading,
                loading = loading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(R.string.prompt_have_account),
                    color = RockstarTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.action_login_link),
                    color = RockstarAccent,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable(onClick = onLoginClick)
                )
            }
        }
    }
}
