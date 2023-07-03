package ir.game.sc_rplauncher

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import ir.game.sc_rplauncher.util.LocalContextWrapper
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.Locale

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        plant(Timber.DebugTree())
    }
    override fun attachBaseContext(base: Context?) {
        var context = base ?: this
        val config = context.resources.configuration
        val language = "fa"
        val sysLocale: Locale = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            LocalContextWrapper.getSystemLocale(config)
        } else {
            LocalContextWrapper.getSystemLocaleLegacy(config)
        }
        if (sysLocale.language != language) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            LocalContextWrapper.setSystemLocale(config, locale)
        }
        context = context.createConfigurationContext(config)

        super.attachBaseContext(base)
    }
}