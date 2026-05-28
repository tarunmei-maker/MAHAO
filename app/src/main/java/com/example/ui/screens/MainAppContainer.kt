package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CuisineViewModel

@Composable
fun MainAppContainer(
    viewModel: CuisineViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == "menu",
                    onClick = { viewModel.selectTab("menu") },
                    icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = "Menu page") },
                    label = { Text("Browse Menu", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_menu_tab")
                )
                NavigationBarItem(
                    selected = currentTab == "track",
                    onClick = { viewModel.selectTab("track") },
                    icon = { Icon(Icons.Default.Moped, contentDescription = "Track page") },
                    label = { Text("Track Delivery", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_track_tab")
                )
                NavigationBarItem(
                    selected = currentTab == "seller",
                    onClick = { viewModel.selectTab("seller") },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Seller panel") },
                    label = { Text("Seller Portal", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_seller_tab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                "menu" -> CustomerMenuScreen(viewModel = viewModel)
                "track" -> TrackingScreen(viewModel = viewModel)
                "seller" -> SellerDashboardScreen(viewModel = viewModel)
            }
        }
    }
}
