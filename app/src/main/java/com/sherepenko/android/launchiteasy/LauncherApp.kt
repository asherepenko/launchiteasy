package com.sherepenko.android.launchiteasy

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.sherepenko.android.launchiteasy.api.OpenWeatherApi
import com.sherepenko.android.launchiteasy.data.db.AppDatabase
import com.sherepenko.android.launchiteasy.livedata.AppStateLiveData
import com.sherepenko.android.launchiteasy.livedata.ConnectivityLiveData
import com.sherepenko.android.launchiteasy.livedata.LocationLiveData
import com.sherepenko.android.launchiteasy.providers.AppsLocalDataSource
import com.sherepenko.android.launchiteasy.providers.AppsRemoteDataSource
import com.sherepenko.android.launchiteasy.providers.WeatherLocalDataSource
import com.sherepenko.android.launchiteasy.providers.WeatherRemoteDataSource
import com.sherepenko.android.launchiteasy.repositories.AppStateRepository
import com.sherepenko.android.launchiteasy.repositories.AppStateRepositoryImpl
import com.sherepenko.android.launchiteasy.repositories.AppsRepository
import com.sherepenko.android.launchiteasy.repositories.AppsRepositoryImpl
import com.sherepenko.android.launchiteasy.repositories.ConnectivityRepository
import com.sherepenko.android.launchiteasy.repositories.ConnectivityRepositoryImpl
import com.sherepenko.android.launchiteasy.repositories.LocationRepository
import com.sherepenko.android.launchiteasy.repositories.LocationRepositoryImpl
import com.sherepenko.android.launchiteasy.repositories.WeatherRepository
import com.sherepenko.android.launchiteasy.repositories.WeatherRepositoryImpl
import com.sherepenko.android.launchiteasy.utils.PreferenceHelper
import com.sherepenko.android.launchiteasy.viewmodels.AppsViewModel
import com.sherepenko.android.launchiteasy.viewmodels.ConnectivityViewModel
import com.sherepenko.android.launchiteasy.viewmodels.WeatherViewModel
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import timber.log.Timber

class LauncherApp : Application() {

    companion object {
        private const val DB_NAME = "launch_it_easy_db"
    }

    private val appModule = module {
        single {
            PreferenceHelper(
                get(),
                PreferenceManager.getDefaultSharedPreferences(get())
            )
        }
    }

    private val apiModule = module {
        val tag = "OkHttp"

        single {
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor {
                        Timber.tag(tag).i(it)
                    }.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
                .addNetworkInterceptor(StethoInterceptor())
                .build()
        }

        single {
            Retrofit.Builder()
                .baseUrl(OpenWeatherApi.BASE_URL)
                .client(get())
                .addConverterFactory(
                    JacksonConverterFactory.create(jacksonObjectMapper())
                )
                .build()
                .create(OpenWeatherApi::class.java)
        }
    }

    private val databaseModule = module(createdAtStart = true) {
        single {
            Room.databaseBuilder(
                get(),
                AppDatabase::class.java,
                DB_NAME
            )
                .build()
        }
    }

    private val repositoryModule = module {
        single<ConnectivityRepository> {
            ConnectivityRepositoryImpl(
                ConnectivityLiveData(get())
            )
        }

        single<LocationRepository> {
            LocationRepositoryImpl(
                LocationLiveData(get())
            )
        }

        single<WeatherRepository> {
            WeatherRepositoryImpl(
                get(),
                get(),
                WeatherLocalDataSource(get()),
                WeatherRemoteDataSource(get())
            )
        }

        single<AppStateRepository> {
            AppStateRepositoryImpl(
                AppStateLiveData(get())
            )
        }

        single<AppsRepository> {
            AppsRepositoryImpl(
                get(),
                AppsLocalDataSource(get()),
                AppsRemoteDataSource(get())
            )
        }
    }

    private val viewModelModule = module {
        viewModel {
            ConnectivityViewModel(get())
        }

        viewModel {
            AppsViewModel(get())
        }

        viewModel {
            WeatherViewModel(get(), get(), get())
        }
    }

    override fun onCreate() {
        super.onCreate()

        setupTimber()
        setupStetho()
        setupCalligraphy()
        setupKoin()
    }

    private fun setupTimber() {
        Timber.plant(CrashlyticsTree())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setupStetho() {
        Stetho.initializeWithDefaults(this@LauncherApp)
    }

    private fun setupCalligraphy() {
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .build()
                    )
                )
                .build()
        )
    }

    private fun setupKoin() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@LauncherApp)
            modules(
                appModule,
                apiModule,
                databaseModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}

internal class CrashlyticsTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean =
        priority == Log.WARN || priority == Log.ERROR || priority == Log.ASSERT

    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        Firebase.crashlytics.apply {
            log(message)

            throwable?.let {
                recordException(it)
                sendUnsentReports()
            }
        }
    }
}
