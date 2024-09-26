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

package com.algorand.android.module.algosdk.network

import com.algorand.android.module.algosdk.transaction.model.AlgodInterceptorNodeDetails
import com.algorand.android.node.domain.Node
import com.algorand.android.node.domain.usecase.GetActiveNode
import javax.inject.Inject

internal class GetAlgodInterceptorNodeDetailsImpl @Inject constructor(
    private val getActiveNode: GetActiveNode,
    private val provideAlgorandApiKey: ProvideAlgorandApiKey
) : GetAlgodInterceptorNodeDetails {

    override fun invoke(): AlgodInterceptorNodeDetails {
        return when (getActiveNode()) {
            Node.Mainnet -> getMainnetNodeDetails()
            Node.Testnet -> getTestnetNodeDetails()
        }
    }

    private fun getMainnetNodeDetails(): AlgodInterceptorNodeDetails {
        return AlgodInterceptorNodeDetails(
            apiKey = provideAlgorandApiKey(),
            baseUrl = "https://node-mainnet.chain.perawallet.app/"
        )
    }

    private fun getTestnetNodeDetails(): AlgodInterceptorNodeDetails {
        return AlgodInterceptorNodeDetails(
            apiKey = provideAlgorandApiKey(),
            baseUrl = "https://node-testnet.chain.perawallet.app/"
        )
    }
}
