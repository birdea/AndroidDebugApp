package com.risewide.bdebugapp.jsp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.risewide.bdebugapp.BaseActivity
import com.risewide.bdebugapp.R

class WebViewTestActivity : BaseActivity() {

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_webview)

        context = this

        webview = findViewById(R.id.webview)
        webview.settings.javaScriptEnabled = true
        webview.addJavascriptInterface(WebBridge(), "android")
        webview.loadUrl("file:///android_asset/javapage.html")

        textview = findViewById(R.id.textview)

        var editText = findViewById<EditText>(R.id.edittext)

        var button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            webview.loadUrl("javascript:setMessage('" + editText.getText() + "')")
        }
    }

    class WebBridge {
        @JavascriptInterface
        fun sucessLogin(result:String) {
            Toast.makeText(context, "result: $result", Toast.LENGTH_SHORT).show()
        }

        /** Show a toast from the web page  */
        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun setMessage(arg: String) {
            handler.post(Runnable { textview.setText("받은 메시지: \n$arg") })
        }
    }

    companion object {
        lateinit var textview : TextView
        var context : Context? = null
        var handler : Handler = Handler()
        lateinit var webview : WebView
    }
}
