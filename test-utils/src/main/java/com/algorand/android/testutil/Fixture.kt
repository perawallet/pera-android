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

package com.algorand.android.testutil

import com.appmattus.kotlinfixture.kotlinFixture

class PeraFixture(private val fixtureConfiguration: PeraConfiguration) {

    val fixture = kotlinFixture {
        repeatCount { fixtureConfiguration.listSize }
    }

    inline operator fun <reified T : Any?> invoke(): T {
        return fixture()
    }
}

class PeraConfigurationBuilder(configuration: PeraConfiguration = PeraConfiguration()) {
    var listSize: Int = configuration.listSize

    fun build() = PeraConfiguration(
        listSize = listSize
    )
}

class PeraConfiguration(val listSize: Int = 1)

inline fun peraFixture(configuration: PeraConfigurationBuilder.() -> Unit = {}) =
    PeraFixture(PeraConfigurationBuilder().apply(configuration).build())

inline fun <reified T : Any?> fixtureOf(
    configuration: PeraConfigurationBuilder.() -> Unit = {}
): T = peraFixture(configuration).invoke()
