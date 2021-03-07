package com.example.androiddevchallenge

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

class OuterCircularTransition(
    progressState: State<Float>
) {
    val progress by progressState
}

@Composable
fun outerCircleTransitionData(
    remainingTimerTime: RemainingTimerTime?
): OuterCircularTransition {
    val transition = updateTransition(targetState = remainingTimerTime?.progress ?: 0f)

    val circleProgress = transition.animateFloat(
        transitionSpec = { tween(800) }
    ) {
        360f * it
    }

    return OuterCircularTransition(circleProgress)
}
