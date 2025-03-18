package com.example.worldline.data.model

import androidx.compose.ui.graphics.Color
import kotlin.math.sqrt

class Path(val speed: Float, val distanceFromReference: Float, val color: Color, val width: Float) {

    fun solveForY(x: Float) : Float {
        if (speed == 0f) {
            return Float.POSITIVE_INFINITY
        } else {
            return (x-distanceFromReference)/speed
        }
    }
    fun solveForX(y: Float) : Float {
        if(speed == 0f) {
            return distanceFromReference
        } else {
            return y * speed + distanceFromReference
        }
    }
}