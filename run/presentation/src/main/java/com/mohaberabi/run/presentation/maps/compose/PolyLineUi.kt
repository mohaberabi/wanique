package com.mohaberabi.run.presentation.maps.compose

import androidx.compose.ui.graphics.Color
import com.mohaberabi.core.domain.model.AppLatLng

data class PolyLineUi(
    val location1: AppLatLng,
    val location2: AppLatLng,
    val color: Color,
)
