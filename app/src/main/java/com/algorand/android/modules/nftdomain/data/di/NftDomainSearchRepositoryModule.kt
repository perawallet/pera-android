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

package com.algorand.android.modules.nftdomain.data.di

import com.algorand.android.modules.nftdomain.data.mapper.NftDomainSearchResultDTOMapper
import com.algorand.android.modules.nftdomain.data.repository.NftDomainSearchRepositoryImpl
import com.algorand.android.modules.nftdomain.domain.repository.NftDomainSearchRepository
import com.algorand.android.network.MobileAlgorandApi
import com.hipo.hipoexceptionsandroid.RetrofitErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object NftDomainSearchRepositoryModule {

    @Named(NftDomainSearchRepository.INJECTION_NAME)
    @Provides
    fun provideNftDomainSearchRepository(
        mobileAlgorandApi: MobileAlgorandApi,
        hipoErrorHandler: RetrofitErrorHandler,
        nftDomainSearchResultDTOMapper: NftDomainSearchResultDTOMapper
    ): NftDomainSearchRepository {
        return NftDomainSearchRepositoryImpl(mobileAlgorandApi, hipoErrorHandler, nftDomainSearchResultDTOMapper)
    }
}
