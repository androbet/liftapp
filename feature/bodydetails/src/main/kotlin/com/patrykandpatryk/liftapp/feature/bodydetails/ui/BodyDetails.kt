package com.patrykandpatryk.liftapp.feature.bodydetails.ui

import androidx.compose.foundation.layout.WindowInsets.Companion
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.toPaddingValues
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.navigation.Routes.InsertBodyEntry
import com.patrykandpatryk.liftapp.core.navigation.composable
import com.patrykandpatryk.liftapp.core.provider.navigator
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListItemWithOptions
import com.patrykandpatryk.liftapp.core.ui.OptionItem
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer

fun NavGraphBuilder.addBodyDetails() {

    composable(route = Routes.BodyDetails) {
        BodyDetails()
    }
}
@Composable
fun BodyDetails(
    modifier: Modifier = Modifier,
) {

    val viewModel: BodyDetailsViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()

    BodyDetails(
        onIntent = viewModel::handleIntent,
        state = state,
        modelProducer = viewModel.chartEntryModelProducer,
        modifier = modifier,
    )
}

@Composable
private fun BodyDetails(
    onIntent: (Intent) -> Unit,
    state: ScreenState,
    modelProducer: ChartEntryModelProducer,
    modifier: Modifier = Modifier,
) {

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navigator = navigator

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = state.name,
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = navigator::popBackStack,
            )
        },
        floatingActionButton = {

            ExtendedFloatingActionButton(
                modifier = Modifier.navigationBarsPadding(),
                text = { Text(text = stringResource(id = R.string.action_new_entry)) },
                icon = { Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = null) },
                onClick = { navigator.navigate(InsertBodyEntry.create(state.bodyId)) },
            )
        },
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues),
            contentPadding = Companion.navigationBars.toPaddingValues(),
        ) {

            item {

                Chart(
                    modifier = Modifier.padding(
                        top = LocalDimens.current.padding.itemVertical,
                        bottom = LocalDimens.current.padding.itemVertical,
                        start = LocalDimens.current.padding.contentHorizontal,
                    ),
                    chart = lineChart(
                        axisValuesOverrider = AxisValuesOverrider.adaptiveYValues(yFraction = 1.1f),
                    ),
                    chartModelProducer = modelProducer,
                    startAxis = startAxis(
                        maxLabelCount = 3,
                    ),
                    bottomAxis = bottomAxis(),
                )
            }

            item {
                Text(
                    modifier = Modifier.padding(
                        vertical = LocalDimens.current.padding.itemVertical,
                        horizontal = LocalDimens.current.padding.contentHorizontal,
                    ),
                    text = stringResource(id = R.string.generic_journal),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            items(items = state.entries, key = { it.id }) { entry ->

                ListItemWithOptions(
                    mainContent = {
                        ListItem(
                            modifier = Modifier.animateItemPlacement(),
                            title = entry.value,
                            description = entry.date,
                        )
                    },
                    optionItems = listOf(
                        OptionItem(
                            iconPainter = painterResource(id = R.drawable.ic_edit),
                            label = stringResource(id = R.string.action_edit),
                            onClick = {
                                navigator.navigate(InsertBodyEntry.create(state.bodyId, entry.id))
                            },
                        ),
                        OptionItem(
                            iconPainter = painterResource(id = R.drawable.ic_delete),
                            label = stringResource(id = R.string.action_delete),
                            onClick = {},
                        ),
                    ),
                    isExpanded = entry.isExpanded,
                    setExpanded = { onIntent(Intent.ExpandItem(id = entry.id)) },
                )
            }
        }
    }
}
