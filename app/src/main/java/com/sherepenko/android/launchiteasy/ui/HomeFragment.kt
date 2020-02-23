package com.sherepenko.android.launchiteasy.ui

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.snackbar.Snackbar
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.Status
import com.sherepenko.android.launchiteasy.ui.adapters.ForecastsAdapter
import com.sherepenko.android.launchiteasy.viewmodels.WeatherViewModel
import kotlinx.android.synthetic.main.fragment_home.swipeRefreshLayout
import kotlinx.android.synthetic.main.layout_home.allAppsButton
import kotlinx.android.synthetic.main.layout_home.currentLocationView
import kotlinx.android.synthetic.main.layout_home.currentTemperatureView
import kotlinx.android.synthetic.main.layout_home.currentWeatherConditionView
import kotlinx.android.synthetic.main.layout_home.currentWeatherIconView
import kotlinx.android.synthetic.main.layout_home.nextAlarmView
import kotlinx.android.synthetic.main.layout_home.perceivedTemperatureView
import kotlinx.android.synthetic.main.layout_home.weatherForecastsView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val weatherViewModel: WeatherViewModel by viewModel()

    private lateinit var forecastsAdapter: ForecastsAdapter

    private lateinit var snackbar: Snackbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nextAlarmView.setOnClickListener {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            intent.resolveActivity(requireContext().packageManager)?.let {
                startActivity(intent)
            }
        }

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.nextAlarmClock?.let {
            nextAlarmView.text = it.format()
            nextAlarmView.visibility = View.VISIBLE
        }

        allAppsButton.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.toLauncherFragment()
            )
        }

        swipeRefreshLayout.setColorSchemeResources(
            R.color.colorSecondary,
            R.color.colorSecondaryVariant
        )
        swipeRefreshLayout.setOnRefreshListener {
            weatherViewModel.forceUpdate()
        }

        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = true
        }

        forecastsAdapter = ForecastsAdapter()

        weatherForecastsView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = forecastsAdapter
        }

        snackbar = Snackbar.make(view, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.action_dismiss) {
                hideSnackbar()
            }

        weatherViewModel.getConnectionState().observe(viewLifecycleOwner, Observer { isConnected ->
            if (isConnected) {
                hideSnackbar()
            } else {
                showSnackbar()
            }
        })

        weatherViewModel.getCurrentWeather().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    it.data?.let { data ->
                        currentLocationView.text = data.location.name
                        currentWeatherIconView.text = data.condition.icon.glyph
                        currentWeatherConditionView.text = data.condition.name
                        currentTemperatureView.text =
                            getString(
                                R.string.temperature_value,
                                data.temperature.celsius
                            )
                        perceivedTemperatureView.text =
                            getString(
                                R.string.perceived_temperature,
                                data.perceivedTemperature.celsius
                            )
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
                Status.SUCCESS -> {
                    currentLocationView.text = it.data!!.location.name
                    currentWeatherIconView.text = it.data.condition.icon.glyph
                    currentWeatherConditionView.text = it.data.condition.name
                    currentTemperatureView.text =
                        getString(
                            R.string.temperature_value,
                            it.data.temperature.celsius
                        )
                    perceivedTemperatureView.text =
                        getString(
                            R.string.perceived_temperature,
                            it.data.perceivedTemperature.celsius
                        )
                    swipeRefreshLayout.isRefreshing = false
                }
                Status.ERROR -> {
                    currentWeatherIconView.text =
                        getString(R.string.unknown_weather)
                    currentWeatherConditionView.text = ""
                    currentTemperatureView.text =
                        getString(R.string.no_temperature)
                    swipeRefreshLayout.isRefreshing = false
                    perceivedTemperatureView.text = ""
                }
            }
        })

        weatherViewModel.getWeatherForecasts().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    it.data?.let { data ->
                        forecastsAdapter.items = data

                        if (data.isNotEmpty()) {
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
                Status.SUCCESS -> {
                    forecastsAdapter.items = it.data!!
                    swipeRefreshLayout.isRefreshing = false
                }
                Status.ERROR -> {
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun showSnackbar() {
        if (!snackbar.isShownOrQueued) {
            snackbar.show()
        }
    }

    private fun hideSnackbar() {
        if (snackbar.isShownOrQueued) {
            snackbar.dismiss()
        }
    }

    private fun AlarmManager.AlarmClockInfo.format(): String =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(triggerTime), ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("E HH:mm"))
}
