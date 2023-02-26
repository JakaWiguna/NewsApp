package com.me.newsapp.presentation.news_articles

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.me.newsapp.presentation.component.DetailArticleScreen
import com.me.newsapp.presentation.component.EndlessListHandler
import com.me.newsapp.ui.theme.TextLight

@Composable
fun NewsArticlesScreen(
    viewModel: NewsArticlesViewModel,
    onNewsArticleClick: (url: String) -> Unit,
) {
    val state = viewModel.state

    val listState = rememberLazyListState()

    val mContext = LocalContext.current
    LaunchedEffect(key1 = mContext) {
        viewModel.event.collect { event ->
            when (event) {
                is NewsArticlesEvent.ShowToast -> {
                    Toast.makeText(mContext, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = state.isRefresh),
        onRefresh = viewModel::doRefresh,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    if (state.articles.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "No Articles Found",
                                    fontSize = 18.sp,
                                    color = TextLight,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                    items(items = state.articles, key = { it.url }) { item ->
                        DetailArticleScreen(
                            article = item,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) { url ->
                            onNewsArticleClick(url)
                        }
                    }
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
                EndlessListHandler(listState = listState) { isLoadMore ->
                    if (isLoadMore && state.canLoadMore) {
                        viewModel.onEvent(NewsArticlesEvent.OnLoadMore)
                    }
                }
            }
        }
    }
}