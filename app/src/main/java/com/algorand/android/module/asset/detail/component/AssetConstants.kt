package com.algorand.android.module.asset.detail.component

import java.math.BigDecimal

object AssetConstants {
    const val ALGO_ASSET_ID = -7L
    const val DEFAULT_ASSET_DECIMAL = 0
    const val ALGO_FULL_NAME = "Algo"
    const val ALGO_SHORT_NAME = "ALGO"
    const val ALGO_DECIMALS = 6
    const val ALGORAND_DISCORD_URL = "https://discord.com/invite/84AActu3at"
    const val ALGORAND_TELEGRAM_URL = "https://t.me/algorand"
    const val ALGORAND_TWITTER_USERNAME = "Algorand"
    const val ALGORAND_WEBSITE_URL = "https://algorand.com"
    val ALGO_TOTAL_SUPPLY: BigDecimal = BigDecimal.valueOf(10_000_000_000L)
    val MINIMUM_CURRENCY_VALUE_TO_DISPLAY_EXACT_AMOUNT: BigDecimal = BigDecimal.valueOf(0.000001)

    const val USDC_MAINNET_ID = 31566704L
    const val USDC_TESTNET_ID = 10458941L
    const val USDT_MAINNET_ID = 312769L
}
