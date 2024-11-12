package com.roy93group.cpuinfo.features.temperature.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.roy93group.cpuinfo.databinding.VItemTemperatureBinding
import com.roy93group.cpuinfo.features.temperature.TemperatureFormatter

/**
 * Temperature list adapter which observe temperatureListLiveData
 *
 */
class AdapterTemperature(
    private val temperatureFormatter: TemperatureFormatter,
    private val temperatureList: List<TemperatureItem>
) : RecyclerView.Adapter<AdapterTemperature.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VItemTemperatureBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, temperatureFormatter)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(temperatureList[position])
    }

    override fun getItemCount(): Int = temperatureList.size

    class ViewHolder(
        private val binding: VItemTemperatureBinding,
        private val temperatureFormatter: TemperatureFormatter
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(temperatureItem: TemperatureItem) {
            with(binding) {
                temperatureIv.setImageResource(temperatureItem.iconRes)
                temperatureTypeTv.text = temperatureItem.name
                temperatureTv.text = temperatureFormatter.format(temperatureItem.temperature)
            }
        }
    }
}