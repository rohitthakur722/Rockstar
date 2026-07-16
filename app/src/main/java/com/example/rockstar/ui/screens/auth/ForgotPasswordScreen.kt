package com.example.rockstar.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.widget.Toast
import com.example.rockstar.R
import com.example.rockstar.ui.components.RockstarPrimaryButton
import com.example.rockstar.ui.components.RockstarTextField
import com.example.rockstar.ui.theme.RockstarAccent
import com.example.rockstar.ui.theme.RockstarBackground
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary
import com.example.rockstar.viewmodel.AuthUiState
import com.example.rockstar.viewmodel.UserViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: UserViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val fillEmailMessage = stringResource(R.string.error_fill_email)
    var email by remember { mutableStateOf("") }

    ForgotPasswordContent(
        email = email,
        onEmailChange = { email = it },
        authState = authState,
        onSendResetLinkClick = {
            if (email.isBlank()) {
                Toast.makeText(context, fillEmailMessage, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.forgotPassword(email)
            }
        },
        onBack = onBack
    )
}

@Composable
fun ForgotPasswordContent(
    email: String,
    onEmailChange: (String) -> Unit,
    authState: AuthUiState,
    onSendResetLinkClick: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RockstarBackground)
            .safeDrawingPadding()
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
            text = stringResource(R.string.forgot_password_title),
            style = MaterialTheme.typography.headlineLarge,
            color = RockstarTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.forgot_password_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = RockstarTextSecondary
        )

        Spacer(modifier = Modifier.height(40.dp))

        RockstarTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.placeholder_email),
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            onImeAction = onSendResetLinkClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (authState) {
            is AuthUiState.Success -> Text(
                text = authState.message,
                color = RockstarAccent,
                style = MaterialTheme.typography.bodyMedium
            )
            is AuthUiState.Error -> Text(
                text = authState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            else -> Unit
        }

        Spacer(modifier = Modifier.height(20.dp))

        RockstarPrimaryButton(
            text = stringResource(R.string.action_send_reset_link),
            onClick = onSendResetLinkClick,
            loading = authState is AuthUiState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(R.string.prompt_remember_password),
                color = RockstarTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.action_login_link),
                color = RockstarAccent,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable(onClick = onBack)
            )
        }
    }
}
