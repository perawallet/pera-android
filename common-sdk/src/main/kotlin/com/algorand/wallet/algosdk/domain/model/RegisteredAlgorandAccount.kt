package com.algorand.wallet.algosdk.model

data class RegisteredAlgorandAccount(
    val address: String,
    val algoValue: String,
    val usdValue: String,
    val calculationType: String,
    val accountExists: Boolean,
    val isImportedToDB: Boolean,
    val account: Int,
    val change: Int,
    val keyIndex: Int,
    val derivationType: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RegisteredAlgorandAccount

        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        return result
    }
}
