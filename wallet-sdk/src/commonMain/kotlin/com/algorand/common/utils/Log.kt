package com.algorand.common.utils

import io.github.aakira.napier.Napier

class Log {
    companion object {
        fun d(
            tag: String,
            message: String,
        ) {
            Napier.d(message, tag = tag)
        }

        fun i(
            tag: String,
            message: String,
        ) {
            Napier.i(message, tag = tag)
        }

        fun w(
            tag: String,
            message: String,
        ) {
            Napier.w(message, tag = tag)
        }

        fun e(
            tag: String,
            message: String,
        ) {
            Napier.e(message, tag = tag)
        }
    }
}