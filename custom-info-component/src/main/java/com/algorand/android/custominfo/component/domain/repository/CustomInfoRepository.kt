package com.algorand.android.custominfo.component.domain.repository

import com.algorand.android.custominfo.component.domain.model.CustomInfo

internal interface CustomInfoRepository {

    suspend fun getCustomInfo(address: String): CustomInfo

    suspend fun getCustomInfoOrNull(address: String): CustomInfo?

    suspend fun setCustomInfo(customInfo: CustomInfo)

    suspend fun setCustomName(address: String, name: String)

    suspend fun deleteCustomInfo(address: String)
}
