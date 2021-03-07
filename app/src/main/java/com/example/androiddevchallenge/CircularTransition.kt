package com.example.androiddevchallenge

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue

class CircularTransition(
    progressState: State<Float>
) {
    val progress by progressState
}

@Composable
fun circularTransition(
    remainingTimerTime: RemainingTimerTime?
): CircularTransition {
    val transition = updateTransition(targetState = remainingTimerTime?.progress ?: 0f)

    val circleProgress = transition.animateFloat(
        transitionSpec = { tween(800) }
    ) {
        360f * it
    }

    return CircularTransition(circleProgress)
}