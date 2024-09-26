package com.algorand.android.module.nameservice.domain.model

data class NameService(
    val accountAddress: String,
    val nameServiceName: String?,
    val nameServiceSource: NameServiceSource?,
    val nameServiceUri: String?
)
