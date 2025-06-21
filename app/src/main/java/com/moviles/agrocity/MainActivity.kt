package com.moviles.agrocity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moviles.agrocity.viewmodel.LoginActivity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.moviles.agrocity.ui.screens.MainScreen
import com.moviles.agrocity.ui.theme.AgrocityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgrocityTheme {
                MainScreen() // contiene NavHost y tus pantallas
            }
        }
    }
}
