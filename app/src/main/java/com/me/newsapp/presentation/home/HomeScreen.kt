package com.me.newsapp.presentation.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.me.newsapp.ui.theme.NewsAppTheme

@Composable
fun HomeScreen(
    onCategoryClick: (category: String) -> Unit,
    viewModel: HomeViewModel,
) {
    val state = viewModel.state

    val mContext = LocalContext.current
    LaunchedEffect(key1 = mContext) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeEvent.ShowToast -> {
                    Toast.makeText(mContext, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    NewsAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(items = state.categories) { index, item ->
                    CategoryItem(index = index, category = item, OnClick = {
                        onCategoryClick(item)
                    })
                    Divider(
                        thickness = 4.dp,
                        color = Color.Black
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun CategoryItem(
    category: String,
    OnClick: () -> Unit,
    index: Int,
) {
    val aspectRatio = 3f / 2f
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { OnClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio), contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(
                    LocalContext.current
                )
                    .data("https://picsum.photos/300/200/?blur=2?random=${index + 1}")
                    .crossfade(true)
                    .build(),
                contentDescription = "Image News",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
            )
            Divider(modifier = Modifier.matchParentSize())
            Text(
                text = category.uppercase(),
                style = TextStyle.Default.copy(
                    drawStyle = Stroke(
                        miter = 10f,
                        width = 5f,
                        join = StrokeJoin.Round
                    )
                ),
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

        }
    }
}