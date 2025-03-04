package com.algorand.android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface OnboardingAccountType : Parcelable {

    @Parcelize
    data object Algo25 : OnboardingAccountType

    @Parcelize
    data object HdKey : OnboardingAccountType

    companion object {
        @Suppress("MagicNumber")
        fun OnboardingAccountType.wordCount(): Int {
            return when (this) {
                is Algo25 -> 25
                is HdKey -> 24
            }
        }
    }
}
