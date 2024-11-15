package com.galaxyjoy.cpuinfo.features.processes

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.galaxyjoy.cpuinfo.R
import com.galaxyjoy.cpuinfo.databinding.FrmProcessesBinding
import com.galaxyjoy.cpuinfo.features.information.base.BaseFragment
import com.galaxyjoy.cpuinfo.utils.DividerItemDecoration
import com.galaxyjoy.cpuinfo.utils.lifecycle.ListLiveDataObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentProcesses : BaseFragment<FrmProcessesBinding>(R.layout.frm_processes) {

    private val viewModel: ProcessesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setupRecyclerView()

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_process, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menuActionSorting -> {
                        viewModel.changeProcessSorting()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        binding.rv.adapter = null
        super.onDestroyView()
    }

    /**
     * Setup for [RecyclerView]
     */
    private fun setupRecyclerView() {
        val adapterProcesses = AdapterProcesses(viewModel.processList)
        viewModel.processList.listStatusChangeNotificator.observe(
            viewLifecycleOwner,
            ListLiveDataObserver(adapterProcesses)
        )

        val rvLayoutManager = LinearLayoutManager(requireContext())
        binding.rv.apply {
            layoutManager = rvLayoutManager
            adapter = adapterProcesses
            addItemDecoration(DividerItemDecoration(requireContext()))
            (this.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startProcessRefreshing()
    }

    override fun onStop() {
        viewModel.stopProcessRefreshing()
        super.onStop()
    }
}
