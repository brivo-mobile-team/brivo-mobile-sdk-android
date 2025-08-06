package com.brivo.app_sdk_public.features.unlockdoor.navigation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.brivo.app_sdk_public.features.unlockdoor.UnlockDoorScreen
import com.brivo.common_app.navigation.Destinations

const val PassIdArg = "passIdArg"
const val AccessPointIdArg = "accessPointIdArg"
const val AccessPointNameArg = "accessPointNameArg"
const val HasTrustedNetwork = "hasTrustedNetwork"

class UnlockDoorArgs(
    val passId: String,
    val accessPointId: String,
    val accessPointName: String,
    val hasTrustedNetwork: Boolean = false
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        Uri.decode(checkNotNull(savedStateHandle[PassIdArg] ?: "").toString()),
        Uri.decode(checkNotNull(savedStateHandle[AccessPointIdArg] ?: "").toString()),
        Uri.decode(checkNotNull(savedStateHandle[AccessPointNameArg] ?: "").toString()),
        Uri.decode(savedStateHandle[HasTrustedNetwork] ?: "false").toBooleanStrictOrNull() ?: false
    )
}

fun NavController.navigateUnlockDoorScreen(
    passId: String,
    accessPointId: String,
    accessPointName: String,
    hasTrustedNetwork: Boolean
) {
    this.navigate("${Destinations.UnlockDoor.route}/$passId/$accessPointId/$accessPointName/$hasTrustedNetwork")
}

fun NavGraphBuilder.unlockDoorScreen(
    onBackPressed: () -> Unit,
    onCheckPermissions: suspend (hasTrustedNetwork: Boolean) -> Boolean
) {
    composable(
        route = "${Destinations.UnlockDoor.route}/{$PassIdArg}/{$AccessPointIdArg}/{$AccessPointNameArg}/{$HasTrustedNetwork}",
        arguments = listOf(
            navArgument(PassIdArg) { type = NavType.StringType },
            navArgument(AccessPointIdArg) { type = NavType.StringType },
            navArgument(AccessPointNameArg) { type = NavType.StringType },
            navArgument(HasTrustedNetwork) { type = NavType.StringType }
        )
    ) {
        UnlockDoorScreen(
            onBackPressed = onBackPressed,
            onCheckPermissions = onCheckPermissions
        )
    }
}

fun NavController.navigateUnlockDoorMagicButtonScreen() {
    this.navigate(Destinations.UnlockDoor.route)
}

fun NavGraphBuilder.unlockDoorMagicButtonScreen(
    onBackPressed: () -> Unit,
    onCheckPermissions: suspend (hasTrustedNetwork: Boolean) -> Boolean
) {
    composable(
        route = Destinations.UnlockDoor.route,
    ) {
        UnlockDoorScreen(
            onBackPressed = onBackPressed,
            onCheckPermissions = onCheckPermissions
        )
    }
}
