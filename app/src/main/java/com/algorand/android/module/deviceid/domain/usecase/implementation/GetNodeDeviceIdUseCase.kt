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

import com.algorand.android.module.deviceid.domain.usecase.GetMainnetDeviceId
import com.algorand.android.module.deviceid.domain.usecase.GetNodeDeviceId
import com.algorand.android.module.deviceid.domain.usecase.GetTestnetDeviceId
import com.algorand.android.node.domain.Node
import javax.inject.Inject

internal class GetNodeDeviceIdUseCase @Inject constructor(
    private val getMainnetDeviceId: GetMainnetDeviceId,
    private val getTestnetDeviceId: GetTestnetDeviceId
) : GetNodeDeviceId {

    override fun invoke(node: Node): String? {
        return when (node) {
            Node.Mainnet -> getMainnetDeviceId()
            Node.Testnet -> getTestnetDeviceId()
        }
    }
}