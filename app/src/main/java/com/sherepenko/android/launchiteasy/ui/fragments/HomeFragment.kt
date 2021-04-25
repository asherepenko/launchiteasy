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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.Resource
import com.sherepenko.android.launchiteasy.data.WeatherItem
import com.sherepenko.android.launchiteasy.data.isMetric
import com.sherepenko.android.launchiteasy.databinding.FragmentHomeBinding
import com.sherepenko.android.launchiteasy.ui.adapters.ForecastsAdapter
import com.sherepenko.android.launchiteasy.utils.PreferenceHelper
import com.sherepenko.android.launchiteasy.utils.launchActivityIfResolved
import com.sherepenko.android.launchiteasy.utils.viewBinding
import com.sherepenko.android.launchiteasy.viewmodels.WeatherViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.inject
import timber.log.Timber

@KoinApiExtension
class HomeFragment : ConnectivityAwareFragment(R.layout.fragment_home) {

    companion object {
        private const val TAG = "HomeFragment"
    }

    private val weatherViewModel: WeatherViewModel by viewModel()

    private val prefs: PreferenceHelper by inject()

    private val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)

    private lateinit var forecastsAdapter: ForecastsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupSwipeRefreshLayout()

        setupCurrentWeather()
        setupCurrentLocation()
        setupWeatherForecasts()

        binding.apply {
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
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(binding.toolbarView)
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.apply {
            setColorSchemeResources(
                R.color.colorSecondary,
                R.color.colorSecondaryVariant
            )
            setOnRefreshListener {
                weatherViewModel.forceUpdate()
            }
            post {
                binding.swipeRefreshLayout.isRefreshing = true
            }
        }
    }

    private fun setupAlarmClock() {
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (alarmManager.nextAlarmClock != null) {
            val nextAlarm = alarmManager.nextAlarmClock.toLocalDateTime()
            Timber.tag(TAG).i("Next alarm time: $nextAlarm")

            binding.nextAlarmView.apply {
                text = nextAlarm.formatAlarmDateTime()
                visibility = View.VISIBLE
            }
        } else {
            binding.nextAlarmView.visibility = View.GONE
        }
    }

    private fun setupCurrentLocation() {
        weatherViewModel.getCurrentLocationName().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    // ignore
                }
                is Resource.Success -> {
                    checkNotNull(it.data)
                    binding.currentLocationView.text = it.data
                }
                is Resource.Error -> {
                    binding.currentLocationView.text = ""
                }
            }
        }
    }

    private fun setupCurrentWeather() {
        weatherViewModel.getCurrentWeather().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    it.data?.let { data ->
                        setCurrentWeather(data)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
                is Resource.Success -> {
                    checkNotNull(it.data)
                    setCurrentWeather(it.data)
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                is Resource.Error -> {
                    if (it.data != null) {
                        setCurrentWeather(it.data)
                    } else {
                        binding.apply {
                            currentWeatherIconView.text =
                                getString(R.string.unknown_weather)
                            currentWeatherConditionView.text = ""
                            currentTemperatureView.text =
                                getString(R.string.no_temperature)
                            perceivedTemperatureView.text = ""
                        }
                    }

                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun setupWeatherForecasts() {
        forecastsAdapter = ForecastsAdapter()

        binding.weatherForecastsView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = forecastsAdapter
        }

        weatherViewModel.getWeatherForecasts().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    it.data?.let { data ->
                        setWeatherForecasts(it.data)

                        if (data.isNotEmpty()) {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
                is Resource.Success -> {
                    checkNotNull(it.data)
                    setWeatherForecasts(it.data)
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                is Resource.Error -> {
                    setWeatherForecasts(it.data ?: listOf())
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun setCurrentWeather(item: WeatherItem) {
        val isMetricSystem = prefs.getTemperatureUnit().isMetric()

        binding.apply {
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
    }

    private fun setWeatherForecasts(items: List<ForecastItem>) {
        forecastsAdapter.isMetricSystem = prefs.getTemperatureUnit().isMetric()
        forecastsAdapter.items = items
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
