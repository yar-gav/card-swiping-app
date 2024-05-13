package com.example.cardswipingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.cardswipingapp.ui.theme.CardSwipingAppTheme

class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CardSwipingAppTheme {
                MainActivityScreen(mainActivityViewModel = mainActivityViewModel)
            }
        }
    }
}
