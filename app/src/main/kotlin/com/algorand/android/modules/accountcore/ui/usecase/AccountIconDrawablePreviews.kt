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

package com.algorand.android.modules.accountcore.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.AccountIconResource
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview

/**
 * Never use this class directly. It must be used via use cases
 */
internal object AccountIconDrawablePreviews {

    fun getAlgo25Drawable(): AccountIconDrawablePreview {
        return AccountIconDrawablePreview(
            backgroundColorResId = AccountIconResource.STANDARD.backgroundColorResId,
            iconTintResId = AccountIconResource.STANDARD.iconTintResId,
            iconResId = AccountIconResource.STANDARD.iconResId
        )
    }

    fun getLedgerBleDrawable(): AccountIconDrawablePreview {
        return AccountIconDrawablePreview(
            backgroundColorResId = AccountIconResource.LEDGER.backgroundColorResId,
            iconTintResId = AccountIconResource.LEDGER.iconTintResId,
            iconResId = AccountIconResource.LEDGER.iconResId
        )
    }

    fun getNoAuthDrawable(): AccountIconDrawablePreview {
        return AccountIconDrawablePreview(
            backgroundColorResId = AccountIconResource.WATCH.backgroundColorResId,
            iconTintResId = AccountIconResource.WATCH.iconTintResId,
            iconResId = AccountIconResource.WATCH.iconResId
        )
    }

    fun getHdKeyDrawable(): AccountIconDrawablePreview {
        return AccountIconDrawablePreview(
            backgroundColorResId = AccountIconResource.HD.backgroundColorResId,
            iconTintResId = AccountIconResource.HD.iconTintResId,
            iconResId = AccountIconResource.HD.iconResId
        )
    }

    fun getRekeyedDrawable(): AccountIconDrawablePreview {
        return AccountIconDrawablePreview(
            backgroundColorResId = R.color.negative_lighter,
            iconTintResId = R.color.negative,
            iconResId = R.drawable.ic_rekey_shield
        )
    }

    fun getDefaultIconDrawablePreview(): AccountIconDrawablePreview {
        return AccountIconDrawablePreview(
            backgroundColorResId = R.color.layer_gray_lighter,
            iconTintResId = R.color.text_gray,
            iconResId = R.drawable.ic_wallet
        )
    }
}
