package com.xueh.comm_core.widget.crop

import android.graphics.Bitmap
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.max
import kotlin.math.min

/** 文案配置（便于宿主接入多语言）。 */
data class SquareImageCropperLabels(
    val title: String = "裁剪图片",
    val cancel: String = "取消",
    val confirm: String = "选用",
    val closeContentDescription: String = "关闭",
)

/** 尺寸与边距。 */
data class SquareImageCropperLayout(
    val horizontalPadding: Dp = 20.dp,
    /** 裁剪框最小边长（短边设备会自动夹逼）。 */
    val minCropSize: Dp = 241.dp,
    val cornerRadius: Dp = 10.dp,
    val bottomBarPaddingBottom: Dp = 47.dp,
    val topBarPaddingTop: Dp = 12.dp,
)

/** 颜色配置。 */
data class SquareImageCropperStyle(
    val scrimBackground: Color = Color.Black.copy(alpha = 0.92f),
    val outsideCropDim: Color = Color.Black.copy(alpha = 0.6f),
    val cropBorder: Color = Color.White.copy(alpha = 0.3f),
    val cornerLine: Color = Color.White,
    val barContent: Color = Color.White,
)

/** [SquareImageCropperDialog] 外观与文案。 */
data class SquareImageCropperConfig(
    val labels: SquareImageCropperLabels = SquareImageCropperLabels(),
    val layout: SquareImageCropperLayout = SquareImageCropperLayout(),
    val style: SquareImageCropperStyle = SquareImageCropperStyle(),
)

/**
 * 方形区域裁切全屏对话框：双指缩放裁剪框、拖框 / 拖图；确认时从 [sourceBitmap] 按显示映射导出子图。
 *
 * **内存**：调用方提供已解码的 [sourceBitmap] 与 [previewBitmap]（通常来自 [loadBitmapPairForSquareCrop]），
 * 关闭或确认后由调用方 [Bitmap.recycle] 释放二者；[cropBitmapFromDisplayMapping] 生成的新图由 [onCropped] 接收并归调用方管理。
 */
@Composable
fun SquareImageCropperDialog(
    sourceBitmap: Bitmap,
    previewBitmap: Bitmap,
    onDismiss: () -> Unit,
    onCropped: (Bitmap) -> Unit,
    config: SquareImageCropperConfig = SquareImageCropperConfig(),
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        // 根节点必须铺满窗口；部分导航/嵌套场景下仅靠子级 fillMaxSize 可能拿不到全屏约束
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = config.style.scrimBackground,
        ) {
            SquareImageCropperContent(
                sourceBitmap = sourceBitmap,
                previewBitmap = previewBitmap,
                config = config,
                onDismiss = onDismiss,
                onCropped = onCropped,
            )
        }
    }
}

@Composable
private fun SquareImageCropperContent(
    sourceBitmap: Bitmap,
    previewBitmap: Bitmap,
    config: SquareImageCropperConfig,
    onDismiss: () -> Unit,
    onCropped: (Bitmap) -> Unit,
) {
    val density = LocalDensity.current
    val horizontalPaddingPx = with(density) { config.layout.horizontalPadding.toPx() }
    val minCropPx = with(density) { config.layout.minCropSize.toPx() }
    val cornerRadiusDp = config.layout.cornerRadius
    val cornerRadiusPx = with(density) { cornerRadiusDp.toPx() }
    val strokeBorderPx = with(density) { 1.dp.toPx() }
    val strokeCornerPx = with(density) { 2.dp.toPx() }

    val onDismissUpdated = rememberUpdatedState(onDismiss)
    val onCroppedUpdated = rememberUpdatedState(onCropped)
    val sourceUpdated = rememberUpdatedState(sourceBitmap)

    var imageOffset by remember(previewBitmap) { mutableStateOf(Offset.Zero) }
    var cropOffset by remember(previewBitmap) { mutableStateOf(Offset.Zero) }
    var cropSize by remember(previewBitmap) { mutableFloatStateOf(minCropPx) }
    var didSetupInitialLayout by remember(previewBitmap) { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val maxWpx = with(density) { maxWidth.toPx() }
            val maxHpx = with(density) { maxHeight.toPx() }
            val contentWidthPx = maxOf(maxWpx - horizontalPaddingPx * 2f, 1f)
            val aspect =
                max(previewBitmap.width, 1).toFloat() / max(previewBitmap.height, 1).toFloat()
            val imageDisplayHeightPx = contentWidthPx / max(aspect, 0.001f)
            val imageDisplaySize = Size(contentWidthPx, imageDisplayHeightPx)
            val screenCenter = Offset(maxWpx / 2f, maxHpx / 2f)

            LaunchedEffect(contentWidthPx, imageDisplayHeightPx, previewBitmap) {
                didSetupInitialLayout = false
            }

            LaunchedEffect(contentWidthPx, imageDisplayHeightPx, previewBitmap, didSetupInitialLayout) {
                if (!didSetupInitialLayout && contentWidthPx > 0f && imageDisplayHeightPx > 0f) {
                    val maxSize = min(contentWidthPx, imageDisplayHeightPx)
                    cropSize = min(minCropPx, maxSize)
                    imageOffset = Offset.Zero
                    cropOffset = Offset.Zero
                    didSetupInitialLayout = true
                }
            }

            fun clampAll(): Triple<Float, Offset, Offset> {
                val maxSize = min(contentWidthPx, imageDisplaySize.height)
                val minSize = min(minCropPx, maxSize)
                var cs = cropSize.coerceIn(minSize, maxSize)

                val imgRect = Rect(
                    offset = Offset(
                        screenCenter.x - imageDisplaySize.width / 2f + imageOffset.x,
                        screenCenter.y - imageDisplaySize.height / 2f + imageOffset.y,
                    ),
                    size = imageDisplaySize,
                )

                val half = cs / 2f
                var co = cropOffset
                co = Offset(
                    x = co.x.coerceIn(
                        imgRect.left + half - screenCenter.x,
                        imgRect.right - half - screenCenter.x,
                    ),
                    y = co.y.coerceIn(
                        imgRect.top + half - screenCenter.y,
                        imgRect.bottom - half - screenCenter.y,
                    ),
                )

                val cropCenterCalc = Offset(screenCenter.x + co.x, screenCenter.y + co.y)
                val cropRect = Rect(
                    offset = Offset(cropCenterCalc.x - half, cropCenterCalc.y - half),
                    size = Size(cs, cs),
                )

                val baseRect = Rect(
                    offset = Offset(
                        screenCenter.x - imageDisplaySize.width / 2f,
                        screenCenter.y - imageDisplaySize.height / 2f,
                    ),
                    size = imageDisplaySize,
                )

                val minX = cropRect.right - baseRect.right
                val maxX = cropRect.left - baseRect.left
                val minY = cropRect.bottom - baseRect.bottom
                val maxY = cropRect.top - baseRect.top

                var io = imageOffset
                io = Offset(
                    x = if (minX > maxX) (minX + maxX) / 2f else io.x.coerceIn(minX, maxX),
                    y = if (minY > maxY) (minY + maxY) / 2f else io.y.coerceIn(minY, maxY),
                )

                return Triple(cs, co, io)
            }

            fun applyClamp() {
                val (cs, co, io) = clampAll()
                cropSize = cs
                cropOffset = co
                imageOffset = io
            }

            LaunchedEffect(contentWidthPx, imageDisplayHeightPx, previewBitmap, didSetupInitialLayout) {
                if (didSetupInitialLayout) {
                    applyClamp()
                }
            }

            val imgCenter = Offset(
                screenCenter.x + imageOffset.x,
                screenCenter.y + imageOffset.y,
            )
            val topLeftImage = Offset(
                imgCenter.x - imageDisplaySize.width / 2f,
                imgCenter.y - imageDisplaySize.height / 2f,
            )

            val cropCenter = Offset(screenCenter.x + cropOffset.x, screenCenter.y + cropOffset.y)
            val cropRect = Rect(
                offset = Offset(cropCenter.x - cropSize / 2f, cropCenter.y - cropSize / 2f),
                size = Size(cropSize, cropSize),
            )

            val cropRectForHitState = rememberUpdatedState(cropRect)
            val cropOffsetState = rememberUpdatedState(cropOffset)
            val imageOffsetState = rememberUpdatedState(imageOffset)
            val cropSizeForPinchState = rememberUpdatedState(cropSize)

            Box(Modifier.fillMaxSize()) {
                Image(
                    bitmap = previewBitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                topLeftImage.x.toInt(),
                                topLeftImage.y.toInt(),
                            )
                        }
                        .size(
                            width = with(density) { imageDisplaySize.width.toDp() },
                            height = with(density) { imageDisplaySize.height.toDp() },
                        ),
                )

                Canvas(Modifier.fillMaxSize()) {
                    val dimPath = Path().apply {
                        fillType = PathFillType.EvenOdd
                        addRect(Rect(0f, 0f, size.width, size.height))
                        addOval(cropRect)
                    }
                    drawPath(dimPath, config.style.outsideCropDim)

                    val borderPath = Path().apply {
                        addRoundRect(
                            RoundRect(
                                cropRect,
                                cornerRadiusPx,
                                cornerRadiusPx,
                            ),
                        )
                    }
                    drawPath(
                        borderPath,
                        color = config.style.cropBorder,
                        style = Stroke(width = strokeBorderPx),
                    )

                    val cornersAndroid = cropCornerHandlesPath(cropSize)
                    val cornerPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = strokeCornerPx
                        strokeCap = android.graphics.Paint.Cap.ROUND
                        strokeJoin = android.graphics.Paint.Join.ROUND
                        isAntiAlias = true
                    }
                    translate(left = cropRect.left, top = cropRect.top) {
                        drawContext.canvas.nativeCanvas.drawPath(cornersAndroid, cornerPaint)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(screenCenter, contentWidthPx, imageDisplaySize, minCropPx) {
                            forEachGesture {
                                var pinchBase = 0f
                                var pinchZoom = 1f
                                detectTransformGestures { _, _, zoomChange, _ ->
                                    if (pinchBase == 0f) {
                                        pinchBase = cropSizeForPinchState.value
                                        pinchZoom = 1f
                                    }
                                    pinchZoom *= zoomChange
                                    val maxSize = min(contentWidthPx, imageDisplaySize.height)
                                    val minSize = min(minCropPx, maxSize)
                                    cropSize = (pinchBase * pinchZoom).coerceIn(minSize, maxSize)
                                    val (cs, co, io) = clampAll()
                                    cropSize = cs
                                    cropOffset = co
                                    imageOffset = io
                                }
                            }
                        },
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(screenCenter, contentWidthPx, imageDisplaySize, minCropPx) {
                            var dragTarget: DragTarget? = null
                            var startCrop = Offset.Zero
                            var startImage = Offset.Zero
                            var totalDrag = Offset.Zero
                            detectDragGestures(
                                onDragStart = { down ->
                                    val rect = cropRectForHitState.value
                                    dragTarget =
                                        if (rect.contains(down)) DragTarget.Crop else DragTarget.Image
                                    startCrop = cropOffsetState.value
                                    startImage = imageOffsetState.value
                                    totalDrag = Offset.Zero
                                },
                                onDrag = { _, dragAmount ->
                                    totalDrag += dragAmount
                                    when (dragTarget) {
                                        DragTarget.Crop -> {
                                            cropOffset = Offset(
                                                startCrop.x + totalDrag.x,
                                                startCrop.y + totalDrag.y,
                                            )
                                        }
                                        DragTarget.Image -> {
                                            imageOffset = Offset(
                                                startImage.x + totalDrag.x,
                                                startImage.y + totalDrag.y,
                                            )
                                        }
                                        null -> Unit
                                    }
                                    val (cs, co, io) = clampAll()
                                    cropSize = cs
                                    cropOffset = co
                                    imageOffset = io
                                },
                                onDragEnd = { dragTarget = null },
                                onDragCancel = { dragTarget = null },
                            )
                        },
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = config.layout.topBarPaddingTop),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(44.dp))
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = config.labels.title,
                    color = config.style.barContent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onDismissUpdated.value,
                    modifier = Modifier.size(44.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = config.labels.closeContentDescription,
                        tint = config.style.barContent,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = config.layout.bottomBarPaddingBottom),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = onDismissUpdated.value,
                    modifier = Modifier.padding(start = 24.dp),
                ) {
                    Text(
                        text = config.labels.cancel,
                        color = config.style.barContent,
                        fontSize = 17.sp,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {
                        val imgC = Offset(
                            screenCenter.x + imageOffset.x,
                            screenCenter.y + imageOffset.y,
                        )
                        val displayedImageRect = Rect(
                            offset = Offset(
                                imgC.x - imageDisplaySize.width / 2f,
                                imgC.y - imageDisplaySize.height / 2f,
                            ),
                            size = imageDisplaySize,
                        )
                        val half = cropSize / 2f
                        val cropFrame = Rect(
                            offset = Offset(
                                cropCenter.x - half,
                                cropCenter.y - half,
                            ),
                            size = Size(cropSize, cropSize),
                        )
                        val cropped = cropBitmapFromDisplayMapping(
                            sourceUpdated.value,
                            cropFrame,
                            displayedImageRect,
                        )
                        onCroppedUpdated.value(cropped)
                    },
                    modifier = Modifier.padding(end = 24.dp),
                ) {
                    Text(
                        text = config.labels.confirm,
                        color = config.style.barContent,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

private enum class DragTarget { Crop, Image }

/** 四角「L」形把手路径（与常见相机裁切 UI 一致），坐标系 0..[size]。 */
private fun cropCornerHandlesPath(size: Float): android.graphics.Path {
    val path = android.graphics.Path()
    val cl = 60f
    val cr = 10f
    val w = size
    val h = size

    path.moveTo(0f, cl)
    path.lineTo(0f, cr)
    path.arcTo(RectF(0f, 0f, 2 * cr, 2 * cr), 180f, 90f, false)
    path.lineTo(cl, 0f)

    path.moveTo(w - cl, 0f)
    path.lineTo(w - cr, 0f)
    path.arcTo(RectF(w - 2 * cr, 0f, w, 2 * cr), 270f, 90f, false)
    path.lineTo(w, cl)

    path.moveTo(w, h - cl)
    path.lineTo(w, h - cr)
    path.arcTo(RectF(w - 2 * cr, h - 2 * cr, w, h), 0f, 90f, false)
    path.lineTo(w - cl, h)

    path.moveTo(cl, h)
    path.lineTo(cr, h)
    path.arcTo(RectF(0f, h - 2 * cr, 2 * cr, h), 90f, 90f, false)
    path.lineTo(0f, h - cl)

    return path
}
