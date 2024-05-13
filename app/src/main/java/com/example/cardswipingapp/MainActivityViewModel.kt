package com.example.cardswipingapp

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.cardswipingapp.ui.gestures.HorizontalSwipeGesture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivityViewModel : ViewModel(), HorizontalSwipeGesture.HorizontalSwipeGestureHandler {

    private val _uiState = MutableStateFlow(
        UiState(
            cardDataList = mutableListOf("one", "two", "three", "four", "five", "six", "seven"),
            firstCardBackgroundColor = Color.White,
            thresholdReachedText = ""
        )
    )
    val uiState: StateFlow<UiState> = _uiState

    override fun onSwipeRight() {
        _uiState.value = UiState(
            cardDataList = _uiState.value.cardDataList.subList(1, _uiState.value.cardDataList.size),
            firstCardBackgroundColor = Color.White,
            thresholdReachedText = ""
        )

    }

    override fun onSwipeLeft() {
        _uiState.value =
            UiState(
                cardDataList = _uiState.value.cardDataList.subList(
                    1,
                    _uiState.value.cardDataList.size
                ),
                firstCardBackgroundColor = Color.White,
                thresholdReachedText = ""
            )
    }

    override fun onRightThresholdReached() {
        _uiState.value = _uiState.value.copy(
            firstCardBackgroundColor = Color.Green,
            thresholdReachedText = "right"
        )
    }

    override fun onLeftThresholdReached() {
        _uiState.value =
            _uiState.value.copy(firstCardBackgroundColor = Color.Red, thresholdReachedText = "left")
    }

    override fun onNoThresholdReached() {
        _uiState.value =
            _uiState.value.copy(firstCardBackgroundColor = Color.White, thresholdReachedText = "")
    }

    data class UiState(
        val cardDataList: List<String>,
        val firstCardBackgroundColor: Color,
        val thresholdReachedText: String
    )

}