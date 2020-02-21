package com.sherepenko.android.launchiteasy.ui

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
import kotlinx.android.synthetic.main.fragment_home.currentLocationView
import kotlinx.android.synthetic.main.fragment_home.currentTemperatureView
import kotlinx.android.synthetic.main.fragment_home.currentTimeView
import kotlinx.android.synthetic.main.fragment_home.currentWeatherConditionView
import kotlinx.android.synthetic.main.fragment_home.currentWeatherIconView
import kotlinx.android.synthetic.main.fragment_home.launcherButton
import kotlinx.android.synthetic.main.fragment_home.weatherForecastsView
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val weatherViewModel: WeatherViewModel by viewModel()

    private lateinit var snackbar: Snackbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentTimeView.setOnClickListener {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            }
        }

        launcherButton.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.toLauncherFragment()
            )
        }

        val forecastsAdapter = ForecastsAdapter()

        weatherForecastsView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = forecastsAdapter
        }

        snackbar = Snackbar.make(view, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)

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
                        currentWeatherIconView.text = data.condition.icon.glyph
                        currentWeatherConditionView.text = data.condition.name
                        currentTemperatureView.text =
                            getString(R.string.temperature_value, data.temperature.celsius)
                        currentLocationView.text = data.location.name
                    }
                }
                Status.SUCCESS -> {
                    currentWeatherIconView.text = it.data!!.condition.icon.glyph
                    currentWeatherConditionView.text = it.data.condition.name
                    currentTemperatureView.text =
                        getString(R.string.temperature_value, it.data.temperature.celsius)
                    currentLocationView.text = it.data.location.name
                }
                Status.ERROR -> {
                    currentTemperatureView.text =
                        getString(R.string.no_temperature)
                }
            }
        })

        weatherViewModel.getWeatherForecasts().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    it.data?.let { data ->
                        forecastsAdapter.items = data
                    }
                }
                Status.SUCCESS -> {
                    forecastsAdapter.items = it.data!!
                }
                Status.ERROR -> {
                    // ignore
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
}
