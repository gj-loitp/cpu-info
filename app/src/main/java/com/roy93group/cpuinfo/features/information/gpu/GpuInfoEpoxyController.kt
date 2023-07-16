package com.roy93group.cpuinfo.features.information.gpu

import android.content.Context
import com.airbnb.epoxy.TypedEpoxyController
import com.roy93group.cpuinfo.R
import com.roy93group.cpuinfo.itemValue
import com.roy93group.cpuinfo.verticalDivider

class GpuInfoEpoxyController(
    private val context: Context
) : TypedEpoxyController<GpuInfoViewState>() {

    override fun buildModels(data: GpuInfoViewState) {
        itemValue {
            id("vulkan_version")
            title(this@GpuInfoEpoxyController.context.getString(R.string.vulkan_version))
            value(data.gpuData.vulkanVersion)
        }
        verticalDivider { id("vulkan_divider") }
        itemValue {
            id("gles_version")
            title(this@GpuInfoEpoxyController.context.getString(R.string.gles_version))
            value(data.gpuData.glesVersio)
        }
        if (data.gpuData.glVendor != null) {
            verticalDivider { id("gl_vendor_divider") }
            itemValue {
                id("gl_vendor")
                title(this@GpuInfoEpoxyController.context.getString(R.string.vendor))
                value(data.gpuData.glVendor)
            }
        }
        if (data.gpuData.glRenderer != null) {
            verticalDivider { id("gl_renderer_divider") }
            itemValue {
                id("gl_renderer")
                title(this@GpuInfoEpoxyController.context.getString(R.string.renderer))
                value(data.gpuData.glRenderer)
            }
        }
        if (data.gpuData.glExtensions != null) {
            verticalDivider { id("gl_extensions_divider") }
            itemValue {
                id("gl_extensions")
                title(this@GpuInfoEpoxyController.context.getString(R.string.extensions))
                value(data.gpuData.glExtensions)
            }
        }
    }
}