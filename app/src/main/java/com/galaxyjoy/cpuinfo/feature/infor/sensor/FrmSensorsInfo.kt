package com.galaxyjoy.cpuinfo.feature.infor.sensor

import androidx.fragment.app.viewModels
import com.galaxyjoy.cpuinfo.feature.infor.base.BaseRvFragment
import com.galaxyjoy.cpuinfo.feature.infor.base.AdapterInfoItems
import com.galaxyjoy.cpuinfo.util.DividerItemDecoration
import com.galaxyjoy.cpuinfo.util.lifecycle.ListLiveDataObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FrmSensorsInfo : BaseRvFragment() {

    private val viewModel: VMSensorsInfo by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.startProvidingData()
    }

    override fun onStop() {
        viewModel.stopProvidingData()
        super.onStop()
    }

    override fun setupRecyclerViewAdapter() {
        val adapterInfoItems = AdapterInfoItems(
            viewModel.listLiveData,
            AdapterInfoItems.LayoutType.VERTICAL_LAYOUT, onClickListener = this
        )
        viewModel.listLiveData.listStatusChangeNotificator.observe(
            viewLifecycleOwner,
            ListLiveDataObserver(adapterInfoItems)
        )
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext()))
        recyclerView.adapter = adapterInfoItems
    }
}