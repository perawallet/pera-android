package com.algorand.android.module.custominfo.domain.usecase

fun interface DeleteCustomInfo {
    suspend operator fun invoke(address: String)
}
