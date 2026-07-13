package com.brivo.common_app.features.continuousscanner

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.brivo.common_app.navigation.Destinations

fun NavController.navigateToContinuousScanner(navOptions: NavOptions? = null) {
    this.navigate(Destinations.ContinuousScanner.route, navOptions)
}

/**
 * Registers the Continuous Scanner destination. The [content] slot lets each app supply its own
 * ViewModel-backed screen (see [ContinuousScannerScreen]) while the route stays shared in common-app.
 */
fun NavGraphBuilder.continuousScannerScreen(
    content: @Composable () -> Unit
) {
    composable(
        route = Destinations.ContinuousScanner.route
    ) {
        content()
    }
}
