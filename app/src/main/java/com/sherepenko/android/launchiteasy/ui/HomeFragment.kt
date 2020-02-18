package com.sherepenko.android.launchiteasy.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.utils.Status
import com.sherepenko.android.launchiteasy.viewmodels.WeatherViewModel
import kotlinx.android.synthetic.main.fragment_home.currentTemperatureView
import kotlinx.android.synthetic.main.fragment_home.launcherButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val weatherViewModel: WeatherViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        launcherButton.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.toLauncherFragment()
            )
        }

        weatherViewModel.getCurrentWeather().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    it.data?.let { data ->
                        currentTemperatureView.text =
                            getString(R.string.temperature_value, data.temperature.celsius)
                    }
                }
                Status.SUCCESS -> {
                    currentTemperatureView.text =
                        getString(R.string.temperature_value, it.data!!.temperature.celsius)
                }
                Status.ERROR -> {
                    // ignore
                }
            }
        })
    }
}
