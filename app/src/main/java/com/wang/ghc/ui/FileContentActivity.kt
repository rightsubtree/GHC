package com.wang.ghc.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.wang.ghc.R
import com.wang.ghc.util.Base64Decoder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FileContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_content)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val content = intent.getStringExtra("CONTENT") ?: return
        val decodedContent = Base64Decoder.decode(content)

        //Log.d("FileContentActivity", "解码结果：$decodedContent")

        val webView = findViewById<WebView>(R.id.webViewContent)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true

        val formattedContent = "<pre>${decodedContent}</pre>"
        webView.loadDataWithBaseURL("", formattedContent, "text/html", "UTF-8", null)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}