package com.brivo.app_sdk_sample.features.home.model

import com.brivo.sdk.onair.model.BrivoSite

data class BrivoSiteUIModel(
    val id: Int,
    val siteName: String
)

fun BrivoSite.toBrivoSiteUIModel() =
    BrivoSiteUIModel(
        id = this.id,
        siteName = this.siteName
    )
