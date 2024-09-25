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

package com.algorand.android.module.swap.component.data

import java.math.BigDecimal

val SWAP_FEE_PADDING: BigDecimal = BigDecimal.valueOf(0.665)

internal object SwapUtils {

    val DEFAULT_PERA_SWAP_FEE: BigDecimal = BigDecimal.ZERO
    val DEFAULT_EXCHANGE_SWAP_FEE: BigDecimal = BigDecimal.ZERO

    const val PERA_FEE_WALLET_ADDRESS = "V73GWLED56UUKKGOESJYHQADILUFMDM4RIBZZOLOOR4RKONZFDXYTVPMRM"
}
