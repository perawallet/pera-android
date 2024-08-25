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

package com.algorand.android.deviceregistration.domain.usecase

import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.deviceid.component.domain.model.DeviceRegistration
import com.algorand.android.deviceid.component.domain.usecase.RegisterDeviceId
import com.algorand.android.deviceid.component.domain.usecase.SetSelectedNodeDeviceId
import javax.inject.Inject
import kotlinx.coroutines.delay

class RegisterDeviceIdUseCase @Inject constructor(
    private val registerDeviceId: RegisterDeviceId,
    private val setSelectedNodeDeviceId: SetSelectedNodeDeviceId,
    private val getLocalAccounts: GetLocalAccounts
) {

    suspend operator fun invoke(token: String): Result<String> {
        val accountAddresses = getLocalAccounts().map { it.address }
        return registerDeviceId(DeviceRegistration(token, accountAddresses))
            .onSuccess { deviceId ->
                setSelectedNodeDeviceId(deviceId)
                Result.success(deviceId)
            }
            .onFailure {
                delay(REGISTER_DEVICE_FAIL_DELAY)
                invoke(token)
            }

    }

    private companion object {
        const val REGISTER_DEVICE_FAIL_DELAY = 1500L
    }
}
