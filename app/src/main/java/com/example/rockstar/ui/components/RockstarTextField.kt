package com.example.rockstar.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.rockstar.R
import com.example.rockstar.ui.theme.RockstarAccent
import com.example.rockstar.ui.theme.RockstarError
import com.example.rockstar.ui.theme.RockstarSurfaceElevated
import com.example.rockstar.ui.theme.RockstarTextPrimary
import com.example.rockstar.ui.theme.RockstarTextSecondary

@Composable
fun RockstarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    focusRequester: FocusRequester? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = if (focusRequester != null) modifier.focusRequester(focusRequester) else modifier,
        enabled = enabled,
        isError = isError,
        supportingText = supportingText?.let {
            { Text(text = it, color = RockstarError) }
        },
        placeholder = { Text(placeholder, color = RockstarTextSecondary) },
        leadingIcon = leadingIcon?.let {
            {
                Icon(imageVector = it, contentDescription = null, tint = RockstarTextSecondary)
            }
        },
        trailingIcon = trailingIcon,
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() }
        ),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = RockstarSurfaceElevated,
            focusedContainerColor = RockstarSurfaceElevated,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = RockstarAccent,
            errorIndicatorColor = RockstarError,
            errorContainerColor = RockstarSurfaceElevated,
            focusedTextColor = RockstarTextPrimary,
            unfocusedTextColor = RockstarTextPrimary,
            cursorColor = RockstarAccent
        )
    )
}

@Composable
fun PasswordVisibilityToggle(
    isVisible: Boolean,
    onToggle: () -> Unit
) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
            contentDescription = stringResource(
                if (isVisible) R.string.cd_hide_password else R.string.cd_show_password
            ),
            tint = RockstarTextSecondary
        )
    }
}
