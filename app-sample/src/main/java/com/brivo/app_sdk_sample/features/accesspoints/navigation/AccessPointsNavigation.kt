package com.brivo.app_sdk_sample.features.accesspoints.navigation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.brivo.app_sdk_sample.features.accesspoints.presentation.AccessPointsScreen
import com.brivo.app_sdk_sample.navigation.Destinations

const val PassIdArg = "passIdArg"
const val SiteIdArg = "siteIdArg"

class AccessPointsArgs(val passId: String, val siteId: Int) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        Uri.decode(checkNotNull(savedStateHandle[PassIdArg]).toString()),
        Uri.decode(checkNotNull(savedStateHandle[SiteIdArg]).toString()).toInt(),
    )
}

fun NavController.navigateAccessPointsScreen(passId: String, siteId: Int) {
    this.navigate("${Destinations.AccessPoints.route}/$passId/$siteId")
}

fun NavGraphBuilder.accessPointsScreen(
    onAccessPointPressed: (String, String, String) -> Unit
) {
    composable(
        route = "${Destinations.AccessPoints.route}/{$PassIdArg}/{$SiteIdArg}",
        arguments = listOf(
            navArgument(PassIdArg) { type = NavType.StringType },
            navArgument(SiteIdArg) { type = NavType.IntType }
        )
    ) {
        AccessPointsScreen(
            onAccessPointPressed = onAccessPointPressed
        )
    }
}