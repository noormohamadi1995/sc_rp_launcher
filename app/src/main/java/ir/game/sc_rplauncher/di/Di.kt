package ir.game.sc_rplauncher.di

import android.content.Context
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.fetch.FetchImpl
import com.tonyodev.fetch2.fetch.FetchModulesBuilder
import com.tonyodev.fetch2core.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object Di {
    @Provides
    @Singleton
    internal fun provideFetchConfiguration(@ApplicationContext context: Context): FetchConfiguration =
        FetchConfiguration.Builder(context)
            .setDownloadConcurrentLimit(10)
            .setProgressReportingInterval(200)
            .setLogger(object : Logger {
                override var enabled: Boolean = true

                override fun d(message: String) {
                    Timber.tag("downloader").d(message)
                }

                override fun d(message: String, throwable: Throwable) {
                    Timber.tag("downloader").d(throwable, message)
                }

                override fun e(message: String) {
                    Timber.tag("downloader").e(message)
                }

                override fun e(message: String, throwable: Throwable) {
                    Timber.tag("downloader").e(throwable, message)
                }
            })
            .enableLogging(true)
            .enableAutoStart(false)
            .enableFileExistChecks(true)
            .build()

    @Provides
    @Singleton
    internal fun provideFetch(fetchConfiguration: FetchConfiguration) : Fetch =
        FetchImpl.newInstance(FetchModulesBuilder.buildModulesFromPrefs(fetchConfiguration))
}