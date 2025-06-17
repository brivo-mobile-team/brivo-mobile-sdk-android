package com.brivo.app_sdk_public.domain.models

enum class RequestedPermissions {
    BLUETOOTH,
    LOCATION,
    LOCATION_AND_BLUETOOTH;

    companion object {
        fun fromName(name: String?): RequestedPermissions? {
            return when (name) {
                BLUETOOTH.name -> {
                    BLUETOOTH
                }

                LOCATION.name -> {
                    LOCATION
                }

                LOCATION_AND_BLUETOOTH.name -> {
                    LOCATION_AND_BLUETOOTH
                }

                else -> null
            }
        }
    }
}
