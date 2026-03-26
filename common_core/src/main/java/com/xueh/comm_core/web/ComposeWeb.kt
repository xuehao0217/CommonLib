package com.xueh.comm_core.web

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView as AndroidSystemWebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.kevinnzou.web.AccompanistWebViewClient
import com.kevinnzou.web.LoadingState
import com.kevinnzou.web.WebView
import com.kevinnzou.web.rememberWebViewNavigator
import com.kevinnzou.web.rememberWebViewState

@Composable
fun ComposeWebView(url: String) {
    val state = rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()
    Column(Modifier.fillMaxSize()) {
        val loadingState = state.loadingState
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                progress = { loadingState.progress },
                modifier = Modifier.fillMaxWidth()
            )
        }

        val webClient = remember {
            object : AccompanistWebViewClient() {
                override fun onPageStarted(
                    view: AndroidSystemWebView,
                    url: String?,
                    favicon: Bitmap?,
                ) {
                    super.onPageStarted(view, url, favicon)
                    Log.d("ComposeWebView", "Page started loading for $url")
                }
            }
        }

        WebView(
            state = state,
            modifier = Modifier
                .weight(1f),
            navigator = navigator,
            onCreated = { webView ->
                webView.settings.javaScriptEnabled = true
            },
            client = webClient
        )
    }
}