package com.roy93group.cpuinfo.domain.observable

import com.roy93group.cpuinfo.data.provider.CpuDataNativeProvider
import com.roy93group.cpuinfo.data.provider.CpuDataProvider
import com.roy93group.cpuinfo.domain.ImmutableInteractor
import com.roy93group.cpuinfo.domain.model.CpuData
import com.roy93group.cpuinfo.utils.DispatchersProvider
import com.roy93group.cpuinfo.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CpuDataObservable @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    private val cpuDataProvider: CpuDataProvider,
    private val cpuDataNativeProvider: CpuDataNativeProvider
) : ImmutableInteractor<Unit, CpuData>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        while (true) {
            val processorName = cpuDataNativeProvider.getCpuName()
            val abi = cpuDataProvider.getAbi()
            val coreNumber = cpuDataProvider.getNumberOfCores()
            val hasArmNeon = cpuDataNativeProvider.hasArmNeon()
            val frequencies = mutableListOf<CpuData.Frequency>()
            val l1dCaches = cpuDataNativeProvider.getL1dCaches()
                ?.joinToString(separator = "\n") { Utils.humanReadableByteCount(it.toLong()) }
                ?: ""
            val l1iCaches = cpuDataNativeProvider.getL1iCaches()
                ?.joinToString(separator = "\n") { Utils.humanReadableByteCount(it.toLong()) }
                ?: ""
            val l2Caches = cpuDataNativeProvider.getL2Caches()
                ?.joinToString(separator = "\n") { Utils.humanReadableByteCount(it.toLong()) }
                ?: ""
            val l3Caches = cpuDataNativeProvider.getL3Caches()
                ?.joinToString(separator = "\n") { Utils.humanReadableByteCount(it.toLong()) }
                ?: ""
            val l4Caches = cpuDataNativeProvider.getL4Caches()
                ?.joinToString(separator = "\n") { Utils.humanReadableByteCount(it.toLong()) }
                ?: ""
            for (i in 0 until coreNumber) {
                val (min, max) = cpuDataProvider.getMinMaxFreq(i)
                val current = cpuDataProvider.getCurrentFreq(i)
                frequencies.add(CpuData.Frequency(min, max, current))
            }
            emit(
                CpuData(
                    processorName, abi, coreNumber, hasArmNeon, frequencies,
                    l1dCaches, l1iCaches, l2Caches, l3Caches, l4Caches
                )
            )
            delay(REFRESH_DELAY)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 1000L
    }
}