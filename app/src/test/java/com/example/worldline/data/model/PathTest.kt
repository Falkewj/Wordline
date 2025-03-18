package com.example.worldline.data.model

import androidx.compose.ui.graphics.Color
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class PathTest {
    private lateinit var verticalPath: Path
    private lateinit var positivelyAngledPath: Path
    private lateinit var negativelyAngledPath: Path

    @Before
    fun setUp(){
        verticalPath = Path(0f, 0f, 5f, Color.Red, 5f)
        positivelyAngledPath = Path(0.5f, 0f, 4f, Color.Red, 5f)
        negativelyAngledPath = Path(-0.5f, 0f, 9f, Color.Red, 5f)
    }

    @Test
    fun verticalPath_solveForY_returnPositiveInfinity(){
        val expectedResult = Float.POSITIVE_INFINITY

        val resultOnEqualsDistance = verticalPath.solveForY(verticalPath.distanceFromReference)
        val resultOnLargerThanDistance = verticalPath.solveForY(verticalPath.distanceFromReference + 1)
        val resultOnSmallerThanDistance = verticalPath.solveForY(verticalPath.distanceFromReference - 1)

        assertEquals(expectedResult, resultOnEqualsDistance)
        assertEquals(expectedResult, resultOnLargerThanDistance)
        assertEquals(expectedResult, resultOnSmallerThanDistance)
    }


    @Test
    fun positivelyAngledPath_solveForY_returnCorrectY(){
        val x = 9f
        val expectedResult = 10f
        val result = positivelyAngledPath.solveForY(x)
        assertEquals(expectedResult, result)

    }

    @Test
    fun negativelyAngledPath_solveForY_returnCorrectY(){
        val x = 2f
        val expectedResult = 14f
        val result = negativelyAngledPath.solveForY(x)
        assertEquals(expectedResult, result)
    }
    @Test
    fun verticalPath_solveForX_returnDistanceFromReference(){
        val y = 99f
        val expectedResult = verticalPath.distanceFromReference
        val result = verticalPath.solveForX(y)

        assertEquals(expectedResult, result)
    }


    @Test
    fun positivelyAngledPath_solveForX_returnCorrectX(){
        val y = 9f
        val expectedResult = 8.5f
        val result = positivelyAngledPath.solveForX(y)
        assertEquals(expectedResult, result)

    }

    @Test
    fun negativelyAngledPath_solveForX_returnCorrectX(){
        val y = 2f
        val expectedResult = 8f
        val result = negativelyAngledPath.solveForX(y)
        assertEquals(expectedResult, result)
    }
}