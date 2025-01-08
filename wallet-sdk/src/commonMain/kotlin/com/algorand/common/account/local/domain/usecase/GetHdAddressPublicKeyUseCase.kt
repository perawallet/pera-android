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
 *
 *
 */

package com.algorand.common.account.local.domain.usecase

import com.algorand.common.account.local.domain.repository.HdKeyAccountRepository

internal class GetHdAddressPublicKeyUseCase(
    private val hdKeyAccountRepository: HdKeyAccountRepository
) : GetSecretKey {

    override suspend fun invoke(address: String): ByteArray? {

        val account = hdKeyAccountRepository.getAccount(address)
//        account?.let {
//            return keyGen(
//                KeyContext.Address,
//                account.account,
//                account.change,
//                account.keyIndex,
//                account.derivationType
//            )
//        }
        return null
    }
}
