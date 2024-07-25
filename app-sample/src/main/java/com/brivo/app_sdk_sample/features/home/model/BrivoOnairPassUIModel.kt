package com.brivo.app_sdk_sample.features.home.model

import com.brivo.sdk.onair.model.BrivoOnairPass

data class BrivoOnairPassUIModel(
    val passId: String,
    val accountId: Long,
    val accountName: String,
    val firstName: String,
    val lastName: String,
    val accessToken: String?,
    val refreshToken: String,
    val sites: List<BrivoSiteUIModel> = emptyList()
)

fun BrivoOnairPass.toBrivoOnairPassUIModel() =
    BrivoOnairPassUIModel(
        passId = this.passId,
        accountId = this.accountId,
        accountName = this.accountName,
        firstName = this.firstName,
        lastName = this.lastName,
        accessToken = this.brivoOnairPassCredentials.tokens.accessToken,
        refreshToken = this.brivoOnairPassCredentials.tokens.refreshToken,
        sites = this.sites.map { site -> site.toBrivoSiteUIModel() }
    )