package com.example.worldline.ui.screens.diagram

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.worldline.data.model.DrawInstruction
import com.example.worldline.data.model.Path
import com.example.worldline.data.model.VisibleArea

class DiagramViewModel() : ViewModel() {
    private var canvasSize: Size = Size.Zero
    private val visibleArea by lazy { VisibleArea(canvasSize.width/canvasSize.height) }
    private var referenceFrame: Path = Path(0f, 0f, Color.Blue, 5f)
    private val nullLine: Path = Path(1f, 0f, Color.Yellow, 5f)
    private val worldlines: MutableList<Path> = mutableListOf(referenceFrame, nullLine)
    var drawInstructions by mutableStateOf<List<DrawInstruction>>(listOf()) // NOTE: Triggers recomposition
        private set

    companion object {
        private const val c = 299_792_458 //NOTE: m/s
    }


    fun updateCanvasSize(size: Size) {
        canvasSize = size
        createDrawInstructions()
    }

    fun zoom(zoomAmount: Float) {
        visibleArea.zoom(zoomAmount)
        createDrawInstructions()
    }

    fun pan(panVector: Offset){
        visibleArea.move(panVector)
        createDrawInstructions()
    }


    private fun createDrawInstructions() {
        val newDrawInstructions: MutableList<DrawInstruction> = mutableListOf()

        for(gridlines in visibleArea.getGridLines()){
            newDrawInstructions.add(DrawInstruction(
                toCanvasOffset(gridlines.first),
                toCanvasOffset(gridlines.second),
                2f,
                Color.Black
            ))
        }

        for(worldline in worldlines) {
            val (enterOffset, exitOffset) = visibleArea.inArea(worldline) ?: continue
            newDrawInstructions.add(DrawInstruction(
                toCanvasOffset(enterOffset),
                toCanvasOffset(exitOffset),
                worldline.width,
                worldline.color
            ))
        }
        drawInstructions = newDrawInstructions
    }

    private fun morph(){
        // NOTE: Maybe move scaling here as well as gravity morphing of grid? Alternative make gravity a separate grid that spacetime is anchored to.
    }

    private fun toCanvasOffset(internalOffset: Offset): Offset { // TODO: scale is not offset dependant. Move outside?
        val size = visibleArea.getSize()
        val scaleX = canvasSize.width / size.width
        val scaleY = canvasSize.height / size.height
        val translation = visibleArea.translate(internalOffset)

        return Offset(translation.x * scaleX, canvasSize.height - (translation.y * scaleY))
    }

    private fun toInternalOffset(canvasOffset: Offset): Offset {
//        TODO: Opposite of toCanvasOffset but with single Offset.
        val internalOffset = canvasOffset
        return internalOffset
    }




    fun onLineTapped(offset: Offset) {
        //TODO: Find line by offset and assign it to var selectedLine
    }

}