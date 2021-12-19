package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreViewModel(finalScore: Int): ViewModel() {

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _playEvent = MutableLiveData<Boolean>()
    val playEvent: LiveData<Boolean> = _playEvent

    init {
        _score.value = finalScore
        Log.d("ScoreViewModel", "ScoreViewModel got $finalScore")
    }

    fun playAgainClicked(){
        _playEvent.value = true
    }

    fun playAgainCompleted(){
        _playEvent.value = false
    }
}
