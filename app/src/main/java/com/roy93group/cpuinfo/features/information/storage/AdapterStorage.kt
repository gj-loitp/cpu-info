package com.roy93group.cpuinfo.features.information.storage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roy93group.cpuinfo.R
import com.roy93group.cpuinfo.utils.Utils
import com.roy93group.cpuinfo.utils.round2
import com.roy93group.cpuinfo.widgets.progress.IconRoundCornerProgressBar

/**
 * Adapter for items in [FragmentStorageInfo]
 *
 * @author roy93group
 */
class AdapterStorage(private val storageList: List<StorageItem>) :
    RecyclerView.Adapter<AdapterStorage.ViewHolder>() {

    override fun getItemCount(): Int = storageList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.v_item_storage, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(storageList[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val storageDescriptionTv: TextView =
            itemView.findViewById(R.id.storageDescriptionTv)
        private val storageProgress: IconRoundCornerProgressBar =
            itemView.findViewById(R.id.storageProgress)

        fun bindViewHolder(storageItem: StorageItem) {
            val totalReadable = Utils.humanReadableByteCount(storageItem.storageTotal)
            val usedReadable = Utils.humanReadableByteCount(storageItem.storageUsed)
            val usedPercent = (storageItem.storageUsed.toFloat()
                    / storageItem.storageTotal.toFloat() * 100.0).round2()

            val storageDesc = "${storageItem.type}: $usedReadable / $totalReadable ($usedPercent%)"
            storageDescriptionTv.text = storageDesc

            storageProgress.iconImageResource = storageItem.iconRes
            storageProgress.progress = usedPercent.toFloat()
        }
    }
}