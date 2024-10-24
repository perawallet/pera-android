/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.testing

import com.appmattus.kotlinfixture.kotlinFixture

class PeraFixture(private val listItemSize: Int) {

    val fixture = kotlinFixture {
        repeatCount { listItemSize }
    }

    inline operator fun <reified T : Any?> invoke(): T {
        return fixture()
    }
}

actual inline fun <reified T : Any?> peraFixture(listItemSize: Int): T = PeraFixture(listItemSize).fixture()
