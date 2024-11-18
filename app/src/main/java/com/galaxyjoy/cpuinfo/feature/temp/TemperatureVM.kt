package com.galaxyjoy.cpuinfo.feature.temp

import android.content.res.Resources
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import com.galaxyjoy.cpuinfo.R
import com.galaxyjoy.cpuinfo.feature.temp.list.TemperatureItem
import com.galaxyjoy.cpuinfo.util.NonNullMutableLiveData
import com.galaxyjoy.cpuinfo.util.Prefs
import com.galaxyjoy.cpuinfo.util.lifecycle.ListLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel for [FrmTemperature]
 *
 */
@HiltViewModel
class TemperatureVM @Inject constructor(
    private val prefs: Prefs,
    private val resources: Resources,
    private val temperatureIconProvider: TemperatureIconProvider,
    private val temperatureProvider: TemperatureProvider
) : ViewModel() {

    companion object {
        private const val CPU_TEMP_RESULT_KEY = "temp_result_key"
    }

    // Binding fields
    val isLoading = NonNullMutableLiveData(false)
    val isError = NonNullMutableLiveData(false)

    val temperatureListLiveData = ListLiveData<TemperatureItem>()

    private var temperatureDisposable: Disposable? = null
    private var refreshingDisposable: Disposable? = null
    private var cpuTemperatureResult: TemperatureProvider.CpuTemperatureResult? = null
    private var isBatteryTemperatureAvailable = false

    /**
     * Start temperature getting process. It also validates all temperatures availability.
     */
    fun startTemperatureRefreshing() {
        Timber.i("startTemperatureRefreshing()")
        if (prefs.contains(CPU_TEMP_RESULT_KEY)) {
            cpuTemperatureResult = prefs.get(
                CPU_TEMP_RESULT_KEY,
                TemperatureProvider.CpuTemperatureResult()
            )
            verifyTemperaturesAvailability()
        } else {
            temperatureDisposable = getCpuAvailabilityTest()
        }
    }

    /**
     * Stop temperature getting process
     */
    fun stopTemperatureRefreshing() {
        Timber.i("stopTemperatureRefreshing()")
        refreshingDisposable?.dispose()
    }

    /**
     * Try to find path with CPU temperature. If success try validate temperatures and schedule
     * refreshing process.
     */
    private fun getCpuAvailabilityTest(): Disposable {
        return temperatureProvider.getCpuTemperatureFinder()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                isLoading.value = true
                isError.value = false
            }
            .doFinally {
                isLoading.value = false
                verifyTemperaturesAvailability()
            }
            .subscribe({ temperatureResult ->
                prefs.insert(CPU_TEMP_RESULT_KEY, temperatureResult)
                cpuTemperatureResult = temperatureResult
            }, Timber::e, { Timber.i("List scan complete") })
    }

    /**
     * Verify which temperatures are available and schedule refreshing. If we don't have any
     * temperature info set isError flag to true
     */
    private fun verifyTemperaturesAvailability() {
        if (!isBatteryTemperatureAvailable) {
            val batteryTemp = temperatureProvider.getBatteryTemperature()
            if (batteryTemp != 0) {
                isBatteryTemperatureAvailable = true
            }
        }

        if (isBatteryTemperatureAvailable || cpuTemperatureResult != null) {
            scheduleRefreshing()
        } else {
            isError.value = true
        }
    }

    /**
     * Schedule refreshing process (for 3s)
     */
    private fun scheduleRefreshing() {
        if (refreshingDisposable != null) {
            refreshingDisposable!!.dispose()
        }

        refreshingDisposable = getRefreshingInvoker()
            .map {
                var batteryTemp: Int? = null
                if (isBatteryTemperatureAvailable) {
                    batteryTemp = temperatureProvider.getBatteryTemperature()
                }
                var cpuTemp: Float? = null
                if (cpuTemperatureResult != null) {
                    cpuTemp = temperatureProvider.getCpuTemp(cpuTemperatureResult!!.filePath)
                }
                TempContainer(cpuTemp, batteryTemp)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (cpuTemp, batteryTemp) ->
                val temporaryTempList = ArrayList<TemperatureItem>()
                if (cpuTemp != null) {
                    temporaryTempList.add(
                        TemperatureItem(
                            temperatureIconProvider.getIcon(
                                TemperatureIconProvider.Type.CPU
                            ),
                            resources.getString(R.string.cpu), cpuTemp
                        )
                    )
                }
                if (batteryTemp != null) {
                    temporaryTempList.add(
                        TemperatureItem(
                            temperatureIconProvider.getIcon(
                                TemperatureIconProvider.Type.BATTERY
                            ),
                            resources.getString(R.string.battery),
                            batteryTemp.toFloat()
                        )
                    )
                }

                temperatureListLiveData.replace(temporaryTempList)
            }, Timber::e)
    }

    /**
     * Return refreshing invoker
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    internal fun getRefreshingInvoker(): Observable<Long> =
        Observable.interval(0, 3, TimeUnit.SECONDS)

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared()")
        temperatureDisposable?.dispose()
        refreshingDisposable?.dispose()
    }

    private data class TempContainer(val cpuTemp: Float?, val batteryTemp: Int?)
}
