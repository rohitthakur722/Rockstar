package com.example.rockstar.ui.screens.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rockstar.R
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.ui.components.EmptyState
import com.example.rockstar.ui.components.RockstarAppScaffold
import com.example.rockstar.ui.components.RockstarPrimaryButton
import com.example.rockstar.viewmodel.UserViewModel

@Composable
fun AccountScreen(
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit,
    viewModel: UserViewModel,
    onLoggedOut: () -> Unit
) {
    RockstarAppScaffold(
        title = stringResource(R.string.nav_account),
        currentRoute = currentRoute,
        onTabSelected = onTabSelected
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            EmptyState(
                icon = Icons.Default.Person,
                title = stringResource(R.string.account_title),
                message = stringResource(R.string.empty_account_message),
                modifier = Modifier.weight(1f)
            )

            RockstarPrimaryButton(
                text = stringResource(R.string.action_log_out),
                onClick = { viewModel.logout(onLoggedOut) },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }
    }
}
