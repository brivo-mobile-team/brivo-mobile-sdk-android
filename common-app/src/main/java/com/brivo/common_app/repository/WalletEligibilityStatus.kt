package com.brivo.common_app.repository

data class WalletEligibilityStatus(
    val hasPurchasedNFC: Boolean,
    val hasCurrentCredential: Boolean,
)
