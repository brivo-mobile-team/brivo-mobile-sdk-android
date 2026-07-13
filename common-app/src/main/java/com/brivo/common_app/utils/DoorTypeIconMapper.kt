package com.brivo.common_app.utils

import androidx.annotation.DrawableRes
import com.brivo.common_app.R
import com.brivo.sdk.enums.DoorType

object DoorTypeIconMapper {

    @DrawableRes
    fun getIconForDoorType(doorType: DoorType?): Int? {
        return when (doorType) {
            DoorType.INTERNET -> R.drawable.ic_net
            DoorType.ALLEGION, DoorType.ALLEGION_BLE -> R.drawable.ic_allegion
            DoorType.WAVELYNX -> R.drawable.ic_brivo_eye
            DoorType.HID_ORIGO -> R.drawable.ic_hid
            else -> null
        }
    }

    @DrawableRes
    fun getLockIcon(isUnlocked: Boolean): Int {
        return if (isUnlocked) R.drawable.lock_open else R.drawable.lock
    }
}