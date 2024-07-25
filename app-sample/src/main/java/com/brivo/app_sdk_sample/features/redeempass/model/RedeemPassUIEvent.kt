package com.brivo.app_sdk_sample.features.redeempass.model

import androidx.compose.ui.text.input.TextFieldValue

sealed class RedeemPassUIEvent {

    data class UpdateEmail(val newValue: TextFieldValue) : RedeemPassUIEvent()

    data class UpdateToken(val newValue: TextFieldValue) : RedeemPassUIEvent()

    data class UpdateRegion(val newValue: Boolean) : RedeemPassUIEvent()

    data class UpdateAlertMessage(val message: String) : RedeemPassUIEvent()

    data object RedeemPass: RedeemPassUIEvent()
}