package com.brivo.common_app.features.home.model

import com.brivo.sdk.onair.model.BrivoSite

data class BrivoSiteUIModel(
    val id: String,
    val siteName: String
)

fun BrivoSite.toBrivoSiteUIModel() =
    BrivoSiteUIModel(
        id = this.id,
        siteName = this.siteName
    )
