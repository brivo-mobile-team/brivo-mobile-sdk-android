package com.brivo.app_sdk_public

object BrivoSampleConstants {
    val CLIENT_ID: String = requireGradleProperties(BuildConfig.CLIENT_ID)
    val CLIENT_SECRET: String = requireGradleProperties(BuildConfig.CLIENT_SECRET)

    const val CONFIG_KEY = "CONFIG_KEY"
    const val CLIENT_ID_KEY = "CLIENT_ID"
    const val CLIENT_SECRET_KEY = "CLIENT_SECRET"

    val CLIENT_ID_EU: String = requireGradleProperties(BuildConfig.CLIENT_ID_EU)
    val CLIENT_SECRET_EU: String = requireGradleProperties(BuildConfig.CLIENT_SECRET_EU)

    const val AUTH_URL = "https://auth.brivo.com"
    const val API_URL = "https://pi.brivo.com/api/"
    const val SMART_HOME_API_URL = "https://api.smarthome.brivo.com/"
    const val AUTH_URL_EU = "https://auth.eu.brivo.com"
    const val API_URL_EU = "https://pi.eu.brivo.com/api/"

    const val SELECTED_SITE = "SELECTED_SITE"
    const val PASS_ID = "PASS_ID"
    const val SELECTED_ACCESS_POINT = "SELECTED_ACCESS_POINT"
    const val IS_MAGIC_DOOR = "IS_MAGIC_DOOR"
}

private fun requireGradleProperties(requiredProperty: String): String{
    require(requiredProperty.isNotBlank()){
        "Please add your client Id and client Secret inside gradle.properties file"
    }
    return requiredProperty
}