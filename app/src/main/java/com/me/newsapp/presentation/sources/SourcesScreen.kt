package com.me.newsapp.presentation.sources

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.ExperimentalPagingApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.me.newsapp.domain.model.Source
import com.me.newsapp.ui.theme.GreyLight
import com.me.newsapp.ui.theme.TextDisabledLight
import com.me.newsapp.ui.theme.TextLight
import com.me.newsapp.ui.theme.TextMutedLight

@ExperimentalPagingApi
@Composable
fun SourcesScreen(
    onSourcesClick: (sources: String) -> Unit,
    viewModel: SourcesViewModel,
) {
    val state = viewModel.state

    val mContext = LocalContext.current
    LaunchedEffect(key1 = mContext) {
        viewModel.event.collect { event ->
            when (event) {
                is SourcesEvent.ShowToast -> {
                    Toast.makeText(mContext, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(items = state.sources) { index, item ->
                    if (index % 2 == 0) {
                        SourcesItemEven(index = index,
                            item = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(GreyLight),
                            onClick = {
                                onSourcesClick(item.id)
                            })
                    } else {
                        SourcesItemOdd(index = index,
                            item = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(GreyLight),
                            onClick = {
                                onSourcesClick(item.id)
                            })
                    }
                    if (index != state.sources.size - 1) {
                        Divider(
                            thickness = 2.dp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SourcesItemOdd(
    index: Int,
    item: Source,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(
                LocalContext.current
            )
                .data("https://picsum.photos/100/?random=${index + 1}")
                .crossfade(true)
                .build(),
            contentDescription = "Image News",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .background(GreyLight)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = item.name,
                fontSize = 16.sp,
                color = TextLight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                fontSize = 14.sp,
                color = TextDisabledLight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.category.uppercase(),
                fontSize = 12.sp,
                color = TextMutedLight,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SourcesItemEven(
    index: Int,
    item: Source,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = item.name,
                fontSize = 16.sp,
                color = TextLight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                fontSize = 14.sp,
                color = TextDisabledLight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.category.uppercase(),
                fontSize = 12.sp,
                color = TextMutedLight,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        AsyncImage(
            model = ImageRequest.Builder(
                LocalContext.current
            )
                .data("https://picsum.photos/100/?random=${index + 1}")
                .crossfade(true)
                .build(),
            contentDescription = "Image News",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .background(GreyLight)
        )
    }
}