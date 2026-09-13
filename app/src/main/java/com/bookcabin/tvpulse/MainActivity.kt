package com.bookcabin.tvpulse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    
    @OptIn(ExperimentalMaterial3Api::class)
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
                        // The app bar and tabs are only shown on top-level destinations, not on screens like Detail.
                        if (selectedIndex == null) return@Scaffold
                        Column {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = stringResource(R.string.app_name),
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            PrimaryTabRow(selectedTabIndex = selectedIndex) {
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
                                                text = stringResource(topLevelRoute.labelRes).uppercase(),
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                        }
                                    )
                                }
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
    private fun getSelectedIndex(navController: NavHostController): Int? {
        val routes = AppDestinations.entries
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        // fallback if destination isn't resolving properly yet on cold start
        val currentDestination = navBackStackEntry?.destination ?: return 0

        // null when the current destination isn't a tab
        return routes.indexOfFirst { currentDestination.hasRoute(it.route::class) }.takeIf { it >= 0 }
    }
}
