package com.algorand.android.modules.perawebview

fun interface ParseOpenSystemBrowserUrl {
    operator fun invoke(jsonPayload: String): String?
}
