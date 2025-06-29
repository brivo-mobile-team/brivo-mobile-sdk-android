package com.brivo.app_sdk_public.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.brivo.app_sdk_public.features.accesspoints.navigation.accessPointsScreen
import com.brivo.app_sdk_public.features.accesspoints.navigation.navigateAccessPointsScreen
import com.brivo.app_sdk_public.features.home.presentation.HomeScreen
import com.brivo.app_sdk_public.features.redeempass.navigation.navigateRedeemPassScreen
import com.brivo.app_sdk_public.features.redeempass.navigation.redeemPassScreen
import com.brivo.app_sdk_public.features.unlockdoor.navigation.navigateUnlockDoorMagicButtonScreen
import com.brivo.app_sdk_public.features.unlockdoor.navigation.navigateUnlockDoorScreen
import com.brivo.app_sdk_public.features.unlockdoor.navigation.unlockDoorMagicButtonScreen
import com.brivo.app_sdk_public.features.unlockdoor.navigation.unlockDoorScreen

const val HomeNavigationRoute = "home_route"

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = HomeNavigationRoute,
        modifier = modifier
    ) {
        homeGraph(
            onRedeemPassPressed = {
                navController.navigateRedeemPassScreen()
            },
            onSitePressed = { passId, siteId ->
                navController.navigateAccessPointsScreen(passId = passId, siteId = siteId)
            },
            onMagicButtonPressed = {
                navController.navigateUnlockDoorMagicButtonScreen()
            },
            nestedGraph = {
                unlockDoorMagicButtonScreen(onBackPressed = { navController.navigateUp()})
                unlockDoorScreen(onBackPressed = { navController.navigateUp()})
                redeemPassScreen(onBackPressed = { navController.navigateUp()} )
                accessPointsScreen(
                    onAccessPointPressed = { passId, accessPointId, accessPointName, hasTrustedNetwork ->
                        navController.navigateUnlockDoorScreen(
                            passId = passId,
                            accessPointId = accessPointId,
                            accessPointName = accessPointName,
                            hasTrustedNetwork = hasTrustedNetwork
                        )
                    }
                )
            }
        )
    }
}

fun NavGraphBuilder.homeGraph(
    onRedeemPassPressed: () -> Unit,
    onSitePressed: (String, String) -> Unit,
    onMagicButtonPressed: () -> Unit,
    nestedGraph: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = HomeNavigationRoute,
        startDestination = Destinations.Home.route
    ) {
        composable(
            route = Destinations.Home.route
        ) {
            HomeScreen(
                onRedeemPassPressed = onRedeemPassPressed,
                onSitePressed = onSitePressed,
                onMagicButtonPressed = onMagicButtonPressed
            )
        }
        nestedGraph()
    }
}
