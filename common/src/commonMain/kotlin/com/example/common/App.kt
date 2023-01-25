package com.example.common

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        var offset by remember { mutableStateOf(Offset.Zero) }
        var size by remember { mutableStateOf(100f) }
        var showCustomUrl by remember { mutableStateOf(false) }
        var url by remember { mutableStateOf("https://images.alphacoders.com/117/1173124.jpg") }
        var customUrl by remember { mutableStateOf(url) }
        Surface {
            Scaffold(
                bottomBar = {
                    Column {
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
                        ListItem(
                            headlineText = { Text("Url") },
                            supportingText = {
                                OutlinedTextField(
                                    value = customUrl,
                                    onValueChange = { customUrl = it },
                                    label = { Text("Url") },
                                    trailingIcon = {
                                        IconButton(onClick = { url = customUrl }) { Icon(Icons.Default.Send, null) }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = showCustomUrl
                                )
                            },
                            trailingContent = {
                                Switch(
                                    showCustomUrl,
                                    onCheckedChange = { showCustomUrl = it }
                                )
                            }
                        )
                    }
                }
            ) { p ->
                Crossfade(showCustomUrl) { target ->
                    when (target) {
                        true -> {
                            KamelImage(
                                resource = lazyPainterResource(url),
                                contentDescription = null,
                                onLoading = { progress -> CircularProgressIndicator(progress) },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(p),
                                crossfade = true
                            )
                        }

                        false -> {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(p)
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
                ShowBehind(
                    size = size,
                    offset = offset,
                    offsetChange = { offset += it },
                    modifier = Modifier.fillMaxSize()
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
    modifier: Modifier = Modifier,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    borderWidth: Dp = 2.dp,
    dragEnabled: Boolean = true,
    matcher: PointerMatcher = PointerMatcher.Primary,
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {}
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
                .drag(
                    offset = offset,
                    offsetChange = offsetChange,
                    enabled = dragEnabled,
                    matcher = matcher,
                    onDragStart = onDragStart,
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel
                )
                .border(borderWidth, borderColor, CircleShape)
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