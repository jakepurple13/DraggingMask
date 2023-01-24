package com.example.common

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        var offset by remember { mutableStateOf(Offset.Zero) }
        var size by remember { mutableStateOf(100f) }
        Surface {
            Scaffold(
                bottomBar = {
                    ListItem(
                        headlineText = { Text(size.toString()) },
                        supportingText = {
                            Slider(
                                value = size,
                                onValueChange = { size = it },
                                valueRange = 50f..900f,
                            )
                        }
                    )
                }
            ) { p ->
                ShowBehind(
                    size = size,
                    offset = offset,
                    offsetChange = { offset += it },
                    modifier = Modifier
                        .padding(p)
                        .fillMaxSize()
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color.Blue,
                                    Color.Red,
                                    Color.Green,
                                    Color.Cyan,
                                    Color.Magenta,
                                    Color.Yellow
                                )
                            )
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ShowBehind(
    size: Float,
    offset: Offset = Offset.Zero,
    offsetChange: (Offset) -> Unit = {},
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(Modifier.fillMaxSize()) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                // Destination
                drawRect(surfaceColor)

                // Source
                drawCircle(
                    color = Color.Transparent,
                    radius = size / 2f,
                    center = offset + Offset(size / 2f, size / 2f),
                    blendMode = BlendMode.DstIn
                )
                restoreToCount(checkPoint)
            }
        }
        Box(
            modifier = Modifier
                .drag(offset, offsetChange = offsetChange)
                .border(2.dp, Color.Cyan, CircleShape)
                .size(size.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Modifier.drag(
    offset: Offset,
    offsetChange: (Offset) -> Unit,
    enabled: Boolean = true,
    matcher: PointerMatcher = PointerMatcher.Primary,
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {}
) = offset { IntOffset(offset.x.toInt(), offset.y.toInt()) }
    .onDrag(
        enabled = enabled,
        matcher = matcher,
        onDragStart = onDragStart,
        onDragCancel = onDragCancel,
        onDragEnd = onDragEnd,
    ) { offsetChange(it) }