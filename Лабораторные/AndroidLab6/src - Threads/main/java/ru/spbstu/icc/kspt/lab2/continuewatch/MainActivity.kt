package ru.spbstu.icc.kspt.lab2.continuewatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.locks.*;

class MainActivity : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private var background: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        background = Thread {
            try {
                var timePast: Long
                var delay = 1000L
                var delayPast = 1000L
                var time = System.currentTimeMillis() - 1000
                while (true) {
                    timePast = time
                    time = System.currentTimeMillis()
                    delay -= time - timePast - delayPast
                    Log.d("Timer", delay.toString())
                    Thread.sleep(delay)
                    textSecondsElapsed.post {
                        textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
                    }
                    if (background?.isInterrupted == true) break;
                    delayPast = delay
                    delay = 1000L
                }
            } catch (e: InterruptedException) {
            }
        }
        background?.start()
        super.onResume()
    }

    override fun onPause() {
        background?.interrupt()
        background = null
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("secs", secondsElapsed)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        secondsElapsed = savedInstanceState.getInt("secs")
        textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)

        super.onRestoreInstanceState(savedInstanceState)
    }
}
