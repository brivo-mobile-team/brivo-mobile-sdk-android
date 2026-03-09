package com.brivo.common_app.features.devices.model

data class AccessPointsViewState(
    val passId: String = "",
    val siteName: String = "",
    val siteId: String = "",
    val selectedSiteHasTrustedNetwork: Boolean = false,
    val accessPoints: List<AccessPointUIModel> = emptyList(),
    val thermostats: List<ResideoThermostatUIModel> = emptyList(),
    val loading: Boolean = true,
    val alertMessage: String = "",
    val shouldShowBottomSheet: Boolean = false,
    val siteDetailsBottomSheetUIModel: SiteDetailsBottomSheetUIModel = SiteDetailsBottomSheetUIModel()
)
