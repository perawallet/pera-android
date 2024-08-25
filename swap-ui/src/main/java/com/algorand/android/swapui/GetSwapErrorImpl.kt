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

package com.algorand.android.swapui

import android.content.Context
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.designsystem.getXmlStyledString
import com.algorand.android.swapui.assetswap.model.SwapError

internal class GetSwapErrorImpl(
    private val context: Context
) : GetSwapError {

    override fun invoke(errorResId: Int, title: Int?): SwapError {
        return SwapError(
            description = context.getString(errorResId),
            title = title?.let { context.getString(it) }
        )
    }

    override fun invoke(description: AnnotatedString, title: AnnotatedString?): SwapError {
        return SwapError(
            description = context.getXmlStyledString(description).toString(),
            title = title?.let { context.getXmlStyledString(it).toString() }
        )
    }

    override fun invoke(message: String): SwapError {
        return SwapError(
            title = null,
            description = message
        )
    }
}
