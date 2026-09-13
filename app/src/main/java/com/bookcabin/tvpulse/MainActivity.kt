package com.bookcabin.tvpulse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bookcabin.tvpulse.features.common.TVPulseTheme
import com.bookcabin.tvpulse.features.navigation.AppDestinations
import com.bookcabin.tvpulse.features.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            TVPulseTheme {
                val navController = rememberNavController()

                DisposableEffect(navController) {
                    val listener = Consumer<Intent> { intent -> navController.handleDeepLink(intent) }
                    addOnNewIntentListener(listener)
                    onDispose { removeOnNewIntentListener(listener) }
                }

                val routes = AppDestinations.entries
                val selectedIndex = getSelectedIndex(navController)

                Scaffold(
                    topBar = {
                        PrimaryTabRow(
                            selectedTabIndex = selectedIndex,
                            modifier = Modifier.statusBarsPadding(),
                        ) {
                            routes.forEachIndexed { index, topLevelRoute ->
                                Tab(
                                    selected = selectedIndex == index,
                                    onClick = {
                                        navController.navigate(topLevelRoute.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    text = {
                                        Text(
                                            text = topLevelRoute.label,
                                            style = MaterialTheme.typography.titleSmall,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    private fun getSelectedIndex(navController: NavHostController): Int {
        val routes = AppDestinations.entries
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // fallback if destination isn't resolving properly yet on cold start
        return routes.indexOfFirst { currentDestination?.hasRoute(it.route::class) == true }.takeIf { it >= 0 } ?: 0
    }
}
