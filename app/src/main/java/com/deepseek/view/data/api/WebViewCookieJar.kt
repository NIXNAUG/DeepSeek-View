package com.deepseek.view.data.api

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * CookieJar that bridges OkHttp with Android's WebView CookieManager.
 * This allows API calls (Retrofit/OkHttp) to share cookies with WebView sessions,
 * so logging into platform.deepseek.com in WebView also authenticates API calls.
 */
class WebViewCookieJar : CookieJar {

    private val cookieManager = CookieManager.getInstance()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies) {
            val cookieString = "${cookie.name}=${cookie.value}; Domain=${cookie.domain}; " +
                    "Path=${cookie.path}; ${if (cookie.secure) "Secure;" else ""} " +
                    "${if (cookie.httpOnly) "HttpOnly;" else ""} " +
                    "Max-Age=${cookie.persistent}"
            cookieManager.setCookie(url.toString(), cookieString)
        }
        cookieManager.flush()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieString = cookieManager.getCookie(url.toString()) ?: return emptyList()
        val cookies = mutableListOf<Cookie>()
        for (part in cookieString.split(";")) {
            val trimmed = part.trim()
            val eqIdx = trimmed.indexOf('=')
            if (eqIdx > 0) {
                val name = trimmed.substring(0, eqIdx).trim()
                val value = trimmed.substring(eqIdx + 1).trim()
                cookies.add(
                    Cookie.Builder()
                        .name(name)
                        .value(value)
                        .domain(url.host)
                        .build()
                )
            }
        }
        return cookies
    }
}