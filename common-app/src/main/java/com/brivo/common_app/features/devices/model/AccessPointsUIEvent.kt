package com.brivo.common_app.features.devices.model

sealed class AccessPointsUIEvent {
    data class UpdateAlertMessage(val message: String) : AccessPointsUIEvent()
    data class ShouldShowBottomSheet(val shouldShow: Boolean): AccessPointsUIEvent()
}
