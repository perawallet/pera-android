package com.algorand.android.modules.assetinbox.send.summary.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

@Parcelize
data class Arc59SendSummaryNavArgs(
    val senderPublicKey: String,
    val receiverPublicKey: String,
    val assetId: Long,
    val assetAmount: BigInteger
) : Parcelable
