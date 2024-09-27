package com.algorand.android.module.account.core.ui.model

import androidx.annotation.*
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier
import com.algorand.android.R

enum class VerificationTierConfiguration(
    @DrawableRes val drawableResId: Int?,
    @ColorRes val textColorResId: Int
) {
    VERIFIED(
        drawableResId = R.drawable.ic_asa_verified,
        textColorResId = R.color.text_main
    ) {
        override fun toVerificationTier(): VerificationTier = VerificationTier.VERIFIED
    },
    UNVERIFIED(
        drawableResId = null,
        textColorResId = R.color.text_main
    ) {
        override fun toVerificationTier(): VerificationTier = VerificationTier.UNVERIFIED
    },
    TRUSTED(
        drawableResId = R.drawable.ic_asa_trusted,
        textColorResId = R.color.text_main
    ) {
        override fun toVerificationTier(): VerificationTier = VerificationTier.TRUSTED
    },
    SUSPICIOUS(
        drawableResId = R.drawable.ic_asa_danger,
        textColorResId = R.color.negative
    ) {
        override fun toVerificationTier(): VerificationTier = VerificationTier.SUSPICIOUS
    };

    abstract fun toVerificationTier(): VerificationTier

    companion object {
        val DEFAULT_TEXT_COLOR_RES_ID = R.color.text_main
    }
}
