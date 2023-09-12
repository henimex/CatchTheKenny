package com.hendev.catchthekenny

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.hendev.catchthekenny.databinding.ActivityMainBinding
import kotlinx.coroutines.Runnable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences

    var runnable: Runnable = Runnable {}
    var handler: Handler = Handler(Looper.getMainLooper())

    private lateinit var imageKenny: ImageView
    private lateinit var txtScore: TextView
    private lateinit var txtTime: TextView
    private lateinit var txtXloc: EditText
    private lateinit var txtYloc: EditText
    private lateinit var btnTest: Button
    private lateinit var restartButton: Button

    private var timer = 15;
    private var score = 0;
    private var savedHighScore = 0;
    private var animationSpeed: Long = 100;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        arrangeBindings();
        startGame();
    }

    fun saveHighScore(){
        if (score > savedHighScore){
            sharedPref.edit().putInt("high_score", score).apply();
            restartButton.text = "Can You Beat ${scoreToShow()}"
            println(savedHighScore)
        }
    }

    fun trigTest(view: View) {
        moveKenny()
    }

    private fun startProcess(){
        runnable = object : Runnable{
            override fun run() {
                moveKenny();
                handler.postDelayed(this,1000)
            }
        }
        handler.post(runnable);
    }

    fun btnRestart(view: View){
        startGame();
        restartButton.visibility = View.INVISIBLE
    }

    private fun moveKenny() {
        val randomX = (1..850).random().toFloat();
        val randomY = (100..1400).random().toFloat();

        imageKenny.animate().x(randomX).y(randomY).duration = animationSpeed
    }

    private fun moveKennyTest() {
        val randomX = (1..850).random();
        val randomY = (100..1400).random();

        println("Random X: $randomX RandomY: $randomY")

        val x = txtXloc.text.toString().toFloat()
        val y = txtYloc.text.toString().toFloat()
        imageKenny.animate().x(x).y(y);
        //imageKenny.animate().translationX(200f)
    }

    fun catchKenny(view: View) {
        upgradeScore();
    }

    private fun scoreToShow():Int{
        return if (savedHighScore > score){
            savedHighScore
        } else {
            score
        }
    }

    fun gameOver() {
        //saveHighScore();
        val alert = AlertDialog.Builder(this);
        alert.setTitle("Game Over!")
        alert.setMessage("Time is Up! Your score is $score . Would you like to go for another round?")
        alert.setCancelable(false);
        handler.removeCallbacks(runnable);
        imageKenny.isEnabled = false


        alert.setNegativeButton(
            "No"
        ) { p0, p1 ->
            imageKenny.isEnabled = false;
            restartButton.visibility = View.VISIBLE
            restartButton.text = "Restart game to beat ${scoreToShow()}";
        }

        alert.setPositiveButton(
            "Yes"
        ) { p0, p1 -> startGame(); }

        alert.show();
    }

    private fun startGame() {
        imageKenny.isEnabled = true;
        timer = 15;
        score = 0;
        txtScore.text = "Score : $score";
        startProcess();
        countDown();
        getHighScore();
    }

    private fun upgradeScore() {
        score++
        txtScore.text = "Score : $score"
    }

    private fun countDown() {
        val duration = (timer * 1000).toLong();
        val multiplier = (1000).toLong();

        object : CountDownTimer(duration, multiplier) {
            override fun onTick(p0: Long) {
                txtTime.text = "Time : ${p0 / multiplier}"
            }

            override fun onFinish() {
                txtTime.text = "Time Is UP !!"
                saveHighScore();
                gameOver();
            }
        }.start();
    }

    private fun arrangeBindings() {
        imageKenny = binding.imgKenny
        txtScore = binding.txtScore
        txtTime = binding.txtTime
        txtXloc = binding.txtXloc
        txtYloc = binding.txtYloc
        restartButton = binding.btnRestart
        // btnTest = binding.btnTest
    }

    fun getHighScore(){
        sharedPref = this.getSharedPreferences("henimex.getKenny.storage.data", Context.MODE_PRIVATE)
        savedHighScore = sharedPref.getInt("high_score",-1)
        println(savedHighScore)
    }
}