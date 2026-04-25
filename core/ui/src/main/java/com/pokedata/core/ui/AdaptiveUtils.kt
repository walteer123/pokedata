package com.pokedata.core.ui

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowWidthSizeClass(): WindowWidthSizeClass {
    val context = LocalContext.current
    return calculateWindowSizeClass(activity = context as Activity).widthSizeClass
}

@Composable
fun rememberFoldingFeatures(): List<FoldingFeature> {
    val context = LocalContext.current

    val windowLayoutInfo: WindowLayoutInfo by WindowInfoTracker.getOrCreate(context)
        .windowLayoutInfo(context as Activity)
        .collectAsState(initial = WindowLayoutInfo(emptyList()))

    return windowLayoutInfo.displayFeatures.filterIsInstance<FoldingFeature>()
}
