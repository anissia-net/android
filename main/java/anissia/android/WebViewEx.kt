package anissia.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity

@SuppressLint("SetJavaScriptEnabled")
class WebViewEx : WebView {
    val basicUrl = "https://anissia.net/"
    //val basicUrl = "http://192.168.1.2:5173/"
    var schedule = "${basicUrl}schedule"

    var versionName: String = ""
    var firstLoad: Boolean = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> { textZoom(10, true); true }
            KeyEvent.KEYCODE_VOLUME_DOWN -> { textZoom(-10, true); true }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun textZoom(by: Int, apply: Boolean): Int {
        val pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        var now = pref.getInt("textZoom", 100)
        if (by != 0) {
            now = (now + by).coerceAtLeast(80).coerceAtMost(120)
            pref.edit().putInt("textZoom", now).apply()
        }
        if (apply) {
            settings.textZoom = now
            CustomToast.showToast(context, "$now%")
        }
        return now
    }

    private fun init() {
        versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName

        // 기본 정책 설정
        settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            allowFileAccess = true
            domStorageEnabled = true
            textZoom = textZoom(0, false)
        }

        webChromeClient = object: WebChromeClient() {}

        // WebViewClient 설정
        setWebViewClient(object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (firstLoad) {
                    evaluateJavascript("localStorage.setItem('app', 'android');", null)
                    evaluateJavascript("localStorage.setItem('appVersion', '$versionName');", null)
                    requestFocus()
                    firstLoad = false
                }
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return request?.url?.let { handleUrl(it) } ?: true
            }

            private fun handleUrl(url: Uri): Boolean {
                val urls = url.toString()
                // 애니시아 주소가 아니거나 애니편성표의 경우 외부 실행
                if (urls.startsWith(basicUrl) && !urls.startsWith(schedule)) {
                    return false
                }
                return openExternalBrowser(url)
            }

            private fun openExternalBrowser(url: Uri): Boolean {
                // 애니편성표 2015의 경우 전용앱이 있다면 앱을 실행시킨다.
                if (url.toString().startsWith("${schedule}/2015")) {
                    val intent = context.packageManager.getLaunchIntentForPackage("anissia.android.schedule")
                    if (intent != null) {
                        startActivity(context, intent, null)
                        return true
                    }
                }

                val intent = Intent(Intent.ACTION_VIEW, url)
                startActivity(context, intent, null)
                return true
            }
        })
    }
}