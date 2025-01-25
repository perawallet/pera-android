package com.algorand.android.modules.assetinbox.send.ui.model

import android.os.Parcelable
import java.math.BigInteger
import kotlinx.parcelize.Parcelize

@Parcelize
data class Arc59SendSummaryNavArgs(
    val senderPublicKey: String,
    val receiverPublicKey: String,
    val assetId: Long,
    val assetAmount: BigInteger
) : Parcelable
