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

package com.algorand.android.deviceregistration.domain.usecase

import com.algorand.android.deviceregistration.domain.mapper.DeviceUpdateDTOMapper
import com.algorand.android.deviceregistration.domain.model.DeviceUpdateDTO
import com.algorand.android.deviceregistration.domain.repository.UserDeviceIdRepository
import com.algorand.android.models.Result
import com.algorand.android.utils.DataResource
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdatePushTokenUseCase @Inject constructor(
    @Named(UserDeviceIdRepository.USER_DEVICE_ID_REPOSITORY_INJECTION_NAME)
    private val userDeviceIdRepository: UserDeviceIdRepository,
    private val deviceUpdateDTOMapper: DeviceUpdateDTOMapper,
    getLocalAccounts: GetLocalAccounts
) : BaseDeviceIdOperationUseCase(getLocalAccounts) {

    fun updatePushToken(deviceId: String, token: String?): Flow<DataResource<String>> = flow {
        val deviceUpdateDTO = getDeviceUpdateDTO(deviceId, token)
        userDeviceIdRepository.updateDeviceId(deviceUpdateDTO).collect {
            when (it) {
                is Result.Success -> {
                    emit(DataResource.Success(it.data))
                }
                is Result.Error -> {
                    emit(DataResource.Error.Api<String>(it.exception, it.code))
                }
            }
        }
    }

    private suspend fun getDeviceUpdateDTO(deviceId: String, token: String?): DeviceUpdateDTO {
        return deviceUpdateDTOMapper.mapToDeviceUpdateDTO(
            deviceId = deviceId,
            token = token,
            accountPublicKeyList = getAccountPublicKeys(),
            application = getApplicationName(),
            platform = PLATFORM_NAME,
            locale = getLocaleLanguageCode()
        )
    }
}
