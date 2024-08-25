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

package com.algorand.android.account.localaccount.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.repository.Algo25AccountRepository
import com.algorand.android.account.localaccount.domain.repository.LedgerBleAccountRepository
import com.algorand.android.account.localaccount.domain.repository.LedgerUsbAccountRepository
import com.algorand.android.account.localaccount.domain.repository.NoAuthAccountRepository
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccountCountFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

internal class GetLocalAccountCountFlowUseCase @Inject constructor(
    private val algo25AccountRepository: Algo25AccountRepository,
    private val ledgerBleAccountRepository: LedgerBleAccountRepository,
    private val ledgerUsbAccountRepository: LedgerUsbAccountRepository,
    private val noAuthAccountRepository: NoAuthAccountRepository
) : GetLocalAccountCountFlow {

    override fun invoke(): Flow<Int> {
        return combine(
            algo25AccountRepository.getAccountCountAsFlow(),
            ledgerBleAccountRepository.getAccountCountAsFlow(),
            ledgerUsbAccountRepository.getAccountCountAsFlow(),
            noAuthAccountRepository.getAccountCountAsFlow()
        ) { algo25Count, ledgerBleCount, ledgerUsbCount, noAuthCount ->
            algo25Count + ledgerBleCount + ledgerUsbCount + noAuthCount
        }.distinctUntilChanged()
    }
}
