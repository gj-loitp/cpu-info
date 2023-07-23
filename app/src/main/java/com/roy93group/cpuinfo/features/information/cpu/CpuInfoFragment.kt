/*
 * Copyright 2017 KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roy93group.cpuinfo.features.information.cpu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.roy93group.cpuinfo.R
import com.roy93group.cpuinfo.databinding.FRecyclerViewBinding
import com.roy93group.cpuinfo.features.information.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Displays information about device CPU taken form /proc/cpuinfo file
 *
 * @author roy93group
 */
@AndroidEntryPoint
class CpuInfoFragment : BaseFragment<FRecyclerViewBinding>(R.layout.f_recycler_view) {

    private val viewModel: CpuInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller = CpuInfoEpoxyController(requireContext())
        binding.recyclerView.adapter = controller.adapter
        viewModel.viewState.observe(viewLifecycleOwner, { controller.setData(it) })
    }
}