package com.me.newsapp.presentation.web_article

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun WebArticleScreen(url: String) {
    var isLoading by remember { mutableStateOf(false) }

    WebViewWithLoading(
        url = url,
        onLoading = { isLoading = it }
    )
}

@Composable
fun WebViewWithLoading(url: String, onLoading: (Boolean) -> Unit) {
    var isLoading by remember { mutableStateOf(true) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isLoading = false
                        onLoading(false)
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        isLoading = true
                        onLoading(true)
                    }
                }
            }
        },
        update = { view ->
            view.loadUrl(url)
        }
    )

    if (isLoading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
}

