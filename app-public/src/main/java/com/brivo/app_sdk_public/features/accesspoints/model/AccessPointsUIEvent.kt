package com.brivo.app_sdk_public.features.accesspoints.model

sealed class AccessPointsUIEvent {

    data class UpdateAlertMessage(val message: String) : AccessPointsUIEvent()
}