package com.algorand.android.encryption

import android.util.Base64
import javax.inject.Inject

internal class Base64ManagerImpl @Inject constructor() : Base64Manager {

    override fun encode(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun decode(value: String): ByteArray {
        return Base64.decode(value, Base64.DEFAULT)
    }
}
