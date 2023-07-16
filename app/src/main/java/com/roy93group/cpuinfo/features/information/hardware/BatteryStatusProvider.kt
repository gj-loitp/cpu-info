/*
 * Copyright 2017 KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roy93group.cpuinfo.features.information.hardware

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Return battery status from ACTION_BATTERY_CHANGED broadcast receiver and capacity from
 * [com.android.internal.os.PowerProfile]
 *
 */
@Singleton
class BatteryStatusProvider @Inject constructor(
    @ApplicationContext private val appContext: Context
) {

    /**
     * @return [Intent] with battery information
     */
    fun getBatteryStatusIntent(): Intent? {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        return appContext.registerReceiver(null, iFilter)
    }

    /**
     * @return battery capacity from private API. In case of error it will return -1.
     */
    @SuppressLint("PrivateApi")
    fun getBatteryCapacity(): Double {
        var capacity = -1.0
        try {
            val powerProfile = Class.forName("com.android.internal.os.PowerProfile")
                .getConstructor(Context::class.java).newInstance(appContext)
            capacity = Class
                .forName("com.android.internal.os.PowerProfile")
                .getMethod("getAveragePower", String::class.java)
                .invoke(powerProfile, "battery.capacity") as Double
        } catch (e: Exception) {
            Timber.e(e)
        }
        return capacity
    }
}