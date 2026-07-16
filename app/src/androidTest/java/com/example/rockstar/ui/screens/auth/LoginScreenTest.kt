package com.example.rockstar.ui.screens.auth

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rockstar.R
import com.example.rockstar.ui.theme.RockstarTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setLoginContent(
        loading: Boolean = false,
        onLoginClick: () -> Unit = {},
        onRegisterClick: () -> Unit = {},
        onForgotPasswordClick: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            RockstarTheme {
                LoginContent(
                    email = "",
                    onEmailChange = {},
                    password = "",
                    onPasswordChange = {},
                    passwordVisible = false,
                    onPasswordVisibilityToggle = {},
                    onLoginClick = onLoginClick,
                    onRegisterClick = onRegisterClick,
                    onForgotPasswordClick = onForgotPasswordClick,
                    loading = loading
                )
            }
        }
    }

    @Test
    fun tappingCreateOneNavigatesToRegistration() {
        var registerClicked = false
        setLoginContent(onRegisterClick = { registerClicked = true })

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.action_create_one)
        ).performClick()

        assert(registerClicked)
    }

    @Test
    fun tappingForgotPasswordNavigatesToForgotPassword() {
        var forgotPasswordClicked = false
        setLoginContent(onForgotPasswordClick = { forgotPasswordClicked = true })

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.action_forgot_password)
        ).performClick()

        assert(forgotPasswordClicked)
    }

    @Test
    fun loginButtonIsClickableWhenNotLoading() {
        var loginClicked = false
        setLoginContent(loading = false, onLoginClick = { loginClicked = true })

        composeTestRule.onNodeWithTag("login_button").performClick()

        assert(loginClicked)
    }

    @Test
    fun loginButtonIsDisabledWhileLoading() {
        setLoginContent(loading = true)

        composeTestRule.onNodeWithTag("login_button").assertIsNotEnabled()
    }
}
