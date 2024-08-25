package com.algorand.android.nameservice.domain.model

data class NameService(
    val accountAddress: String,
    val nameServiceName: String?,
    val nameServiceSource: NameServiceSource?,
    val nameServiceUri: String?
)
