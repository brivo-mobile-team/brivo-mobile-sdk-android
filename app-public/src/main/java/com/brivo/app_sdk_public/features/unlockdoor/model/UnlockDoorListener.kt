package com.brivo.app_sdk_public.features.unlockdoor.model

import com.brivo.sdk.model.BrivoResult

interface UnlockDoorListener {

    fun onUnlockDoorEvent(result: BrivoResult)
}