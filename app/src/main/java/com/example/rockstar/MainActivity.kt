package com.example.rockstar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.rockstar.navigation.RockstarNavGraph
import com.example.rockstar.ui.theme.RockstarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences by (application as RockstarApplication).container.preferencesRepository.preferences.collectAsState(
                initial = com.example.rockstar.data.preferences.AppPreferences()
            )
            RockstarTheme(themeMode = preferences.themeMode) {
                RockstarNavGraph()
            }
        }
    }
}
