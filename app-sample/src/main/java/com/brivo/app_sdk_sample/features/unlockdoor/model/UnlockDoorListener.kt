package com.brivo.app_sdk_sample.features.unlockdoor.model

import com.brivo.sdk.model.BrivoResult

interface UnlockDoorListener {

    fun onUnlockDoorEvent(result: BrivoResult)
}