package com.roy93group.cpuinfo.domain.model

data class GpuData(
    val vulkanVersion: String,
    val glesVersio: String,
    val glVendor: String?,
    val glRenderer: String?,
    val glExtensions: String?
)