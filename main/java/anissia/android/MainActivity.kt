package anissia.android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : ComponentActivity() {
    private val webview: WebViewEx by lazy {
        findViewById<WebViewEx>(R.id.webview)
    }

    private val swipeRefreshLayout: SwipeRefreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview)

        webview.loadUrl(webview.basicUrl)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webview.canGoBack()) {
                    webview.goBack()
                } else {
                    finish()
                }
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            webview.reload()
            swipeRefreshLayout.isRefreshing = false
        }
    }
}
