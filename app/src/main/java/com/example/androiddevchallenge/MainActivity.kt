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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {

    private val viewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(viewModel)
            }
        }
    }
}

@Composable
fun MyApp(viewModel: TimerViewModel) {
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Title()

            TimerTextField(viewModel)

            StartButton(viewModel)

            TimerView(viewModel)
        }
    }
}

@Composable
private fun StartButton(
    viewModel: TimerViewModel
) {
    val focusManager = LocalFocusManager.current
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()

    OutlinedButton(
        onClick = {
            focusManager.clearFocus()
            viewModel.onClickStart()
        },
        shape = RoundedCornerShape(30),
        colors = if (isTimerRunning) {
            ButtonDefaults.outlinedButtonColors(
                backgroundColor = Color.Red
            )
        } else {
            ButtonDefaults.outlinedButtonColors()
        }
    ) {
        Text(text = if (isTimerRunning) "Stop" else "Start")
    }
}

@Composable
private fun TimerView(viewModel: TimerViewModel) {
    val timerState = viewModel.remainingTimeLiveData.observeAsState()
    val outerCircleTransition = outerCircleTransitionData(timerState.value)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxHeight()
    ) {

        OuterCircle(outerCircleTransition)

        InnerCircle(timerState)

        Text(
            text = timerState.value.run {
                if (this == null) {
                    "00:00:00"
                } else {
                    val hours = String.format("%02d", hours)
                    val minutes = String.format("%02d", minutes)
                    val seconds = String.format("%02d", seconds)
                    "$hours:$minutes:$seconds"
                }
            },
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
private fun OuterCircle(outerCircleTransition: OuterCircularTransition) {
    val circleRadius = 320f
    Canvas(modifier = Modifier.height(circleRadius.dp)) {
        inset(size.width / 2 - circleRadius, size.height / 2 - circleRadius) {
            drawCircle(
                color = Color.Gray,
                radius = circleRadius,
                style = Stroke(width = 50f)
            )

            drawArc(
                startAngle = 270f,
                sweepAngle = outerCircleTransition.progress,
                color = Color.Blue,
                style = Stroke(width = 50f, cap = StrokeCap.Round),
                useCenter = false
            )
        }
    }
}

@Composable
private fun InnerCircle(timerState: State<RemainingTimerTime?>) {
    val innerCircleRadius = 280f
    val remainingSeconds = timerState.value?.timeInSeconds ?: 0
    val arcAngle by animateFloatAsState(
        targetValue = if (remainingSeconds % 2 == 0L) {
            0f
        } else {
            360f
        },
        animationSpec = tween(1000)
    )
    val arcColor by animateColorAsState(
        targetValue = if (remainingSeconds % 2 == 0L) {
            Color.Transparent
        } else {
            Color.Cyan
        },
        animationSpec = tween(1000)
    )
    Canvas(modifier = Modifier.height(innerCircleRadius.dp)) {
        inset(size.width / 2 - innerCircleRadius, size.height / 2 - innerCircleRadius) {
            drawArc(
                startAngle = 270f,
                sweepAngle = arcAngle,
                color = arcColor,
                style = Stroke(width = 40f, cap = StrokeCap.Round),
                useCenter = false
            )
        }
    }
}

@Composable
private fun Title() {
    Text(text = "Timer", style = MaterialTheme.typography.h3)

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun TimerTextField(viewModel: TimerViewModel) {
    val textFieldValue = remember { mutableStateOf(TextFieldValue()) }
    OutlinedTextField(
        value = textFieldValue.value,
        isError = viewModel.timerInputError.observeAsState(false).value,
        label = { Text("hh:mm:ss") },
        singleLine = true,
        onValueChange = {
            if (it.text.length > 8) return@OutlinedTextField

            val modifiedInput = viewModel.timerChanged(it.text)
            textFieldValue.value = TextFieldValue(
                text = modifiedInput,
                selection = TextRange(modifiedInput.length)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp(TimerViewModel())
    }
}
