package com.example.androiddevchallenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TimerViewModel : ViewModel() {

    var timerUserInput = ""

    private var _remainingTimeLiveData: MutableLiveData<RemainingTimerTime?> = MutableLiveData(null)
    val remainingTimeLiveData: LiveData<RemainingTimerTime?> = _remainingTimeLiveData

    private var _timerInputError = MutableLiveData(false)
    val timerInputError: LiveData<Boolean> = _timerInputError

    var timerJob: Job? = null

    fun onClickStart() {
        val splitTime = timerUserInput.split(":")
        if (splitTime.size < 3) {
            _timerInputError.value = true
            return
        }

        _timerInputError.value = false

        val initialTimerTime = RemainingTimerTime(
            hours = splitTime[0].toLong(),
            minutes = splitTime[1].toLong(),
            seconds = splitTime[2].toLong(),
            progress = 0f
        )

        _remainingTimeLiveData.value = initialTimerTime

        startTimer(initialTimerTime.timeInSeconds)
    }

    private fun startTimer(totalSeconds: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)

                _remainingTimeLiveData.value?.let { currentRemainingTime ->
                    val newTime = currentRemainingTime.timeInSeconds - 1
                    _remainingTimeLiveData.value = RemainingTimerTime(
                        hours = TimeUnit.SECONDS.toHours(newTime),
                        minutes = TimeUnit.SECONDS.toMinutes(newTime),
                        seconds = newTime % 60,
                        progress = 1f - newTime.toFloat() / totalSeconds
                    )

                    if (newTime == 0L) cancel()
                }
            }
        }
    }
}

data class RemainingTimerTime(
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val progress: Float
) {

    val timeInSeconds =
        seconds + TimeUnit.MINUTES.toSeconds(minutes) + TimeUnit.HOURS.toSeconds(hours)
}