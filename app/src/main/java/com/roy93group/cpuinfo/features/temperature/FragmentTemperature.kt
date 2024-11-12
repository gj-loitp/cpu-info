package com.roy93group.cpuinfo.features.temperature

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.roy93group.cpuinfo.R
import com.roy93group.cpuinfo.databinding.FTemperatureBinding
import com.roy93group.cpuinfo.features.information.base.BaseFragment
import com.roy93group.cpuinfo.features.temperature.list.AdapterTemperature
import com.roy93group.cpuinfo.utils.lifecycle.ListLiveDataObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentTemperature : BaseFragment<FTemperatureBinding>(
    R.layout.f_temperature
) {

    private val viewModel: TemperatureViewModel by viewModels()

    @Inject
    lateinit var temperatureFormatter: TemperatureFormatter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setupRecycleView()
    }

    override fun onStart() {
        super.onStart()
        viewModel.startTemperatureRefreshing()
    }

    override fun onStop() {
        viewModel.stopTemperatureRefreshing()
        super.onStop()
    }

    private fun setupRecycleView() {
        val adapterTemperature = AdapterTemperature(
            temperatureFormatter,
            viewModel.temperatureListLiveData
        )
        viewModel.temperatureListLiveData.listStatusChangeNotificator.observe(
            viewLifecycleOwner,
            ListLiveDataObserver(adapterTemperature)
        )
        binding.apply {
            tempRv.layoutManager = LinearLayoutManager(requireContext())
            tempRv.adapter = adapterTemperature
            (tempRv.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }
}