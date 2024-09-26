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

package com.algorand.android.module.deviceid.domain.usecase.implementation

import com.algorand.android.module.deviceid.domain.usecase.SetMainnetDeviceId
import com.algorand.android.module.deviceid.domain.usecase.SetSelectedNodeDeviceId
import com.algorand.android.module.deviceid.domain.usecase.SetTestnetDeviceId
import com.algorand.android.node.domain.Node
import com.algorand.android.node.domain.usecase.GetActiveNode
import javax.inject.Inject

internal class SetSelectedNodeDeviceIdUseCase @Inject constructor(
    private val getActiveNode: GetActiveNode,
    private val setMainnetDeviceId: SetMainnetDeviceId,
    private val setTestnetDeviceId: SetTestnetDeviceId
) : SetSelectedNodeDeviceId {

    override fun invoke(deviceId: String?) {
        when (getActiveNode()) {
            Node.Mainnet -> setMainnetDeviceId(deviceId)
            Node.Testnet -> setTestnetDeviceId(deviceId)
        }
    }
}
