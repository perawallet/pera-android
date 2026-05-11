/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.backup.domain.model

data class Argon2idConfig(
    val timeCost: Int,
    val memoryCost: Int,
    val parallelism: Int,
    val outputLength: Int
) {
    companion object {
        val DEFAULT = Argon2idConfig(
            timeCost = 4,
            memoryCost = 16384,
            parallelism = 4,
            outputLength = 32
        )
    }
}
