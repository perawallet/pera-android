package com.algorand.android.core.utils

private const val SHORTENED_ADDRESS_LETTER_COUNT = 6

fun String.toShortenedAddress(): String {
    if (length < SHORTENED_ADDRESS_LETTER_COUNT) return ""
    return "${take(SHORTENED_ADDRESS_LETTER_COUNT)}...${takeLast(SHORTENED_ADDRESS_LETTER_COUNT)}"
}
