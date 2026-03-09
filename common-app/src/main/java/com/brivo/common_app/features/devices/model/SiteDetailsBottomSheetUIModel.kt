package com.brivo.common_app.features.devices.model

data class SiteDetailsBottomSheetUIModel(
    val siteId: String = "",
    val siteName: String = "",
    val hasTrustedNetwork: Boolean = false,
    val preScreening: String? = "",
    val timeZone: String? = ""
)
