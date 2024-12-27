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

package com.algorand.common.account.custom.di

import com.algorand.common.account.custom.data.database.dao.CustomInfoDao
import com.algorand.common.account.custom.data.mapper.entity.CustomInfoEntityMapper
import com.algorand.common.account.custom.data.mapper.entity.CustomInfoEntityMapperImpl
import com.algorand.common.account.custom.data.mapper.model.CustomInfoMapper
import com.algorand.common.account.custom.data.mapper.model.CustomInfoMapperImpl
import com.algorand.common.account.custom.data.repository.CustomInfoRepositoryImpl
import com.algorand.common.account.custom.domain.repository.CustomInfoRepository
import com.algorand.common.account.custom.domain.usecase.DeleteAccountCustomInfo
import com.algorand.common.account.custom.domain.usecase.GetAccountCustomInfo
import com.algorand.common.account.custom.domain.usecase.GetAccountCustomInfoOrNull
import com.algorand.common.account.custom.domain.usecase.SetAccountCustomInfo
import com.algorand.common.account.custom.domain.usecase.SetAccountCustomName
import com.algorand.common.account.custom.domain.usecase.SetAccountOrderIndex
import com.algorand.common.foundation.database.PeraDatabase
import org.koin.dsl.module

internal val customInfoKoinModule = module {

    single<CustomInfoDao> {
        get<PeraDatabase>().customInfoDao()
    }

    factory<CustomInfoEntityMapper> { CustomInfoEntityMapperImpl(get()) }
    factory<CustomInfoMapper> { CustomInfoMapperImpl() }
    single<CustomInfoRepository> { CustomInfoRepositoryImpl(get(), get(), get(), get()) }

    factory<SetAccountCustomName> {
        SetAccountCustomName { address, name ->
            get<CustomInfoRepository>().setCustomName(address, name)
        }
    }

    factory<SetAccountCustomInfo> {
        SetAccountCustomInfo { customInfo ->
            get<CustomInfoRepository>().setCustomInfo(customInfo)
        }
    }

    factory<GetAccountCustomInfoOrNull> {
        GetAccountCustomInfoOrNull { address ->
            get<CustomInfoRepository>().getCustomInfoOrNull(address)
        }
    }

    factory<GetAccountCustomInfo> {
        GetAccountCustomInfo { address ->
            get<CustomInfoRepository>().getCustomInfo(address)
        }
    }

    factory<DeleteAccountCustomInfo> {
        DeleteAccountCustomInfo { address ->
            get<CustomInfoRepository>().deleteCustomInfo(address)
        }
    }

    factory<SetAccountOrderIndex> {
        SetAccountOrderIndex { address, orderIndex ->
            get<CustomInfoRepository>().setOrderIndex(address, orderIndex)
        }
    }
}
