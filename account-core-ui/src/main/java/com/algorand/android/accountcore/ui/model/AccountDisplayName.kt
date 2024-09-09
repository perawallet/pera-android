package com.algorand.android.accountcore.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountDisplayName(
    val accountAddress: String,
    val primaryDisplayName: String,
    val secondaryDisplayName: String?
) : Parcelable
