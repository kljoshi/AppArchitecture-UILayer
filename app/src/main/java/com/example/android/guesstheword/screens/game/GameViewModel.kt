package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L

        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L

        // This is the total time of the game
        const val COUNTDOWN_TIME = 5000L
    }

    // The current word
    private val _word = MutableLiveData<String>()
    val word: LiveData<String> = _word

    // The current score
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _gameFinished = MutableLiveData<Boolean>()
    val gameFinished: LiveData<Boolean> = _gameFinished

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    private lateinit var timer: CountDownTimer

    private val _gameTimer = MutableLiveData<String>()
    val gameTimer: LiveData<String> = _gameTimer

    init {
        Log.i("GameViewModel", "GameViewModel is created!")
        resetList()
        nextWord()
        _score.value = 0

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _gameTimer.value = DateUtils.formatElapsedTime(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                _gameFinished.value = true
            }
        }.start()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat"
//            ,
//            "change",
//            "snail",
//            "soup",
//            "calendar",
//            "sad",
//            "desk",
//            "guitar",
//            "home",
//            "railway",
//            "zebra",
//            "jelly",
//            "car",
//            "crow",
//            "trade",
//            "bag",
//            "roll",
//            "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        _score.value = score.value?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = score.value?.plus(1)
        nextWord()
    }

    fun gameFinishedCompleted() {
        _gameFinished.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel is destroyed!")
    }
}
