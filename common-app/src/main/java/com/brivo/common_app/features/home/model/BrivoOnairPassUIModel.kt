package com.brivo.common_app.features.home.model

import com.brivo.sdk.onair.model.BrivoOnairPass

data class BrivoOnairPassUIModel(
    val passId: String,
    val accountId: Long,
    val accountName: String,
    val firstName: String,
    val lastName: String,
    val accessToken: String?,
    val refreshToken: String,
    val sites: List<BrivoSiteUIModel> = emptyList(),
    val hasAllegionBleCredentials: Boolean,
    val hasHidOrigoMobilePass: Boolean,
    val hidOrigoWalletPassEnabled: Boolean,
    val hasBrivoWalletPass: Boolean,
)

fun BrivoOnairPass.toBrivoOnairPassUIModel() =
    BrivoOnairPassUIModel(
        passId = this.pass,
        accountId = this.accountId,
        accountName = this.accountName,
        firstName = this.firstName,
        lastName = this.lastName,
        accessToken = this.brivoOnairPassCredentials.tokens.accessToken,
        refreshToken = this.brivoOnairPassCredentials.tokens.refreshToken ?: "",
        sites = this.sites.map { site -> site.toBrivoSiteUIModel() },
        hasAllegionBleCredentials = this.hasAllegionBleCredentials,
        hasHidOrigoMobilePass = this.hasHidOrigoMobilePass,
        hidOrigoWalletPassEnabled = this.hidOrigoWalletPassEnabled,
        hasBrivoWalletPass = this.hasBrivoWalletPass
    )
