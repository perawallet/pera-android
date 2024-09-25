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

package com.algorand.android.notification.domain.usecase

import com.algorand.android.deviceid.component.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.android.foundation.PeraResult
import com.algorand.android.notification.domain.repository.NotificationRepository
import javax.inject.Inject

internal class SetNotificationFilterUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId
) : SetNotificationFilter {
    override suspend fun invoke(address: String, isFiltered: Boolean): PeraResult<Unit> {
        return notificationRepository.setNotificationFilter(address, isFiltered, getSelectedNodeDeviceId().orEmpty())
    }
}