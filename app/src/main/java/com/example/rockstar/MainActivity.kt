package com.example.rockstar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.rockstar.navigation.RockstarNavGraph
import com.example.rockstar.ui.theme.RockstarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RockstarTheme {
                RockstarNavGraph()
            }
        }
    }
}
