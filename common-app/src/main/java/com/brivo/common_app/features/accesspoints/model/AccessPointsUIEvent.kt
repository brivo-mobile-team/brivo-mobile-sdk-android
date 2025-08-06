package com.brivo.common_app.features.accesspoints.model

sealed class AccessPointsUIEvent {

    data class UpdateAlertMessage(val message: String) : AccessPointsUIEvent()
}
