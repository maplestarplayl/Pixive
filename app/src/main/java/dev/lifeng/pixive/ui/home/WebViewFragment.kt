package dev.lifeng.pixive.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import dev.lifeng.pixive.R

class WebViewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_web_view, container, false)
        // 初始化 WebView
        val webView = view.findViewById<WebView>(R.id.web_vieww)
        // 设置 WebViewClient 以便点击链接时在 WebView 内部处理
        webView.webViewClient = WebViewClient()
        val url = arguments?.getString("url")
        url?.let {
            webView.loadUrl(url)
            return view
        }
        // 加载一个 URL
        webView.loadUrl("https://www.pixivision.net/zh/a/10027")

        return view
    }
}