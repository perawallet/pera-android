package com.algorand.android.custominfo.component.domain.usecase

fun interface DeleteCustomInfo {
    suspend operator fun invoke(address: String)
}
