package com.example.cardswipingapp.ui.gestures

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

object HorizontalSwipeGesture {
    // Based on the example found here: https://developer.android.com/develop/ui/compose/animation/advanced
    /**
     * [thresholdPercent] percentage of the screen width which defines the threshold from which
     * or [horizontalSwipeGestureHandler] is called, and card is animated off the screen.
     * [rotationInverseRatio] defines the inverse ratio between the offset and the rotation of the card.
     * - if [rotationInverseRatio] is 1, there is no rotation at all
     * - as [rotationInverseRatio] increases, the rotation becomes less extreme
     * - as [rotationInverseRatio] decreases, the rotation becomes more extreme
     */
    fun Modifier.horizontalSwipeable(
        thresholdPercent: Float = 0.33f,
        rotationInverseRatio: Float = 60f,
        horizontalSwipeGestureHandler: HorizontalSwipeGestureHandler
    ): Modifier = this.composed {
        val thresholdReachedTargetValue = getThresholdReachedTargetValue()
        val threshold = getThreshold(thresholdPercent = thresholdPercent)
        val offsetX = remember { Animatable(0f) }

        pointerInput(Unit) {
            // Use suspend functions for touch events and the Animatable.
            coroutineScope {
                while (true) {
                    // Stop any ongoing animation.
                    offsetX.stop()
                    awaitPointerEventScope {
                        // Detect a touch down event.
                        val pointerId = awaitFirstDown().id

                        horizontalDrag(pointerId) { change ->
                            // Update the animation value with touch events.
                            launch {
                                offsetX.snapTo(
                                    offsetX.value + change.positionChange().x
                                )
                                when {
                                    offsetX.value <= -threshold -> horizontalSwipeGestureHandler.onLeftThresholdReached()
                                    offsetX.value >= threshold -> horizontalSwipeGestureHandler.onRightThresholdReached()
                                    else -> horizontalSwipeGestureHandler.onNoThresholdReached()
                                }
                            }
                        }
                    }
                    // No longer receiving touch events. Prepare the animation.
                    launch {
                        val externalSwipeAnimationSpec: AnimationSpec<Float> = tween(150, easing = EaseInOutCubic)
                        when {
                            offsetX.value <= -threshold -> {
                                offsetX.animateTo(
                                    targetValue = -thresholdReachedTargetValue,
                                    externalSwipeAnimationSpec
                                )
                                horizontalSwipeGestureHandler.onSwipeLeft()
                            }

                            offsetX.value >= threshold -> {
                                offsetX.animateTo(
                                    targetValue = thresholdReachedTargetValue,
                                    externalSwipeAnimationSpec
                                )
                                horizontalSwipeGestureHandler.onSwipeRight()
                            }

                            else -> {
                                // Didn't reach threshold; Slide back.
                                offsetX.animateTo(targetValue = 0f)
                            }
                        }
                    }
                }
            }
        }
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer(
                rotationZ = offsetX.value / rotationInverseRatio
            )
    }

    @Composable
    fun getThresholdReachedTargetValue(): Float {
        val density = LocalDensity.current.density
        val screenWidthDp = LocalConfiguration.current.screenWidthDp
        // Use a value two times the screen width to guarantee card is animated off the screen
        return screenWidthDp * density * 2
    }

    @Composable
    fun getThreshold(thresholdPercent: Float): Float {
        val density = LocalDensity.current.density
        val screenWidthDp = LocalConfiguration.current.screenWidthDp
        return screenWidthDp * density * thresholdPercent
    }

    interface HorizontalSwipeGestureHandler {

        /**
         * Function to be called when swiping is completed to the right.
         * This is only once no touch events are done.
         */
        fun onSwipeRight()

        /**
         * Function to be called when swiping is completed to the left.
         * This is only once no touch events are done.
         */
        fun onSwipeLeft()

        /**
         * Function to be called when the right threshold is reached, during touch events.
         * This is for things that should change with the gesture:
         * e.g. change color of card when the threshold is reached, change button color when threshold is reached
         */
        fun onRightThresholdReached()

        /**
         * Function to be called when the left threshold is reached, during touch events.
         * This is for things that should change with the gesture:
         * e.g. change color of card when the threshold is reached, change button color when threshold is reached
         */
        fun onLeftThresholdReached()

        /**
         * Function to be called when the no threshold is reached, during touch events.
         * This is for things that should change with the gesture:
         * e.g. return the original color of card after threshold is not longer reached
         */
        fun onNoThresholdReached()
    }
}