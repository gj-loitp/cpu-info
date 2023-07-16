package com.roy93group.cpuinfo.features.applications

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.roy93group.cpuinfo.domain.model.ExtendedApplicationData
import com.roy93group.cpuinfo.ui.components.CpuSnackbar
import com.roy93group.cpuinfo.ui.components.CpuSwitchBox
import com.roy93group.cpuinfo.ui.components.DraggableBox
import com.roy93group.cpuinfo.ui.components.SurfaceTopAppBar
import com.roy93group.cpuinfo.ui.theme.CpuInfoTheme
import com.roy93group.cpuinfo.ui.theme.rowActionIconSize
import com.roy93group.cpuinfo.ui.theme.spacingSmall
import com.roy93group.cpuinfo.ui.theme.spacingXSmall
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import com.roy93group.cpuinfo.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ApplicationsScreen(
    uiState: NewApplicationsViewModel.UiState,
    onAppClicked: (packageName: String) -> Unit,
    onRefreshApplications: () -> Unit,
    onSnackbarDismissed: () -> Unit,
    onCardExpanded: (id: String) -> Unit,
    onCardCollapsed: (id: String) -> Unit,
    onAppUninstallClicked: (id: String) -> Unit,
    onAppSettingsClicked: (id: String) -> Unit,
    onNativeLibsClicked: (nativeLibraryDir: String) -> Unit,
    onSystemAppsSwitched: (enabled: Boolean) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        scope.launch {
            if (uiState.snackbarMessage != -1) {
                val result = snackbarHostState.showSnackbar(
                    context.getString(uiState.snackbarMessage)
                )
                if (result == SnackbarResult.Dismissed) {
                    onSnackbarDismissed()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopBar(
                withSystemApps = uiState.withSystemApps,
                onSystemAppsSwitched = onSystemAppsSwitched
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                CpuSnackbar(data)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPaddingModifier ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = uiState.isLoading,
            onRefresh = { onRefreshApplications() },
        )
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .padding(innerPaddingModifier),
        ) {
            ApplicationsList(
                appList = uiState.applications,
                revealedCardId = uiState.revealedCardId,
                onAppClicked = onAppClicked,
                onCardExpanded = onCardExpanded,
                onCardCollapsed = onCardCollapsed,
                onAppUninstallClicked = onAppUninstallClicked,
                onAppSettingsClicked = onAppSettingsClicked,
                onNativeLibsClicked = onNativeLibsClicked,
            )
            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun TopBar(
    withSystemApps: Boolean,
    onSystemAppsSwitched: (enabled: Boolean) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    SurfaceTopAppBar(
        title = stringResource(id = R.string.applications),
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        CpuSwitchBox(
                            text = stringResource(id = R.string.apps_show_system_apps),
                            isChecked = withSystemApps,
                            onCheckedChange = { onSystemAppsSwitched(!withSystemApps) }
                        )
                    },
                    onClick = { onSystemAppsSwitched(!withSystemApps) },
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.apps_sort_order),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    onClick = { },
                )
            }
        }
    )
}

@Composable
private fun ApplicationsList(
    appList: List<ExtendedApplicationData>,
    revealedCardId: String?,
    onAppClicked: (packageName: String) -> Unit,
    onCardExpanded: (id: String) -> Unit,
    onCardCollapsed: (id: String) -> Unit,
    onAppUninstallClicked: (id: String) -> Unit,
    onAppSettingsClicked: (id: String) -> Unit,
    onNativeLibsClicked: (nativeLibraryDir: String) -> Unit,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
    ) {
        itemsIndexed(
            items = appList,
            key = { _, item -> item.packageName }
        ) { index, item ->
            DraggableBox(
                isRevealed = revealedCardId == item.packageName,
                onExpand = { onCardExpanded(item.packageName) },
                onCollapse = { onCardCollapsed(item.packageName) },
                actionRow = {
                    Row {
                        IconButton(
                            modifier = Modifier.size(rowActionIconSize),
                            onClick = { onAppSettingsClicked(item.packageName) },
                            content = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_settings),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = stringResource(id = R.string.settings),
                                )
                            }
                        )
                        IconButton(
                            modifier = Modifier.size(56.dp),
                            onClick = { onAppUninstallClicked(item.packageName) },
                            content = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_thrash),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null,
                                )
                            }
                        )
                    }
                },
                content = {
                    ApplicationItem(
                        appData = item,
                        onAppClicked = onAppClicked,
                        onNativeLibsClicked = onNativeLibsClicked,
                    )
                }
            )
            if (index < appList.lastIndex) {
                Divider()
            }
        }
    }
}

@Composable
private fun ApplicationItem(
    appData: ExtendedApplicationData,
    onAppClicked: (packageName: String) -> Unit,
    onNativeLibsClicked: (nativeLibraryDir: String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .clickable(onClick = { onAppClicked(appData.packageName) })
            .padding(spacingSmall),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(appData.appIconUri)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
        )
        Column(
            modifier = Modifier.padding(horizontal = spacingXSmall)
        ) {
            Text(
                text = appData.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = appData.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        if (appData.hasNativeLibs) {
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(spacingXSmall))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.ic_c_plus_plus)
                    .build(),
                contentDescription = stringResource(id = R.string.native_libs),
                modifier = Modifier
                    .requiredSize(40.dp)
                    .clickable { appData.nativeLibraryDir?.let { onNativeLibsClicked(it) } },
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ApplicationInfoPreview() {
    CpuInfoTheme {
        ApplicationsScreen(
            uiState = NewApplicationsViewModel.UiState(
                applications = persistentListOf(previewAppData1, previewAppData2)
            ),
            onAppClicked = {},
            onRefreshApplications = {},
            onSnackbarDismissed = {},
            onCardExpanded = {},
            onCardCollapsed = {},
            onAppSettingsClicked = {},
            onAppUninstallClicked = {},
            onNativeLibsClicked = {},
            onSystemAppsSwitched = {},
        )
    }
}

private val previewAppData1 = ExtendedApplicationData(
    "Cpu Info",
    "com.roy93group.cpuinfo",
    "/testDir",
    null,
    false,
    Uri.parse("https://avatars.githubusercontent.com/u/6407041?s=32&v=4")
)

private val previewAppData2 = ExtendedApplicationData(
    "Cpu Info1",
    "com.roy93group.cpuinfo1",
    "/testDir",
    null,
    false,
    Uri.parse("https://avatars.githubusercontent.com/u/6407041?s=32&v=4")
)
