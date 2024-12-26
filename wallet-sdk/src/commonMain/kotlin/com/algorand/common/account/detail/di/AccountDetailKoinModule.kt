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

package com.algorand.common.account.detail.di

import com.algorand.common.account.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.common.account.detail.domain.usecase.GetAccountRegistrationTypeUseCase
import com.algorand.common.account.detail.domain.usecase.GetAccountState
import com.algorand.common.account.detail.domain.usecase.GetAccountStateUseCase
import com.algorand.common.account.detail.domain.usecase.GetAccountType
import com.algorand.common.account.detail.domain.usecase.GetAccountTypeUseCase
import org.koin.dsl.module

internal val accountDetailKoinModule = module {
    factory<GetAccountType> { GetAccountTypeUseCase(get(), get()) }
    factory<GetAccountRegistrationType> { GetAccountRegistrationTypeUseCase(get()) }
    factory<GetAccountState> { GetAccountStateUseCase(get(), get()) }
}
