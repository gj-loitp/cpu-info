package com.galaxyjoy.cpuinfo.feat.infor.gpu

import android.content.Context
import com.airbnb.epoxy.TypedEpoxyController
import com.galaxyjoy.cpuinfo.R
import com.galaxyjoy.cpuinfo.itemValue
import com.galaxyjoy.cpuinfo.verticalDivider

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
