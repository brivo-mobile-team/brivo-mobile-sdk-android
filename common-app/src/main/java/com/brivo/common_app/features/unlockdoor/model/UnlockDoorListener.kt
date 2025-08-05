package com.brivo.common_app.features.unlockdoor.model

import com.brivo.sdk.model.BrivoResult

interface UnlockDoorListener {

    fun onUnlockDoorEvent(result: BrivoResult)
}
