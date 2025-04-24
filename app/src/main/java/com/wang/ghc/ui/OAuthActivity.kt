package com.wang.ghc.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.wang.ghc.network.NetworkModule
import com.wang.ghc.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OAuthActivity : AppCompatActivity() {
    @Inject
    lateinit var githubConfig: NetworkModule.GitHubConfig

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)

        webView = findViewById(R.id.authWebView)
        setupWebView()
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // 这个会打印多次，只有最后一个是以配置的redirectUri开头的，即成功时的返回值
                Log.w("OAuthActivity", "shouldOverrideUrlLoading url=$url")
                if (url.startsWith(githubConfig.redirectUri)) {
                    handleOAuthCallback(url)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
        webView.loadUrl(
            "https://github.com/login/oauth/authorize?client_id=${githubConfig.clientId}&redirect_uri=${githubConfig.redirectUri}")
    }

    private fun handleOAuthCallback(url: String) {
        val code = url.substringAfter("code=").substringBefore("&")
        // 按照实际测试，即使是登录同一个账号，每次的code也不同
        Log.w("OAuthActivity", "handleOAuthCallback code=$code")
        val resultIntent = Intent().apply {
            putExtra("AUTH_CODE", code)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}