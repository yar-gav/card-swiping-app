import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.cardswipingapp.ui.gestures.HorizontalSwipeGesture
import com.example.cardswipingapp.ui.gestures.HorizontalSwipeGesture.getThresholdReachedTargetValue
import com.example.cardswipingapp.ui.gestures.HorizontalSwipeGesture.horizontalSwipeable
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun <T> StackedSwipeCardView(
    modifier: Modifier = Modifier,
    cardDataList: List<T>,
    visibleCards: Int,
    horizontalSwipeGestureHandler: HorizontalSwipeGesture.HorizontalSwipeGestureHandler,
    externalSwipeDirection: SwipeDirection = SwipeDirection.None,
    rotationInverseRatio: Float = 60f,
    cardContent: @Composable (T) -> Unit
) {
    val density = LocalDensity.current.density
    BoxWithConstraints(modifier = modifier) {
        cardDataList.forEachIndexed { index, cardData ->
            key(cardData) {
                val animatedScale by animateFloatAsState(
                    targetValue = getScale(index),
                    label = "ScaleAnimation",
                    animationSpec = tween(200, easing = EaseInOutCubic)
                )
                // Calculate offset of the current index, or visible cards -1, to show only the visible cards.
                val offsetY = getYOffset(maxHeight.value, min(index, visibleCards - 1)).dp
                val animatedYOffset by animateDpAsState(
                    targetValue = offsetY,
                    label = "YOffsetAnimation",
                    animationSpec = tween(200, easing = EaseInOutCubic)
                )

                val offsetXFromSwipeAnimation = remember { Animatable(0f) }
                if (index == 0) {
                    HandleExternalSwipe(
                        externalSwipeDirection,
                        offsetXFromSwipeAnimation,
                        horizontalSwipeGestureHandler
                    )
                }

                Box(
                    Modifier
                        .zIndex((cardDataList.size - index).toFloat())
                        // Update animation for offset and rotationZ from the external swipe
                        .offset { IntOffset(offsetXFromSwipeAnimation.value.roundToInt(), 0) }
                        .graphicsLayer(
                            rotationZ = offsetXFromSwipeAnimation.value / rotationInverseRatio
                        )
                        .horizontalSwipeable(
                            horizontalSwipeGestureHandler = horizontalSwipeGestureHandler
                        )
                        .graphicsLayer(
                            translationY = animatedYOffset.value * density,
                            scaleX = animatedScale,
                            scaleY = animatedScale
                        )
                ) {
                    cardContent(cardData)
                }
            }
        }
    }
}

@Composable
private fun HandleExternalSwipe(
    externalSwipeDirection: SwipeDirection,
    offsetX: Animatable<Float, AnimationVector1D>,
    horizontalSwipeGestureHandler: HorizontalSwipeGesture.HorizontalSwipeGestureHandler
) {
    val thresholdReachedTargetValue = getThresholdReachedTargetValue()
    val externalSwipeAnimationSpec: AnimationSpec<Float> = tween(400, easing = EaseInOutCubic)

    LaunchedEffect(externalSwipeDirection) {
        when(externalSwipeDirection) {
            SwipeDirection.Right -> {
                offsetX.animateTo(
                    targetValue = thresholdReachedTargetValue,
                    animationSpec = externalSwipeAnimationSpec
                )
                horizontalSwipeGestureHandler.onSwipeRight()
            }

            SwipeDirection.Left -> {
                offsetX.animateTo(
                    targetValue = -thresholdReachedTargetValue,
                    animationSpec = externalSwipeAnimationSpec
                )
                horizontalSwipeGestureHandler.onSwipeLeft()
            }
            SwipeDirection.None -> { }
        }
    }
}

enum class SwipeDirection {
    Left, Right, None
}

private fun yOffsetFromPreviousCard(index: Int): Int {
    return when (index) {
        0 -> 0
        1 -> -16
        2 -> -28
        3 -> -36
        else -> -36
    }
}

/**
 * Offset is calculated based on both hard-coded [yOffsetFromPreviousCard]
 * along with the scaled size of the current card, to get the offset based on the center of the view.
 */
private fun getYOffset(cardHeight: Float, index: Int): Float {
    return ((yOffsetFromPreviousCard(index) - (cardHeight.times(1 - (0.9.pow(index)))) / 2).toFloat())
}

/**
 * Scale is called by index where each consecutive card is scaled 90% compared to previous card.
 */
private fun getScale(index: Int) = 0.9.pow(index).toFloat()

@Composable
@Preview
fun StackedSwipeCardPreview() {
    var uiState by remember {
        mutableStateOf(listOf(1, 2, 3, 4, 5, 6, 7, 8))
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center

    ) {
        // Overlay content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StackedSwipeCardView(
                cardDataList = uiState,
                visibleCards = 4,
                horizontalSwipeGestureHandler = object : HorizontalSwipeGesture.HorizontalSwipeGestureHandler {
                    override fun onSwipeRight() {
                        uiState = uiState.drop(1)
                    }

                    override fun onSwipeLeft() {
                        uiState = uiState.drop(1)
                    }

                    override fun onRightThresholdReached() { }

                    override fun onLeftThresholdReached() { }

                    override fun onNoThresholdReached() { }
                }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize(),
//                    shadowColor = Color.Black,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 2.dp,
                    backgroundColor = MaterialTheme.colors.onPrimary
                ) {
                    Column(Modifier.fillMaxSize()) {
                        Text(
                            "Some card view text: # $it",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}