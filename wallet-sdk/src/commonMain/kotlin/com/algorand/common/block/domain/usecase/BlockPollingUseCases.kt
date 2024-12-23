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

package com.algorand.common.block.domain.usecase

import com.algorand.common.foundation.PeraResult

fun interface ClearLastKnownBlockNumber {
    suspend operator fun invoke()
}

fun interface GetLastKnownBlockNumber {
    suspend operator fun invoke()
}

fun interface UpdateLastKnownBlockNumber {
    suspend operator fun invoke()
}

fun interface ShouldUpdateAccountCache {
    suspend operator fun invoke(): PeraResult<Boolean>
}
