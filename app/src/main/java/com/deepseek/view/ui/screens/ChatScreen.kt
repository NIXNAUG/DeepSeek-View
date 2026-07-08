package com.deepseek.view.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.deepseek.view.BuildConfig

/**
 * DeepSeek Chat interface wrapped in a WebView.
 * This is the core "web-to-app" shell experience.
 *
 * JavaScript interface can be added here to bridge data between
 * the DeepSeek web app and native Android features.
 */
@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableIntStateOf(0) }
    var pageTitle by remember { mutableStateOf("AI 对话") }

    // Handle system back press
    BackHandler(enabled = canGoBack) {
        webView?.goBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pageTitle) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (canGoBack) {
                            webView?.goBack()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { webView?.reload() }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "刷新")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowContentAccess = true
                            databaseEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            builtInZoomControls = true
                            displayZoomControls = false
                            setSupportZoom(true)
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            cacheMode = WebSettings.LOAD_DEFAULT
                            // Enable localStorage for chat history
                            setGeolocationEnabled(false)
                            // User agent
                            userAgentString = "Mozilla/5.0 (Linux; Android 14) " +
                                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                    "Chrome/120.0.6099.144 Mobile Safari/537.36"
                        }

                        // Accept cookies — need explicit WebView reference for setAcceptThirdPartyCookies
                        val thisWebView = this
                        CookieManager.getInstance().apply {
                            setAcceptCookie(true)
                            setAcceptThirdPartyCookies(thisWebView, true)
                        }

                        // Add JavaScript interface for native bridge
                        addJavascriptInterface(
                            DeepSeekJsBridge(context),
                            "DeepSeekNative"
                        )

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: android.graphics.Bitmap?
                            ) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                canGoBack = view?.canGoBack() == true
                                view?.title?.let { title ->
                                    if (title.isNotBlank()) pageTitle = title
                                }
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val url = request?.url?.toString() ?: return false
                                // Keep all deepseek.com URLs in-app
                                return if (url.contains("deepseek.com") ||
                                    url.contains("deepseek.ai")
                                ) {
                                    false
                                } else {
                                    // External links → browser
                                    try {
                                        val intent = android.content.Intent(
                                            android.content.Intent.ACTION_VIEW,
                                            android.net.Uri.parse(url)
                                        )
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                    }
                                    true
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                title?.let { if (it.isNotBlank()) pageTitle = it }
                            }
                        }

                        loadUrl(BuildConfig.DEEPSEEK_CHAT_URL)
                        webView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Progress bar
            if (progress in 1..99) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Initial loading spinner
            if (isLoading && progress == 0) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * JavaScript → Native bridge for the DeepSeek web app.
 * Methods annotated with @JavascriptInterface are callable from JS.
 *
 * Example usage from web console / injected script:
 *   DeepSeekNative.onBalanceUpdate(JSON.stringify({balance: "100.00"}));
 *   DeepSeekNative.showToast("Hello from web!");
 */
class DeepSeekJsBridge(private val context: android.content.Context) {

    @android.webkit.JavascriptInterface
    fun showToast(message: String) {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    @android.webkit.JavascriptInterface
    fun onBalanceUpdate(jsonData: String) {
        // Bridge for future use — could update native UI from web data
        android.util.Log.d("DeepSeekBridge", "Balance update: $jsonData")
    }

    @android.webkit.JavascriptInterface
    fun onTokenUsageUpdate(jsonData: String) {
        // Bridge for token usage updates from web
        android.util.Log.d("DeepSeekBridge", "Token usage update: $jsonData")
    }

    @android.webkit.JavascriptInterface
    fun openNativeScreen(screen: String) {
        // Navigate to native screens from web
        android.util.Log.d("DeepSeekBridge", "Navigate to: $screen")
    }
}
