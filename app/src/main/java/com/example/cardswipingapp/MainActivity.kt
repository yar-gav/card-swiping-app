package com.example.cardswipingapp

import StackedSwipeCardView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.cardswipingapp.ui.gestures.HorizontalSwipeGesture
import com.example.cardswipingapp.ui.theme.CardSwipingAppTheme

class MainActivity : ComponentActivity(), HorizontalSwipeGesture.HorizontalSwipeGestureHandler {

    var cardDataList = mutableListOf("one", "two", "three", "four", "five", "six", "seven")
    var backgroundColor = Color.White

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CardSwipingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    StackedSwipeCardView(
                        modifier = Modifier.fillMaxSize(),
                        cardDataList = cardDataList,
                        visibleCards = 4,
                        horizontalSwipeGestureHandler = this,
                    ) {
                        Text(modifier = Modifier.fillMaxSize().background(color = backgroundColor), text = it)
                    }
                }
            }
        }
    }

    override fun onSwipeRight() {
        cardDataList.removeAt(0)
    }

    override fun onSwipeLeft() {
        cardDataList.removeAt(0)
    }

    override fun onRightThresholdReached() {
        backgroundColor = Color.Red
    }

    override fun onLeftThresholdReached() {
        backgroundColor = Color.Green
    }

    override fun onNoThresholdReached() {
        backgroundColor = Color.White
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CardSwipingAppTheme {
        Greeting("Android")
    }
}