package com.brivo.common_app.features.home.model

data class PassDetailsBottomSheetUIModel(
    val shouldShowBottomSheet: Boolean = false,
    val hasAllegionBleCredentials: Boolean = false,
    val hasHidOrigoMobilePass: Boolean = false,
    val hidOrigoWalletPassEnabled: Boolean = false,
    val hasBrivoWalletPass: Boolean = false,
)
