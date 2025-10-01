package com.brivo.app_sdk_public.features.home.model

sealed class HomeUIEvent {

    data object LoadPasses : HomeUIEvent()

    data object RefreshPasses : HomeUIEvent()

    data class UpdateAlertMessage(val message: String) : HomeUIEvent()
    data class ShouldShowBotomSheet(val shouldShow: Boolean) : HomeUIEvent()
    data class UpdateBottomSheetInformation(
        val hasAllegionBleCredentials: Boolean,
        val hasHidOrigoMobilePass: Boolean,
        val hidOrigoWalletPassEnabled: Boolean,
        val hasBrivoWalletPass: Boolean,
    ) : HomeUIEvent()
}
