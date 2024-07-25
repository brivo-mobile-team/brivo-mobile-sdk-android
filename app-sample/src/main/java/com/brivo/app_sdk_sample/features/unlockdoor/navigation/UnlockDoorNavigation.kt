package com.brivo.app_sdk_sample.features.unlockdoor.navigation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.brivo.app_sdk_sample.features.unlockdoor.presentation.UnlockDoorScreen
import com.brivo.app_sdk_sample.navigation.Destinations

const val PassIdArg = "passIdArg"
const val AccessPointIdArg = "accessPointIdArg"
const val AccessPointNameArg = "accessPointNameArg"

class UnlockDoorArgs(val passId: String, val accessPointId: String, val accessPointName: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        Uri.decode(checkNotNull(savedStateHandle[PassIdArg] ?: "").toString()),
        Uri.decode(checkNotNull(savedStateHandle[AccessPointIdArg] ?: "").toString()),
        Uri.decode(checkNotNull(savedStateHandle[AccessPointNameArg] ?: "").toString()),
    )
}

fun NavController.navigateUnlockDoorScreen(passId: String, accessPointId: String, accessPointName: String) {
    this.navigate("${Destinations.UnlockDoor.route}/$passId/$accessPointId/$accessPointName")
}

fun NavGraphBuilder.unlockDoorScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = "${Destinations.UnlockDoor.route}/{$PassIdArg}/{$AccessPointIdArg}/{$AccessPointNameArg}",
        arguments = listOf(
            navArgument(PassIdArg) { type = NavType.StringType },
            navArgument(AccessPointIdArg) { type = NavType.StringType },
            navArgument(AccessPointNameArg) { type = NavType.StringType }
        )
    ) {
        UnlockDoorScreen(
            onBackPressed = onBackPressed
        )
    }
}

fun NavController.navigateUnlockDoorMagicButtonScreen() {
    this.navigate(Destinations.UnlockDoor.route)
}

fun NavGraphBuilder.unlockDoorMagicButtonScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = Destinations.UnlockDoor.route,
    ) {
        UnlockDoorScreen(
            onBackPressed = onBackPressed
        )
    }
}