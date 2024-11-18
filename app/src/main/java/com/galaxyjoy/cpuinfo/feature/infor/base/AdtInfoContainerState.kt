package com.galaxyjoy.cpuinfo.feature.infor.base

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.galaxyjoy.cpuinfo.R
import com.galaxyjoy.cpuinfo.feature.infor.android.FrmAndroidInfo
import com.galaxyjoy.cpuinfo.feature.infor.cpu.FrmCpuInfo
import com.galaxyjoy.cpuinfo.feature.infor.gpu.FrmGpuInfo
import com.galaxyjoy.cpuinfo.feature.infor.hardware.FrmHardwareInfo
import com.galaxyjoy.cpuinfo.feature.infor.ram.FrmRamInfo
import com.galaxyjoy.cpuinfo.feature.infor.screen.FrmScreenInfo
import com.galaxyjoy.cpuinfo.feature.infor.sensor.FrmSensorsInfo
import com.galaxyjoy.cpuinfo.feature.infor.storage.FrmStorageInfo

/**
 * Simple view pager for info fragments
 */
class AdtInfoContainerState(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment =
        when (position) {
            CPU_POS -> FrmCpuInfo()
            GPU_POS -> FrmGpuInfo()
            RAM_POS -> FrmRamInfo()
            STORAGE_POS -> FrmStorageInfo()
            SCREEN_POS -> FrmScreenInfo()
            ANDROID_POS -> FrmAndroidInfo()
            HARDWARE_POS -> FrmHardwareInfo()
            SENSORS_POS -> FrmSensorsInfo()
            else -> throw IllegalArgumentException("Unknown position for ViewPager2")
        }

    override fun getItemCount(): Int = INFO_PAGE_AMOUNT

    fun getTitleRes(position: Int) = when (position) {
        CPU_POS -> R.string.cpu
        GPU_POS -> R.string.gpu
        RAM_POS -> R.string.ram
        STORAGE_POS -> R.string.storage
        SCREEN_POS -> R.string.screen
        ANDROID_POS -> R.string.android
        HARDWARE_POS -> R.string.hardware
        SENSORS_POS -> R.string.sensors
        else -> throw IllegalArgumentException("Unknown position for ViewPager2")
    }

    companion object {
        private const val CPU_POS = 0
        private const val GPU_POS = 1
        private const val RAM_POS = 2
        private const val STORAGE_POS = 3
        private const val SCREEN_POS = 4
        private const val ANDROID_POS = 5
        private const val HARDWARE_POS = 6
        private const val SENSORS_POS = 7

        private const val INFO_PAGE_AMOUNT = 8
    }
}
