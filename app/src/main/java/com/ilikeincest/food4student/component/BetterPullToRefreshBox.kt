package com.ilikeincest.food4student.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Why do we need this?
 * Because PullToRefreshBox does not have enabled property for some fucking reason.
 * @param lazyListState State of the scrollable lazy list inside content to check if we are on top of the page
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetterPullToRefreshBox(
    lazyListState: LazyListState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val isScrolledToTop by remember { derivedStateOf {
        lazyListState.firstVisibleItemIndex == 0
                && lazyListState.firstVisibleItemScrollOffset == 0
    } }

    val pullState = rememberPullToRefreshState()
    Box(
        modifier = modifier.pullToRefresh(
            state = pullState,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            enabled = isScrolledToTop
        )
    ) {
        content()
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = pullState
        )
    }
}