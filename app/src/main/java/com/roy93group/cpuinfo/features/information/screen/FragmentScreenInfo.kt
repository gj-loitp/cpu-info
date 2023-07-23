package com.roy93group.cpuinfo.features.information.screen

import androidx.fragment.app.viewModels
import com.roy93group.cpuinfo.features.information.base.BaseRvFragment
import com.roy93group.cpuinfo.features.information.base.AdapterInfoItems
import com.roy93group.cpuinfo.utils.DividerItemDecoration
import com.roy93group.cpuinfo.utils.lifecycle.ListLiveDataObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentScreenInfo : BaseRvFragment() {

    private val viewModel: ViewModelScreenInfo by viewModels()

    override fun setupRecyclerViewAdapter() {
        val adapterInfoItems = AdapterInfoItems(
            viewModel.listLiveData,
            AdapterInfoItems.LayoutType.HORIZONTAL_LAYOUT, onClickListener = this
        )
        viewModel.listLiveData.listStatusChangeNotificator.observe(
            viewLifecycleOwner,
            ListLiveDataObserver(adapterInfoItems)
        )
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext()))
        recyclerView.adapter = adapterInfoItems
    }
}
