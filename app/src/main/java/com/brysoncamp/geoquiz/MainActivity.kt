package com.brysoncamp.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.brysoncamp.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        binding.trueButton.setOnClickListener {
            handleTrueFalse(true)
        }

        binding.falseButton.setOnClickListener {
            handleTrueFalse(false)
        }

        binding.nextButton.setOnClickListener {
            quizViewModel.nextQuestion()
            updateQuestion()
        }

        binding.questionTextView.setOnClickListener {
            quizViewModel.nextQuestion()
            updateQuestion()
        }

        binding.prevButton.setOnClickListener {
            quizViewModel.prevQuestion()
            updateQuestion()
        }

        binding.cheatButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        updateQuestion()
    }

    private fun handleTrueFalse(trueFalse : Boolean) {
        var (messageResId, score) = quizViewModel.checkAnswer(trueFalse)
        if (quizViewModel.isCheater && messageResId != 0) {
            messageResId = R.string.judgement_toast
        }
        handleToasts(messageResId, score)
    }

    private fun handleToasts(messageResId: Int, score: Double) {
        if (messageResId != 0) {
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
            if (score != 0.0) {
                Toast.makeText(this, "Score: $score%", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}