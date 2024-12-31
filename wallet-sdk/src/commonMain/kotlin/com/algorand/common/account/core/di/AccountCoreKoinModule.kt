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

package com.algorand.common.account.core.di

import com.algorand.common.account.core.domain.usecase.AddAlgo25Account
import com.algorand.common.account.core.domain.usecase.AddAlgo25AccountUseCase
import com.algorand.common.account.core.domain.usecase.AddLedgerBleAccount
import com.algorand.common.account.core.domain.usecase.AddLedgerBleAccountUseCase
import com.algorand.common.account.core.domain.usecase.AddNoAuthAccount
import com.algorand.common.account.core.domain.usecase.AddNoAuthAccountUseCase
import org.koin.dsl.module

internal val accountCoreKoinModule = module {
    factory<AddAlgo25Account> { AddAlgo25AccountUseCase(get(), get()) }
    factory<AddLedgerBleAccount> { AddLedgerBleAccountUseCase(get(), get()) }
    factory<AddNoAuthAccount> { AddNoAuthAccountUseCase(get(), get()) }
}
