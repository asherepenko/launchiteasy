package com.sherepenko.android.launchiteasy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.data.TemperatureItem
import com.sherepenko.android.launchiteasy.data.celsius
import com.sherepenko.android.launchiteasy.data.fahrenheit
import com.sherepenko.android.launchiteasy.databinding.ItemForecastBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ForecastsAdapter : BaseRecyclerAdapter<ForecastItem, ForecastsAdapter.ViewHolder>() {

    var isMetricSystem: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemForecastBinding.inflate(LayoutInflater.from(parent.context)))

    inner class ViewHolder(
        private val binding: ItemForecastBinding
    ) : BaseRecyclerViewHolder<ForecastItem>(binding.root) {

        override fun bindItem(item: ForecastItem) {
            binding.apply {
                forecastTimeView.text = item.timestamp.format()
                forecastIconView.text = item.condition.icon.glyph
                forecastTemperatureView.text = item.temperature.format()
            }
        }

        private fun TemperatureItem.format(): String =
            itemView.context.getString(
                R.string.temperature_value,
                if (isMetricSystem) {
                    celsius
                } else {
                    fahrenheit
                }
            )
    }
}

private fun Instant.format(): String =
    LocalDateTime.ofInstant(this, ZoneId.systemDefault()).format(
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    )
