package com.brivo.common_app.features.accesspoints.model

data class SiteDetailsBottomSheetUIModel(
    val siteId: String,
    val siteName: String,
    val hasTrustedNetwork: Boolean,
    val preScreening: String?,
    val timeZone: String?
)
