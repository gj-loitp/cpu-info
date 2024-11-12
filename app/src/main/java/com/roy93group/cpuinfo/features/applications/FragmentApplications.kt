package com.roy93group.cpuinfo.features.applications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.roy93group.cpuinfo.R
import com.roy93group.cpuinfo.databinding.FApplicationsBinding
import com.roy93group.cpuinfo.ext.openBrowserPolicy
import com.roy93group.cpuinfo.features.information.base.BaseFragment
import com.roy93group.cpuinfo.utils.DividerItemDecoration
import com.roy93group.cpuinfo.utils.Utils
import com.roy93group.cpuinfo.utils.lifecycle.ListLiveDataObserver
import com.roy93group.cpuinfo.utils.uninstallApp
import com.roy93group.cpuinfo.utils.wrappers.EventObserver
import com.roy93group.cpuinfo.widgets.swiperv.SwipeMenuRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import moreApp
import rateApp
import shareApp
import java.io.File

@AndroidEntryPoint
class FragmentApplications : BaseFragment<FApplicationsBinding>(
    R.layout.f_applications
), AdapterApplications.ItemClickListener {

    private val viewModel: ViewModelApplications by viewModels()

    private val uninstallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.refreshApplicationsList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerUninstallBroadcast()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.accent,
            R.color.primaryDark
        )
        setupRecyclerView()
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_apps, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.actionSorting -> {
                        viewModel.changeAppsSorting()
                        true
                    }
                    R.id.actionRate -> {
                        activity?.let{
                            it.rateApp("com.roy93group.cpuinfo")
                        }
                        true
                    }
                    R.id.actionMore -> {
                        activity?.moreApp()
                        true
                    }
                    R.id.actionShare -> {
                        activity?.shareApp()
                        true
                    }
                    R.id.actionPolicy -> {
                        context?.openBrowserPolicy()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        initObservables()
    }

    /**
     * Setup for [SwipeMenuRecyclerView]
     */
    private fun setupRecyclerView() {
        val adapterApplications = AdapterApplications(viewModel.applicationList, this)
        viewModel.applicationList.listStatusChangeNotificator.observe(
            viewLifecycleOwner,
            ListLiveDataObserver(adapterApplications)
        )

        binding.recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = adapterApplications
            addItemDecoration(DividerItemDecoration(requireContext()))
        }
    }

    /**
     * Register all fields from [ViewModelApplications] which should be observed
     */
    private fun initObservables() {
        viewModel.shouldStartStorageServiceEvent.observe(viewLifecycleOwner, EventObserver {
            ServiceStorageUsage.startService(requireContext(), viewModel.applicationList)
        })
    }

    /**
     * Register broadcast receiver for uninstalling apps
     */
    private fun registerUninstallBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        requireActivity().registerReceiver(uninstallReceiver, intentFilter)
    }


    /**
     * Try to open clicked app. In case of error show [Snackbar].
     */
    override fun appOpenClicked(position: Int) {
        val appInfo = viewModel.applicationList[position]
        // Block self opening
        if (appInfo.packageName == requireContext().packageName) {
            Snackbar.make(
                binding.mainContainer, getString(R.string.cpu_open),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        val intent = requireContext().packageManager.getLaunchIntentForPackage(appInfo.packageName)
        if (intent != null) {
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Snackbar.make(
                    binding.mainContainer, getString(R.string.app_open),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } else {
            Snackbar.make(
                binding.mainContainer, getString(R.string.app_open),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Open settings activity for selected app
     */
    override fun appSettingsClicked(position: Int) {
        val appInfo = viewModel.applicationList[position]
        val uri = Uri.fromParts("package", appInfo.packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
        startActivity(intent)
    }

    /**
     * Try to uninstall selected app
     */
    override fun appUninstallClicked(position: Int) {
        val appInfo = viewModel.applicationList[position]
        if (appInfo.packageName == requireContext().packageName) {
            Snackbar.make(
                binding.mainContainer, getString(R.string.cpu_uninstall),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }
        requireActivity().uninstallApp(appInfo.packageName)
    }

    /**
     * Open dialog with native lib list and open google if user taps on it
     */
    override fun appNativeLibsClicked(nativeDir: String) {
        showNativeListDialog(nativeDir)
    }

    /**
     * Create dialog with native libraries list
     */
    @SuppressLint("InflateParams")
    private fun showNativeListDialog(nativeLibsDir: String) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.dlg_native_libs, null)
        val nativeDirFile = File(nativeLibsDir)
        val libs = nativeDirFile.listFiles()?.map { it.name } ?: emptyList()

        val listView: ListView = dialogLayout.findViewById(R.id.dialogLv)
        val arrayAdapter = ArrayAdapter(
            requireContext(), R.layout.v_item_native_libs,
            R.id.nativeNameTv, libs
        )
        listView.adapter = arrayAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Utils.searchInGoogle(requireContext(), libs[position])
        }
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.cancel()
        }
        builder.setView(dialogLayout)
        val alert = builder.create()
        alert.show()
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(uninstallReceiver)
        super.onDestroy()
    }
}