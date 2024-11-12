package com.roy93group.cpuinfo.features.information

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.roy93group.cpuinfo.R
import com.roy93group.cpuinfo.databinding.FInfoBinding
import com.roy93group.cpuinfo.features.information.base.BaseFragment
import com.roy93group.cpuinfo.features.information.base.AdapterInfoContainerState
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment which is base for all hardware and software information fragments
 *
 */
@AndroidEntryPoint
class FragmentInfoContainer : BaseFragment<FInfoBinding>(R.layout.f_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AdapterInfoContainerState(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab: TabLayout.Tab, position: Int ->
            tab.text = resources.getText(adapter.getTitleRes(position))
        }.attach()
    }
}
