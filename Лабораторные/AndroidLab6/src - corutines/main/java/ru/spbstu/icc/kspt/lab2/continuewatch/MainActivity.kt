package ru.spbstu.icc.kspt.lab2.continuewatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    var secondsElapsed: Int = 0
    private lateinit var corutine: Job
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        corutine = scope.launch {
            while (true) {
                
                delay(1000)
                textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
            }
        }
        super.onResume()
    }

    override fun onPause() {
        corutine.cancel()
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
