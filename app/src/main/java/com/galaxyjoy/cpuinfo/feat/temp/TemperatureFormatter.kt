package com.galaxyjoy.cpuinfo.feat.temp

import com.galaxyjoy.cpuinfo.feat.setting.FrmSettings
import com.galaxyjoy.cpuinfo.util.Prefs
import com.galaxyjoy.cpuinfo.util.round2
import javax.inject.Inject

/**
 * Format temperature using user settings
 *
 */
class TemperatureFormatter @Inject constructor(val prefs: Prefs) {

    companion object {
        const val CELSIUS = 0
        const val FAHRENHEIT = 1
        const val KELVIN = 2
    }

    /**
     * Format temperature for current settings
     *
     * @param temp formatting temperature which will be formatted (passed in Celsius unit)
     */
    fun format(temp: Float): String {
        val tempUnit = prefs.get(FrmSettings.KEY_TEMPERATURE_UNIT, CELSIUS.toString())
            .toInt()
        return if (tempUnit == FAHRENHEIT) {
            val fahrenheit = temp * 9 / 5 + 32
            "${fahrenheit.round2()}\u00B0F"
        } else return if (tempUnit == KELVIN) {
            val kelvin = temp + 273.15
            "${kelvin.round2()}\u00B0K"
        } else {
            val tempFormatted = "${temp.toInt()}\u00B0C"
            tempFormatted
        }
    }
}