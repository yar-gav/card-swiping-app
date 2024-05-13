package com.example.cardswipingapp

import StackedSwipeCardView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cardswipingapp.ui.theme.Typography

@Composable
fun MainActivityScreen(mainActivityViewModel: MainActivityViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
    ) {
        val uiState = mainActivityViewModel.uiState.collectAsState(
            initial = MainActivityViewModel.UiState(
                listOf(), Color.White, ""
            )
        )
        StackedSwipeCardView(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .align(Alignment.Center),
            cardDataList = uiState.value.cardDataList,
            visibleCards = 4,
            horizontalSwipeGestureHandler = mainActivityViewModel,
        ) {
            CardContent(uiState, it)
        }
        ThresholdReachedText(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = uiState.value.thresholdReachedText,
        )
    }
}

@Composable
private fun CardContent(
    uiState: State<MainActivityViewModel.UiState>,
    cardData: String
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        elevation = 4.dp,
        backgroundColor = if (uiState.value.cardDataList.first() == cardData) {
            uiState.value.firstCardBackgroundColor
        } else {
            Color.White
        }
    ) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = "Card\n$cardData",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 20.dp),
                style = Typography.h1
            )
        }
    }
}

@Composable
private fun ThresholdReachedText(modifier: Modifier, text: String) {
    Text(
        "Reached threshold: $text",
        modifier = modifier.fillMaxWidth().padding(vertical = 12.dp),
        textAlign = TextAlign.Center,
        style = Typography.body1
    )
}


