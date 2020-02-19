package com.sherepenko.android.launchiteasy

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.work.WorkManager
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jakewharton.threetenabp.AndroidThreeTen
import com.sherepenko.android.launchiteasy.api.OpenWeatherApi
import com.sherepenko.android.launchiteasy.data.db.AppDatabase
import com.sherepenko.android.launchiteasy.livedata.AppStateLiveData
import com.sherepenko.android.launchiteasy.livedata.ConnectivityLiveData
import com.sherepenko.android.launchiteasy.livedata.LocationLiveData
import com.sherepenko.android.launchiteasy.providers.AppsLocalDataSource
import com.sherepenko.android.launchiteasy.providers.AppsRemoteDataSource
import com.sherepenko.android.launchiteasy.providers.WeatherLocalDataSource
import com.sherepenko.android.launchiteasy.providers.WeatherRemoteDataSource
import com.sherepenko.android.launchiteasy.repositories.AppsRepository
import com.sherepenko.android.launchiteasy.repositories.AppsRepositoryImpl
import com.sherepenko.android.launchiteasy.repositories.WeatherRepository
import com.sherepenko.android.launchiteasy.repositories.WeatherRepositoryImpl
import com.sherepenko.android.launchiteasy.viewmodels.AppsViewModel
import com.sherepenko.android.launchiteasy.viewmodels.WeatherViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
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
            PreferenceManager.getDefaultSharedPreferences(get())
        }

        single {
            WorkManager.getInstance(get())
        }
    }

    private val apiModule = module {
        val tag = "OkHttp"

        single {
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor(
                        object : HttpLoggingInterceptor.Logger {
                            override fun log(message: String) {
                                Timber.tag(tag).i(message)
                            }
                        }
                    ).apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
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
            ).build()
        }
    }

    private val repositoryModule = module {
        single {
            ConnectivityLiveData(get())
        }

        single {
            WeatherRepositoryImpl(
                WeatherLocalDataSource(get()),
                WeatherRemoteDataSource(get()),
                LocationLiveData(get()),
                get()
            ) as WeatherRepository
        }

        single {
            AppsRepositoryImpl(
                AppsLocalDataSource(get()),
                AppsRemoteDataSource(get()),
                AppStateLiveData(get())
            ) as AppsRepository
        }
    }

    private val viewModelModule = module {
        viewModel {
            AppsViewModel(get())
        }

        viewModel {
            WeatherViewModel(get())
        }
    }

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this@LauncherApp)
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
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

@GlideModule
class LauncherAppGlideModule : AppGlideModule()
