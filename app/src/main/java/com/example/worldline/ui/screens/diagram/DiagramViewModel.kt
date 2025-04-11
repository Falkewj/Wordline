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
    private val nullLine: Path = Path(1f, 0f, Color.Red, 5f)
    private val worldlines: MutableList<Path> = mutableListOf(referenceFrame, nullLine)
    var drawInstructions by mutableStateOf<List<DrawInstruction>>(listOf()) // NOTE: Triggers recomposition
        private set

    companion object {
        private const val c = 299_792_458 //NOTE: m/s
    }


    fun updateCanvasSize(size: Size) {
        canvasSize = size
        visibleArea.updateRatio(canvasSize.width/canvasSize.height)
        createDrawInstructions()
    }

    fun zoom(zoomAmount: Float) {
        visibleArea.zoom(zoomAmount)
        createDrawInstructions()
    }

    fun pan(panVector: Offset){
        val (scaleX, scaleY) = scale()
        visibleArea.move(Offset(-1 * (panVector.x / scaleX), (panVector.y / scaleY)))
        createDrawInstructions()
    }


    private fun createDrawInstructions() {
        val newDrawInstructions: MutableList<DrawInstruction> = mutableListOf()

        for(gridlines in visibleArea.getGridLines()){
            newDrawInstructions.add(DrawInstruction(
                toExternalOffset(gridlines.first),
                toExternalOffset(gridlines.second),
                2f,
                Color.Black
            ))
        }

        for(worldline in worldlines) {
            val (enterOffset, exitOffset) = visibleArea.inArea(worldline) ?: continue
            newDrawInstructions.add(DrawInstruction(
                toExternalOffset(enterOffset),
                toExternalOffset(exitOffset),
                worldline.width,
                worldline.color
            ))
        }
        drawInstructions = newDrawInstructions
    }

    private fun morph(){
        // NOTE: Maybe move scaling here as well as gravity morphing of grid? Alternative make gravity a separate grid that spacetime is anchored to.
    }

    private fun toExternalOffset(internalOffset: Offset): Offset { // TODO: scale is not offset dependant. Move outside?
        val (scaleX, scaleY) = scale()
        val translation = visibleArea.translateToExternal(internalOffset)

        return Offset(translation.x * scaleX, canvasSize.height - (translation.y * scaleY))
    }

    private fun toInternalOffset(externalOffset: Offset): Offset {
//        TODO: Opposite of toCanvasOffset but with single Offset.
//        NOTE: Below untested and quickly written
        val (scaleX, scaleY) = scale()

        return Offset(externalOffset.x / scaleX, canvasSize.height - (externalOffset.y / scaleY))
    }

    private fun scale(): Pair<Float, Float>{
        val size = visibleArea.getSize()
        return Pair(canvasSize.width / size.width, canvasSize.height / size.height)
    }




    fun onLineTapped(offset: Offset) {
        //TODO: Find line by offset and assign it to var selectedLine
    }

}