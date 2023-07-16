package com.kgurgul.cpuinfo.domain.result

import android.content.Context
import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.roy93group.utils.DispatchersProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetPackageNameInteractor @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    @ApplicationContext private val context: Context
) : ResultInteractor<Unit, String>() {

    override val dispatcher = dispatchersProvider.io

    override suspend fun doWork(params: Unit): String {
        return context.packageName
    }
}