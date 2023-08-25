package com.brysoncamp.geoquiz

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.sql.RowId

private const val TAG = "QuizViewModel"
const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val ANSWERED_COUNT_KEY = "ANSWERED_COUNT_KEY"
const val CORRECT_COUNT_KEY = "CORRECT_COUNT_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, answer = true, answered = false),
        Question(R.string.question_oceans, answer = true, answered = false),
        Question(R.string.question_mideast, answer = false, answered = false),
        Question(R.string.question_africa, answer = false, answered = false),
        Question(R.string.question_americas, answer = true, answered = false),
        Question(R.string.question_asia, answer = true, answered = false)
    )

    var isCheater: Boolean
        get() = savedStateHandle[IS_CHEATER_KEY] ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    private var currentIndex
        get() = savedStateHandle[CURRENT_INDEX_KEY] ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    private var answeredCount
        get() = savedStateHandle[ANSWERED_COUNT_KEY] ?: 0
        set(value) = savedStateHandle.set(ANSWERED_COUNT_KEY, value)

    private var correctCount
        get() = savedStateHandle[CORRECT_COUNT_KEY] ?: 0
        set(value) = savedStateHandle.set(CORRECT_COUNT_KEY, value)

    data class Message(val messageResId: Int, val finished: Double)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun nextQuestion() {
        isCheater = false
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun prevQuestion() {
        isCheater = false
        currentIndex = (currentIndex + questionBank.size - 1) % questionBank.size
    }

    fun checkAnswer(userAnswer: Boolean): Message {
        if (!questionBank[currentIndex].answered) {
            questionBank[currentIndex].answered = true
            answeredCount++

            val correctAnswer = questionBank[currentIndex].answer

            var messageResId: Int = if (userAnswer == correctAnswer) {
                if (!isCheater) {
                    correctCount++
                }
                R.string.correct_toast
            } else {
                R.string.incorrect_toast
            }

            if (answeredCount == questionBank.size) {
                val score = kotlin.math.round((correctCount.toDouble() / answeredCount) * 10000)/100
                resetVariables()
                return Message(messageResId, score)
            }
            return Message(messageResId, 0.0)
        }
        return Message(0, 0.0)
    }

    private fun resetVariables() {
        answeredCount = 0
        correctCount = 0
        for (question in questionBank) {
            question.answered = false
        }
    }
}