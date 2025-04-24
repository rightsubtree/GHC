package com.wang.ghc.util

import android.util.Base64

object Base64Decoder {
    fun decode(encodedContent: String): String {
        return try {
            String(Base64.decode(encodedContent, Base64.DEFAULT))
        } catch (e: Exception) {
            "解码错误：${e.message}"
        }
    }
}