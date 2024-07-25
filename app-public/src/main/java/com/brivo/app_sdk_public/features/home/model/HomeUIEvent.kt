package com.brivo.app_sdk_public.features.home.model

sealed class HomeUIEvent {

    data object LoadPasses : HomeUIEvent()

    data object RefreshPasses : HomeUIEvent()

    data class UpdateAlertMessage(val message: String) : HomeUIEvent()
}