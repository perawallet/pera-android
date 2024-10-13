package com.algorand.android.modules.perawebview

fun interface GetAuthorizedAddressesWebMessage {
    suspend operator fun invoke(): String
}
