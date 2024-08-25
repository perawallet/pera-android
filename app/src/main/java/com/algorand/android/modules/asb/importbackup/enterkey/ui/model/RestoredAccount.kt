package com.algorand.android.modules.asb.importbackup.enterkey.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RestoredAccount(
    val address: String,
    val name: String,
    val secretKey: ByteArray
) : Parcelable
