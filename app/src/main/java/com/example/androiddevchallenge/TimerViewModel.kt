package com.example.androiddevchallenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {

    var timerValue = ""

    private var _timerLiveData = MutableLiveData(RemainingTimerTime(0, 0, 0))
    val timerLiveData: LiveData<RemainingTimerTime> = _timerLiveData

    private var _timerInputError = MutableLiveData(false)
    val timerInputError: LiveData<Boolean> = _timerInputError

    fun onClickStart() {
        val splitTime = timerValue.split(":")
        if (splitTime.size < 3) {
            _timerInputError.value = true
            return
        }

        _timerInputError.value = false


    }
}

data class RemainingTimerTime(
    val hours: Int,
    val minutes: Int,
    val seconds: Int
)