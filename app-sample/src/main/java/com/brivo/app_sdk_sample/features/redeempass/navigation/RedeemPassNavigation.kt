package com.brivo.app_sdk_sample.features.redeempass.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.brivo.app_sdk_sample.features.redeempass.presentation.RedeemPassScreen
import com.brivo.app_sdk_sample.navigation.Destinations

fun NavController.navigateRedeemPassScreen(navOptions: NavOptions? = null) {
    this.navigate(Destinations.RedeemPass.route, navOptions)
}

fun NavGraphBuilder.redeemPassScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = Destinations.RedeemPass.route
    ) {
        RedeemPassScreen(
            onBackPressed = onBackPressed
        )
    }
}