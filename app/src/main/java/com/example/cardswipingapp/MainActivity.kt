package com.example.cardswipingapp

import StackedSwipeCardView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.cardswipingapp.ui.gestures.HorizontalSwipeGesture
import com.example.cardswipingapp.ui.theme.CardSwipingAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), HorizontalSwipeGesture.HorizontalSwipeGestureHandler {

    var cardDataList = MutableStateFlow(mutableListOf("one", "two", "three", "four", "five", "six", "seven"))
    var backgroundColor = MutableStateFlow(Color.White)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CardSwipingAppTheme {
                // A surface container using the 'background' color from the theme
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                ) {
                    val uiState = cardDataList.collectAsState(initial = listOf<String>())
                    val color = backgroundColor.collectAsState(initial = Color.White)
                    StackedSwipeCardView(
                        modifier = Modifier.fillMaxSize(0.8f).align(Alignment.Center),
                        cardDataList = uiState.value,
                        visibleCards = 4,
                        horizontalSwipeGestureHandler = this@MainActivity,
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize(),
                            shape = RoundedCornerShape(24.dp),
                            elevation = 2.dp,
                            backgroundColor =
                            if (uiState.value.first() == it) {
                                color.value
                            } else {
                                Color.White
                            }
                        ) {
                            Column(Modifier.fillMaxSize()) {
                                Text(
                                    "Some card view text: # $it",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(vertical = 20.dp)
                                )
                            }
                        }
                    }
                    val thresholdDirection = when (color.value) {
                        Color.Red -> "right"
                        Color.Green -> "left"
                        else -> ""
                    }
                        Text("reached threshold: $thresholdDirection", modifier = Modifier.align(
                            Alignment.BottomCenter).fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }
        }
    }

    override fun onSwipeRight() {
//        lifecycleScope.launch {
            cardDataList.value = cardDataList.value.subList(1, cardDataList.value.size)
            backgroundColor.value = Color.White
//        }
    }

    override fun onSwipeLeft() {
        cardDataList.value = cardDataList.value.subList(1, cardDataList.value.size)
        backgroundColor.value = Color.White
    }

    override fun onRightThresholdReached() {
        backgroundColor.value = Color.Red
    }

    override fun onLeftThresholdReached() {
        backgroundColor.value = Color.Green

    }

    override fun onNoThresholdReached() {
        backgroundColor.value = Color.White
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