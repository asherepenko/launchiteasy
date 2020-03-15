package com.sherepenko.android.launchiteasy.ui.fragments

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.Status
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.data.isMetric
import com.sherepenko.android.launchiteasy.ui.adapters.ForecastsAdapter
import com.sherepenko.android.launchiteasy.utils.PreferenceHelper
import com.sherepenko.android.launchiteasy.utils.launchActivityIfResolved
import com.sherepenko.android.launchiteasy.viewmodels.WeatherViewModel
import kotlinx.android.synthetic.main.fragment_home.allAppsButton
import kotlinx.android.synthetic.main.fragment_home.currentLocationView
import kotlinx.android.synthetic.main.fragment_home.currentTemperatureView
import kotlinx.android.synthetic.main.fragment_home.currentWeatherConditionView
import kotlinx.android.synthetic.main.fragment_home.currentWeatherIconView
import kotlinx.android.synthetic.main.fragment_home.nextAlarmView
import kotlinx.android.synthetic.main.fragment_home.perceivedTemperatureView
import kotlinx.android.synthetic.main.fragment_home.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_home.toolbarView
import kotlinx.android.synthetic.main.fragment_home.weatherForecastsView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.inject
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class HomeFragment : ConnectivityAwareFragment(R.layout.fragment_home) {

    private val weatherViewModel: WeatherViewModel by viewModel()

    private val prefs: PreferenceHelper by inject()

    private lateinit var forecastsAdapter: ForecastsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupSwipeRefreshLayout()

        setupCurrentWeather()
        setupCurrentLocation()
        setupWeatherForecasts()

        nextAlarmView.setOnClickListener {
            requireActivity().launchActivityIfResolved(
                Intent(AlarmClock.ACTION_SHOW_ALARMS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        allAppsButton.setOnClickListener {
            findNavController().navigateToLauncherFragment()
        }
    }

    override fun onStart() {
        super.onStart()
        setupAlarmClock()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.actionSettings -> {
                findNavController().navigateToSettingsFragment()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        if (requireActivity() is AppCompatActivity) {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarView)
        }
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout.apply {
            setColorSchemeResources(
                R.color.colorSecondary,
                R.color.colorSecondaryVariant
            )
            setOnRefreshListener {
                weatherViewModel.forceUpdate()
            }
            post {
                swipeRefreshLayout.isRefreshing = true
            }
        }
    }

    private fun setupAlarmClock() {
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (alarmManager.nextAlarmClock != null) {
            val nextAlarm = alarmManager.nextAlarmClock.toLocalDateTime()
            nextAlarmView.text = nextAlarm.formatAlarmDateTime()
            nextAlarmView.visibility = View.VISIBLE
        } else {
            nextAlarmView.visibility = View.GONE
        }
    }

    private fun setupCurrentLocation() {
        weatherViewModel.getCurrentLocationName().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    // ignore
                }
                Status.SUCCESS -> {
                    currentLocationView.text = it.data!!
                }
                Status.ERROR -> {
                    currentLocationView.text = ""
                }
            }
        })
    }

    private fun setupCurrentWeather() {
        weatherViewModel.getCurrentWeather().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    it.data?.let { data ->
                        setCurrentWeather(data)
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
                Status.SUCCESS -> {
                    checkNotNull(it.data)
                    setCurrentWeather(it.data)
                    swipeRefreshLayout.isRefreshing = false
                }
                Status.ERROR -> {
                    if (it.data != null) {
                        setCurrentWeather(it.data)
                    } else {
                        currentWeatherIconView.text =
                            getString(R.string.unknown_weather)
                        currentWeatherConditionView.text = ""
                        currentTemperatureView.text =
                            getString(R.string.no_temperature)
                        perceivedTemperatureView.text = ""
                    }

                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun setupWeatherForecasts() {
        forecastsAdapter = ForecastsAdapter()

        weatherForecastsView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = forecastsAdapter
        }

        weatherViewModel.getWeatherForecasts().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    it.data?.let { data ->
                        setWeatherForecasts(it.data)

                        if (data.isNotEmpty()) {
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
                Status.SUCCESS -> {
                    checkNotNull(it.data)
                    setWeatherForecasts(it.data)
                    swipeRefreshLayout.isRefreshing = false
                }
                Status.ERROR -> {
                    setWeatherForecasts(it.data ?: listOf())
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun setCurrentWeather(item: WeatherItem) {
        val isMetricSystem = prefs.getTemperatureUnit().isMetric()

        currentWeatherIconView.text = item.condition.icon.glyph
        currentWeatherConditionView.text = item.condition.name
        currentTemperatureView.text =
            getString(
                R.string.temperature_value,
                if (isMetricSystem) {
                    item.temperature.celsius
                } else {
                    item.temperature.fahrenheit
                }
            )
        perceivedTemperatureView.text =
            if (isMetricSystem) {
                getString(
                    R.string.perceived_temperature_metric,
                    item.perceivedTemperature.celsius
                )
            } else {
                getString(
                    R.string.perceived_temperature_imperial,
                    item.perceivedTemperature.fahrenheit
                )
            }
    }

    private fun setWeatherForecasts(items: List<ForecastItem>) {
        forecastsAdapter.isMetricSystem = prefs.getTemperatureUnit().isMetric()
        forecastsAdapter.items = items ?: listOf()
    }

    private fun NavController.navigateToLauncherFragment() {
        navigate(HomeFragmentDirections.toLauncherFragment())
    }

    private fun NavController.navigateToSettingsFragment() {
        navigate(HomeFragmentDirections.toSettingsFragment())
    }

    private fun AlarmManager.AlarmClockInfo.toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(triggerTime), ZoneId.systemDefault())

    private fun LocalDateTime.formatAlarmDateTime(): String =
        format(
            if (DateFormat.is24HourFormat(requireActivity())) {
                DateTimeFormatter.ofPattern("E HH:mm")
            } else {
                DateTimeFormatter.ofPattern("E hh:mm a")
            }
        )
}
