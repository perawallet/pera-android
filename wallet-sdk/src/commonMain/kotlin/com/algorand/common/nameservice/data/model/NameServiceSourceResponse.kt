package com.algorand.common.nameservice.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class NameServiceSourceResponse {
    @SerialName("nfdomain")
    NFDOMAIN,

    UNKNOWN
}
