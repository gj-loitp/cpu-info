package com.galaxyjoy.cpuinfo.feat.infor.storage

import androidx.annotation.Keep

/**
 * Container for storage items (e.g. Internal, External, SD)
 *
 */
@Keep
data class StorageItem(
    val type: String,
    val iconRes: Int,
    val storageTotal: Long,
    val storageUsed: Long
)
