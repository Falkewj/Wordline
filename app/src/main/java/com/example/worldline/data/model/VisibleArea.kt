package com.example.worldline.data.model

import android.util.Log
import android.util.SizeF
import androidx.compose.ui.geometry.Offset

class VisibleArea(ratio: Float) {
    // NOTE: alternative move back to diagramviewmodel. Functional approach. Approach could be layering path alterations/morphs (generate visual paths -> morph to geodesics -> morph to ui canvas)
    //  z could be how path is affected by grav or z could be the grav of the worldline. have both with 4D vectors. An anchoring grid beneath worldline grid could also act as gravity, might be more representative
    //  of how gravity really works, so easier to wrap head around and work with - for now second grid is preferred solution. I really like the functional layered solution with an anchoring grid, and z to represent mass.
    //  it makes sense in my head, it would be quite easy to test, and its a new approach for me and new stuff is why im here

    // NOTE: Gravity could influence whats visible in the visible area with paths curving into it. Morph to geodesics first?
    //  Layer list would then look like: (morph to geodesics -> generate visual paths -> Morph to ui canvas).

    // TODO: if above chosen: make botleft and topright pair. Delete initialUnitsOnY. adjust functions as needed (probably not much)

    private val initialUnitsOnY = 10f
    private var botLeft = Offset(-1f,-1f)
    private var topRight = Offset(initialUnitsOnY * ratio, initialUnitsOnY)

    fun inArea(path: Path): Pair<Offset, Offset>? {

        val enterX = path.solveForX(botLeft.y).takeIf { it in botLeft.x..topRight.x }
        val enterY = path.solveForY(botLeft.x).takeIf { it in botLeft.y..topRight.y }
        val exitX = path.solveForX(topRight.y).takeIf { it in botLeft.x..topRight.x }
        val exitY = path.solveForY(topRight.x).takeIf { it in botLeft.y..topRight.y }

        val enterOffset = when {
            enterX != null && enterY != null -> Offset(enterX, enterY)
            enterX != null -> Offset(enterX, botLeft.y)
            enterY != null -> Offset(botLeft.x, enterY)
            else -> null
        }
        val exitOffset = when {
            exitX != null && exitY != null -> Offset(exitX, exitY)
            exitX != null -> Offset(exitX, topRight.y)
            exitY != null -> Offset(topRight.x, exitY)
            else -> null
        }

        if(enterOffset == null || exitOffset == null) return null
        return Pair(enterOffset, exitOffset)

    }
    fun move(vector: Offset){
        botLeft += vector
        topRight += vector
    }
    fun zoom(zoomAmount: Float){
//        NOTE: Wait until you know what type the pinch drag gesture returns
    }

    fun updateRatio(ratio: Float){ // TODO: Rotate topright vector

    }

    fun getSize(): SizeF{
        val diagonalVector = topRight - botLeft
        return SizeF(diagonalVector.x, diagonalVector.y)
    }

    fun translate(toTranslate: Offset): Offset{
        return toTranslate - botLeft
    }

    fun getGridLines(): List<Pair<Offset, Offset>>{ // NOTE: This works... but is it shitty and confusing? yes.. was just an experiment so do not use as is!!!!!. Rewrite to make gridlines independently of each other
        val gridlines: MutableList<Pair<Offset, Offset>> = mutableListOf()
//        val size = getSize()
//        val offsetStep = Offset(
//            x =
//        )
//        }
        val gridSequence = generateSequence(botLeft) {
            Offset(
                x = Math.ceil(it.x.toDouble()).toFloat(),
                y = Math.ceil(it.y.toDouble()).toFloat()
            ) + Offset(1f,1f)
        }.takeWhile { it.x <= topRight.x || it.y <= topRight.y }
        for(line in gridSequence) Log.d("Sequence", line.toString())
        Log.d("Sequence", topRight.toString())
        for(gridline in gridSequence){
            gridlines.add(Pair(
                Offset(x = gridline.x, y = botLeft.y),
                Offset(x = gridline.x, y = topRight.y)
            ))
            gridlines.add(Pair(
                Offset(x = botLeft.x, gridline.y),
                Offset(x = topRight.x, gridline.y)
            ))
        }

        return gridlines
    }
}