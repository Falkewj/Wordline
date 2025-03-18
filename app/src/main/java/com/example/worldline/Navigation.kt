package com.example.worldline

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.worldline.ui.screens.diagram.DiagramScreen
import com.example.worldline.ui.screens.settings.SettingsScreen

enum class Screens() {
    DIAGRAM,
    SETTINGS
}

@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screens.DIAGRAM.name,
        modifier = modifier
    ) {
        composable(Screens.DIAGRAM.name) {
            DiagramScreen(
                onSettingsTapped = { navController.navigate(Screens.SETTINGS.name) }
            )
        }
        composable(Screens.SETTINGS.name) {
            SettingsScreen()
        }
    }
}