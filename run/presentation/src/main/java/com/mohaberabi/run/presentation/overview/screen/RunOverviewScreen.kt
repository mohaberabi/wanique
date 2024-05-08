package com.mohaberabi.run.presentation.overview.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueFab
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueScaffold
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueToolbar
import com.mohaberabi.core.presentation.designsystem.theme.AnalyticsIcon
import com.mohaberabi.core.presentation.designsystem.theme.LogoIcon
import com.mohaberabi.core.presentation.designsystem.theme.LogoutIcon
import com.mohaberabi.core.presentation.designsystem.theme.RunIcon
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme
import com.mohaberabi.run.presentation.overview.viewmodel.RunOverviewAction
import com.mohaberabi.run.presentation.overview.viewmodel.RunOverviewViewModel
import org.koin.androidx.compose.koinViewModel
import  com.mohaberabi.core.presentation.ui.R
import com.mohaberabi.core.presentation.util.DropDownItem
import com.mohaberabi.run.presentation.maps.compose.RunListItem
import com.mohaberabi.run.presentation.overview.viewmodel.RunOverviewState


@Composable
fun RunOverViewRoot(
    viewModel: RunOverviewViewModel = koinViewModel(),
    onStartRunClick: () -> Unit,
    onLogout: () -> Unit,
    onAnalyticsClick: () -> Unit,
) {

    val state = viewModel.state


    RunOverViewScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is RunOverviewAction.OnAnalyticsClick -> onAnalyticsClick()
                is RunOverviewAction.OnStartClick -> onStartRunClick()
                is RunOverviewAction.OnLogoutClick -> onLogout()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RunOverViewScreen(
    state: RunOverviewState,
    onAction: (RunOverviewAction) -> Unit = {},

    ) {

    val topAppBarState = rememberTopAppBarState()

    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState,
    )
    RuniqueScaffold(
        fab = {
            RuniqueFab(
                icon = RunIcon,
                onClick = {
                    onAction(RunOverviewAction.OnStartClick)
                },
            )

        },

        topAppBar = {
            RuniqueToolbar(
                showBackButton = false,
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }, scrollBehaviour = scrollBehaviour,
                title = stringResource(id = R.string.runqiue),
                items = listOf(
                    DropDownItem(AnalyticsIcon, stringResource(id = R.string.anayltics)),
                    DropDownItem(LogoutIcon, stringResource(id = R.string.log_out))

                ),
                onMenuItemClick = { index ->
                    when (index) {
                        0 -> onAction(RunOverviewAction.OnAnalyticsClick)
                        1 -> onAction(RunOverviewAction.OnLogoutClick)
                    }
                }
            )


        }
    ) { padding ->

        LazyColumn(
            modifier =
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehaviour.nestedScrollConnection),
            contentPadding = padding
        ) {
            items(
                items = state.runs,
                key = { it.id },
            ) {

                    runItem ->
                RunListItem(
                    runUi = runItem,
                    modifier = Modifier
                        .animateItemPlacement()
                        .padding(16.dp),
                    onDeleteClick = { onAction(RunOverviewAction.OnDeleteRun(runItem.id)) })
            }
        }
    }
}


@Preview
@Composable
fun PreviewRunOverViewScreen() {

    RuniqueTheme {
        RunOverViewScreen(RunOverviewState())
    }
}