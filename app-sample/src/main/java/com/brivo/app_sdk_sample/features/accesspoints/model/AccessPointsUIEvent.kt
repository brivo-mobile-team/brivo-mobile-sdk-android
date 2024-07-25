package com.brivo.app_sdk_sample.features.accesspoints.model

sealed class AccessPointsUIEvent {

    data class UpdateAlertMessage(val message: String) : AccessPointsUIEvent()
}