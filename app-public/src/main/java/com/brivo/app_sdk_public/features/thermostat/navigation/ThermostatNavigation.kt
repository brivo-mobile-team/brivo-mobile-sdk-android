package com.brivo.app_sdk_public.features.thermostat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.brivo.app_sdk_public.features.thermostat.ThermostatIdArg
import com.brivo.app_sdk_public.features.thermostat.ThermostatScreen
import com.brivo.common_app.navigation.Destinations

fun NavController.navigateThermostatScreen(thermostatId: String) {
    navigate("${Destinations.ResideoThermostat.route}/$thermostatId")
}

fun NavGraphBuilder.thermostatScreen(onBackPressed: () -> Unit) {
    composable(
        route = "${Destinations.ResideoThermostat.route}/{$ThermostatIdArg}",
        arguments = listOf(
            navArgument(ThermostatIdArg) { type = NavType.StringType }
        )
    ) {
        ThermostatScreen(onBackPressed = onBackPressed)
    }
}
