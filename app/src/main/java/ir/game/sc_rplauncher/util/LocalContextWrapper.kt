package ir.game.sc_rplauncher.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

class LocalContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        fun wrap(context: Context, language: String): ContextWrapper {
            var context = context
            val config = context.resources.configuration
            val sysLocale: Locale = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                getSystemLocale(config)
            } else {
                getSystemLocaleLegacy(config)
            }
            if (language != "" && sysLocale.language != language) {
                val locale = Locale(language)
                Locale.setDefault(locale)
                setSystemLocale(config, locale)
            }
            context = context.createConfigurationContext(config)
            return LocalContextWrapper(context)
        }

        fun getSystemLocaleLegacy(config: Configuration): Locale {
            return config.locale
        }

        fun getSystemLocale(config: Configuration): Locale {
            return config.locales[0]
        }

        private fun setSystemLocaleLegacy(config: Configuration, locale: Locale?) {
            config.locale = locale
        }

        fun setSystemLocale(config: Configuration, locale: Locale?) {
            config.setLocale(locale)
        }
    }
}
