package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CuisineViewModel
import com.example.ui.screens.MainAppContainer
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Turn on edge-to-edge full bleed rendering
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val viewModel: CuisineViewModel = viewModel()
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainAppContainer(viewModel = viewModel)
                }
            }
        }
    }
}
