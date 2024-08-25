/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.foundation.common

import android.util.Base64
import java.net.URLEncoder

fun String.decodeBase64(): ByteArray? {
    return try {
        Base64.decode(this, Base64.DEFAULT)
    } catch (exception: Exception) {
        // TODO Log firebase
        null
    }
}

fun String.encodeToURL(charset: String = Charsets.UTF_8.name()): String {
    return URLEncoder.encode(this, charset)
}

fun String.addHashtagToStart(): String {
    return "#$this"
}
