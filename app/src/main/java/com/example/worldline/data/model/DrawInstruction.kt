package com.example.worldline.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class DrawInstruction(val startOffset: Offset, val endOffset: Offset, val width: Float, val color: Color) { // TODO: Change to be a list of vectors so canvas can use path instead of drawline. Not relevant until gravity comes into play
}