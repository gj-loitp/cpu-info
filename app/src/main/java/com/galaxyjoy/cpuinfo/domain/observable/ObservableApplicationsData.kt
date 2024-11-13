package com.galaxyjoy.cpuinfo.domain.observable

import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.galaxyjoy.cpuinfo.data.provider.DataProviderApplications
import com.galaxyjoy.cpuinfo.domain.MutableInteractor
import com.galaxyjoy.cpuinfo.domain.model.ExtendedApplicationData
import com.galaxyjoy.cpuinfo.domain.model.SortOrder
import com.galaxyjoy.cpuinfo.utils.DispatchersProvider
import com.galaxyjoy.cpuinfo.utils.wrapToResultFlow
import com.galaxyjoy.cpuinfo.utils.wrappers.MyResult
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class ObservableApplicationsData @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    private val dataProviderApplications: DataProviderApplications,
    private val packageManager: PackageManager
) : MutableInteractor<ObservableApplicationsData.Params, MyResult<List<ExtendedApplicationData>>>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Params): Flow<MyResult<List<ExtendedApplicationData>>> {
        return wrapToResultFlow {
            val apps = dataProviderApplications.getInstalledApplications(params.withSystemApps)
                .map {
                    ExtendedApplicationData(
                        name = it.loadLabel(packageManager).toString(),
                        packageName = it.packageName,
                        sourceDir = it.sourceDir,
                        nativeLibraryDir = it.nativeLibraryDir,
                        hasNativeLibs = it.hasNativeLibs(),
                        appIconUri = getAppIconUri(it.packageName)
                    )
                }
            when (params.sortOrder) {
                SortOrder.ASCENDING -> apps.sortedBy { it.name.lowercase() }
                SortOrder.DESCENDING -> apps.sortedByDescending { it.name.lowercase() }
                else -> apps
            }
        }
    }

    private fun ApplicationInfo.hasNativeLibs(): Boolean {
        return if (nativeLibraryDir != null) {
            val fileDir = File(nativeLibraryDir)
            val list = fileDir.listFiles()
            list != null && list.isNotEmpty()
        } else {
            false
        }
    }

    private fun getAppIconUri(packageName: String): Uri {
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(packageName)
            .path(getResourceId(packageName).toString())
            .build()
    }

    @Suppress("DEPRECATION")
    private fun getResourceId(packageName: String): Int {
        val packageInfo: PackageInfo
        try {
            packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return 0
        }
        return packageInfo.applicationInfo.icon
    }

    data class Params(
        val withSystemApps: Boolean,
        val sortOrder: SortOrder = SortOrder.NONE
    )
}