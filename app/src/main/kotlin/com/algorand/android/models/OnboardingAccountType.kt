package com.algorand.android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface OnboardingAccountType : Parcelable {

    val wordCount: Int

    @Suppress("MagicNumber")
    @Parcelize
    data object Algo25 : OnboardingAccountType {
        override val wordCount: Int = 25
    }

    @Suppress("MagicNumber")
    @Parcelize
    data object HdKey : OnboardingAccountType {
        override val wordCount: Int = 24
    }
}
