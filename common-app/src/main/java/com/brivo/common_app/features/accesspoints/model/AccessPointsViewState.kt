package com.brivo.common_app.features.accesspoints.model

data class AccessPointsViewState(
    val passId: String = "",
    val siteName: String = "",
    val siteId: String = "",
    val selectedSiteHasTrustedNetwork: Boolean = false,
    val accessPoints: List<AccessPointUIModel> = emptyList(),
    val loading: Boolean = true,
    val alertMessage: String = "",
    val shouldShowBottomSheet: Boolean = false,
    val siteDetailsBottomSheetUIModel: SiteDetailsBottomSheetUIModel = SiteDetailsBottomSheetUIModel(
        hasTrustedNetwork = false,
        preScreening = null,
        siteId = "",
        siteName = "",
        timeZone = null
    )
)
