package com.algorand.android.module.algosdk

const val MNEMONIC_DELIMITER_REGEX = "[, ]+"
const val MNEMONIC_SEPARATOR = " "

fun String.splitMnemonic(): List<String> {
    return this.trim().split(Regex(MNEMONIC_DELIMITER_REGEX))
}

fun List<String>.joinMnemonics(): String {
    return joinToString(MNEMONIC_SEPARATOR)
}
