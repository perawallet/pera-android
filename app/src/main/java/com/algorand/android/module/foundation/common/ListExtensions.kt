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

package com.algorand.android.module.foundation.common

fun List<*>.toCsvString(): String {
    return joinToString(",")
}

fun <T, R> List<T?>.mapToNotNullableListOrNull(transform: (T?) -> R?): List<R>? {
    val safeList = mutableListOf<R>()
    forEach {
        val mappedData = transform(it)
        if (mappedData == null) {
            return null
        } else {
            safeList.add(mappedData)
        }
    }
    return safeList
}

