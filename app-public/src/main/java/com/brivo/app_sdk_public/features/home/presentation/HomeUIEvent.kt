package com.brivo.app_sdk_public.features.home.presentation

sealed class HomeUIEvent {

    data object LoadPasses : HomeUIEvent()

    data object Refresh : HomeUIEvent()

    data class UpdateAlertMessage(val message: String) : HomeUIEvent()
}
