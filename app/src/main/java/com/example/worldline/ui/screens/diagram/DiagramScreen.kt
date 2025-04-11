package com.example.worldline.ui.screens.diagram

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DiagramScreen(
    onSettingsTapped: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiagramViewModel = viewModel()
) {
    val density = LocalDensity.current
    val minCardHeight = 30.dp
    var cardHeight by remember { mutableStateOf(minCardHeight) }
    val velocityTracker = remember { VelocityTracker() }
    var prevCardHeight = remember { minCardHeight }
    var cardExpanded by remember { mutableStateOf(false) }
    var offsetWithinDraggableZone = remember { true }
    lateinit var layoutCoordinates: LayoutCoordinates

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    viewModel.updateCanvasSize(size.toSize())
                }
                .pointerInput(Unit) {
                    detectDragGestures (
                        onDrag = { _, dragAmount ->
                            viewModel.pan(dragAmount)
                        }
                    )
                }
        ) {
            for (instruction in viewModel.drawInstructions){ // TODO: drawline is temporary. Should use path probably
                drawLine(
                    start = instruction.startOffset,
                    end = instruction.endOffset,
                    color = instruction.color,
                    strokeWidth = instruction.width
                )
            }
        }
        Card(
            elevation = CardDefaults.cardElevation(0.dp),
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .onGloballyPositioned { layoutCoordinates = it }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            offsetWithinDraggableZone =
                                with(density) { offset.y.toDp() } < minCardHeight
                            if (!offsetWithinDraggableZone) return@detectDragGestures
                            velocityTracker.resetTracking()
                        },
                        onDrag = { change, dragAmount ->
                            if (!offsetWithinDraggableZone) return@detectDragGestures
                            velocityTracker.addPosition(
                                change.uptimeMillis,
                                layoutCoordinates.localToScreen(change.position)
                            )
                            cardHeight = maxOf(
                                cardHeight - with(density) { dragAmount.y.toDp() },
                                minCardHeight
                            )
                        },
                        onDragEnd = {
                            if (!offsetWithinDraggableZone) return@detectDragGestures
                            val velocity = velocityTracker.calculateVelocity()
                            val flickVelocity = 900f
                            if (velocity.y > flickVelocity) cardHeight = minCardHeight
                            else if (velocity.y < flickVelocity * -1 && !cardExpanded && prevCardHeight != minCardHeight) {
                                cardHeight = prevCardHeight
                            }
                            cardExpanded = cardHeight != minCardHeight
                            if (cardExpanded) prevCardHeight = cardHeight
                        }
                    )
                }
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(minCardHeight)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(MaterialTheme.colorScheme.outline, Color.Transparent),
                            startY = 0f,
                            endY = with(density) { minCardHeight.toPx() }
                        )
                    )
            ) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    modifier = Modifier,
                    onClick = onSettingsTapped
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Placeholder"
                    )
                }
            }
            if (cardExpanded) {
                LineSettings()
            }
        }
    }
}


@Composable
fun LineSettings(modifier: Modifier = Modifier) {
    Text(modifier = modifier, text = "Placeholder")
}


@Preview
@Composable
private fun DiagramScreenPreview() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        DiagramScreen(
            {},
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}