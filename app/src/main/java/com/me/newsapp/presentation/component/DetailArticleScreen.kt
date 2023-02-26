package com.me.newsapp.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.me.newsapp.domain.model.Article
import com.me.newsapp.ui.theme.GreyLight
import com.me.newsapp.ui.theme.TextDisabledLight
import com.me.newsapp.ui.theme.TextLight
import com.me.newsapp.ui.theme.TextMutedLight

@Composable
fun DetailArticleScreen(
    article: Article,
    modifier: Modifier = Modifier,
    onReadMoreClick: (url: String) -> Unit,
) {
    Card(
        modifier = modifier,
        backgroundColor = GreyLight,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = article.title,
                fontSize = 18.sp,
                color = TextLight,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${article.publishedAt} by ",
                fontSize = 12.sp,
                color = TextMutedLight,
            )
            Text(
                text = article.author,
                fontSize = 12.sp,
                color = TextLight,
            )
            Spacer(modifier = Modifier.height(16.dp))
            BoxWithConstraints {
                val aspectRatio = 3f / 1.5f
                AsyncImage(
                    model = ImageRequest.Builder(
                        LocalContext.current
                    )
                        .data(article.urlToImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Image Article",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = article.description,
                fontSize = 14.sp,
                color = TextDisabledLight,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = article.source.name.uppercase(),
                fontSize = 12.sp,
                color = TextLight,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                shape = RoundedCornerShape(16.dp),
                onClick = { onReadMoreClick(article.url) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Read More",
                    fontSize = 14.sp,
                    color = GreyLight,
                )
            }

        }
    }
}