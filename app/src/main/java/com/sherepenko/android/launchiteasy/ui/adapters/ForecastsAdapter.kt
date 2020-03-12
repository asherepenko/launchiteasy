package com.sherepenko.android.launchiteasy.ui.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.sherepenko.android.launchiteasy.R
import com.sherepenko.android.launchiteasy.data.ForecastItem
import com.sherepenko.android.launchiteasy.utils.inflate
import kotlinx.android.synthetic.main.item_forecast.view.forecastIconView
import kotlinx.android.synthetic.main.item_forecast.view.forecastTemperatureView
import kotlinx.android.synthetic.main.item_forecast.view.forecastTimeView
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class ForecastsAdapter :
        BaseRecyclerAdapter<ForecastItem, ForecastsAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ForecastItem>() {
            override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean =
                oldItem.timestamp == newItem.timestamp

            override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean =
                oldItem == newItem
        }
    }

    var isMetricSystem: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.item_forecast))

    inner class ViewHolder(
        itemView: View
    ) : BaseRecyclerViewHolder<ForecastItem>(itemView) {

        override fun bindItem(item: ForecastItem) {
            itemView.apply {
                forecastTimeView.text = item.timestamp.format()
                forecastIconView.text = item.condition.icon.glyph
                forecastTemperatureView.text =
                    context.getString(R.string.temperature_value,
                        if (isMetricSystem) {
                            item.temperature.celsius
                        } else {
                            item.temperature.fahrenheit
                        }
                    )
            }
        }
    }

    private fun Instant.format(): String =
        LocalDateTime.ofInstant(this@format, ZoneId.systemDefault())
            .format(
                DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            )
}
