package ir.game.sc_rplauncher

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        installSplashScreen().apply {
            setKeepOnScreenCondition { true }
        }

        GlobalScope.launch {
            delay(1000)
            Intent(this@SplashActivity,MainActivity::class.java).apply {
                startActivity(this)
                finishAffinity()
            }
        }
    }
}