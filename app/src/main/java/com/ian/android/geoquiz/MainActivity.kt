package com.ian.android.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import java.lang.Math.round

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val VIEW_MODEL = "quizViewModel"
private const val REQUEST_CODE_CHEAT = 0


class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button //lateinit延遲初始化
    private lateinit var falseButton: Button

//    private lateinit var nextButton: Button
//    private lateinit var prevButton: Button

    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(QuizViewModel::class.java)
//        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    //    private val questionBank = listOf(
//        Question(R.string.question_australia, true),
//        Question(R.string.question_ocean, true),
//        Question(R.string.question_mideast, false),
//        Question(R.string.question_africa, false),
//        Question(R.string.question_americas, true),
//        Question(R.string.question_asia, true),
//    )
//    private var hasAnswered = BooleanArray(size = quizViewModel.questionSize)
//    private var answeredList = BooleanArray(size = quizViewModel.questionSize)

    //    private var currentIndex = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false // ?.安全呼叫(不是null才呼叫)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex
//        val provider: ViewModelProvider = ViewModelProviders.of(this)
//        val quizViewModel = provider.get(QuizViewModel::class.java) //deprecated
//        val quizViewModel = ViewModelProvider.NewInstanceFactory().create(QuizViewModel::class.java)
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)

        checkBtnValid()

        trueButton.setOnClickListener { view: View ->
//            Toast.makeText(
//                this,
//                R.string.correct_toast,
//                Toast.LENGTH_SHORT
//            )
//                .show()
            checkAnswer(true)
        }
        falseButton.setOnClickListener { view: View ->
//            Toast.makeText(
//                this,
//                R.string.incorrect_toast,
//                Toast.LENGTH_SHORT
//            )
//                .show()
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
//            currentIndex = (currentIndex + 1) % questionBank.size
            quizViewModel.moveToNext()
            updateQuestion()
            checkBtnValid()
        }
        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
            checkBtnValid()
        }

        cheatButton.setOnClickListener { view ->
            // start CheatActivity
//            val intent = Intent(this, CheatActivity::class.java)
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){//因為此功能需求的最小API是23,但是本專案是21 所以要先檢查
                val options =
                    ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height,)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            }else{
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
            
        }

        updateQuestion()
    }

    private fun updateQuestion() {
//        val questionTextResId = questionBank[currentIndex].textResId
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        var result = quizViewModel.checkQuestionAnswered(userAnswer)
//        var messageResId = if (result) {
//            R.string.correct_toast
//        } else {
//            R.string.incorrect_toast
//        }
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        checkBtnValid()
        checkPoint()
    }

    private fun checkBtnValid() {
        if (quizViewModel.currentHasAnswered) {
            trueButton.isClickable = false
            falseButton.isClickable = false
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        } else {
            trueButton.isClickable = true
            falseButton.isClickable = true
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }
    }

    private fun checkPoint() {
        // move to quizViewModel
//        var check = true
//        for (item in hasAnswered) {
//            if (item == false) {
//                check = false
//                break
//            }
//        }
        if (quizViewModel.checkAllAnswered()) {
//            val point: Double = answeredList.count { it == true }.toDouble() / answeredList.size
            println("point is " + quizViewModel.answerPoint)
            Toast.makeText(this, "You got point ${quizViewModel.answerPoint}", Toast.LENGTH_SHORT)
                .show()
        }
    }
}