package com.ian.android.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel
import java.lang.Math.round

private const val TAG = "QuizViewModel"


class QuizViewModel : ViewModel() {
    //    init {
//        Log.d(TAG, "Viewmodel instance created")
//    }

    override fun onCleared() { // 在ViewModel被銷毀前調用
        super.onCleared()
        Log.d(TAG, "ViewModel instance about to be destroyed")
    }

    var currentIndex = 0 // 讓MainActivity也可以取得
    var isCheater = false

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_ocean, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
    )

    private var hasAnswered = BooleanArray(size = questionSize)
    private var answeredList = BooleanArray(size = questionSize)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionResult: Boolean
        get() = answeredList[currentIndex]

    val currentHasAnswered: Boolean
        get() = hasAnswered[currentIndex]


    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    val questionSize: Int
        get() = questionBank.size

    val answerPoint: Long
        get() = round((answeredList.count { it == true }.toDouble() / answeredList.size) * 100)

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = (currentIndex - 1) % questionBank.size
    }


    fun checkQuestionAnswered(userAnswer: Boolean): Boolean {
        hasAnswered[currentIndex] = true
        if (userAnswer == currentQuestionAnswer) {
            answeredList[currentIndex] = true
            return true
        } else {
            answeredList[currentIndex] = false
            return false
        }
    }

    fun checkAllAnswered(): Boolean {
        var result = true
        for (item in hasAnswered) {
            if (item == false) {
                result = false
                break
            }
        }
        return result
    }


}