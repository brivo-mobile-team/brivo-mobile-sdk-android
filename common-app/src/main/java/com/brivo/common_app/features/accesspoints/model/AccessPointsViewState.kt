package com.brivo.common_app.features.accesspoints.model

data class AccessPointsViewState(
    val passId: String = "",
    val siteName: String = "",
    val selectedSiteHasTrustedNetwork: Boolean = false,
    val accessPoints: List<AccessPointUIModel> = emptyList(),
    val loading: Boolean = true,
    val alertMessage: String = ""
)
