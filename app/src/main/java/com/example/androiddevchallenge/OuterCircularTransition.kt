/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue

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
