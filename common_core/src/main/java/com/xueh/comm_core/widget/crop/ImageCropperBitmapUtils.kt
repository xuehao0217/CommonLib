package com.xueh.comm_core.widget.crop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.compose.ui.geometry.Rect
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

/** 方形裁剪预览图长边上界（像素），与解码全图分离以降低界面内存。 */
const val SQUARE_CROP_DEFAULT_PREVIEW_MAX_LONG_EDGE_PX: Int = 1800

/**
 * 从 [uri] 解码：`EXIF` 转正后的 **全分辨率 [source]**，以及长边不超过 [previewMaxLongEdgePx] 的 **[preview]**。
 *
 * 调用方持有 [Pair] 并对其中 [Bitmap] 在适当时机 [Bitmap.recycle]；失败返回 null。
 */
suspend fun loadBitmapPairForSquareCrop(
    context: Context,
    uri: Uri,
    previewMaxLongEdgePx: Int = SQUARE_CROP_DEFAULT_PREVIEW_MAX_LONG_EDGE_PX,
): Pair<Bitmap, Bitmap>? = withContext(Dispatchers.IO) {
    loadBitmapPairForSquareCropBlocking(context, uri, previewMaxLongEdgePx)
}

/** 同步版，请在后台线程调用。 */
fun loadBitmapPairForSquareCropBlocking(
    context: Context,
    uri: Uri,
    previewMaxLongEdgePx: Int = SQUARE_CROP_DEFAULT_PREVIEW_MAX_LONG_EDGE_PX,
): Pair<Bitmap, Bitmap>? {
    val rotationDegrees = context.contentResolver.openInputStream(uri)?.use { stream ->
        ExifInterface(stream).rotationDegrees
    } ?: 0

    val decoded = decodeFullBitmapForCrop(context, uri) ?: return null

    val source = applyExifRotation(decoded, rotationDegrees)
    val preview = downsampleLongEdgeForPreview(source, previewMaxLongEdgePx)
    return source to preview
}

private fun applyExifRotation(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
    if (rotationDegrees == 0) return bitmap
    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
    val out = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    if (out != bitmap) bitmap.recycle()
    return out
}

private fun downsampleLongEdgeForPreview(source: Bitmap, maxLongEdge: Int): Bitmap {
    val w = source.width
    val h = source.height
    val longEdge = max(w, h)
    if (longEdge <= maxLongEdge) {
        return source.copy(source.config ?: Bitmap.Config.ARGB_8888, false)
    }
    val scale = maxLongEdge.toFloat() / longEdge
    val nw = max((w * scale).toInt(), 1)
    val nh = max((h * scale).toInt(), 1)
    return Bitmap.createScaledBitmap(source, nw, nh, true)
}

/**
 * 用全分辨率 [source]，按界面上的裁剪框与图片显示区域映射到像素并 [Bitmap.createBitmap] 截取。
 *
 * [cropFrameInDisplay]、[displayedImageRect] 为同一显示坐标系（与 [SquareImageCropperDialog] 内逻辑一致）。
 */
fun cropBitmapFromDisplayMapping(
    source: Bitmap,
    cropFrameInDisplay: Rect,
    displayedImageRect: Rect,
): Bitmap {
    val imgW = source.width.toFloat()
    val imgH = source.height.toFloat()
    val scaleX = imgW / max(displayedImageRect.width, 0.001f)
    val scaleY = imgH / max(displayedImageRect.height, 0.001f)

    val originX = (cropFrameInDisplay.left - displayedImageRect.left) * scaleX
    val originY = (cropFrameInDisplay.top - displayedImageRect.top) * scaleY
    val cropW = cropFrameInDisplay.width * scaleX
    val cropH = cropFrameInDisplay.height * scaleY

    var x = floor(originX).toInt()
    var y = floor(originY).toInt()
    var w = ceil(cropW).toInt()
    var h = ceil(cropH).toInt()

    x = x.coerceIn(0, max(source.width - 1, 0))
    y = y.coerceIn(0, max(source.height - 1, 0))
    w = w.coerceIn(1, source.width - x)
    h = h.coerceIn(1, source.height - y)

    return Bitmap.createBitmap(source, x, y, w, h)
}

/**
 * 解码整图供裁剪：[ImageDecoder] + **软件分配**，避免 HARDWARE 位图无法 [Bitmap.createBitmap] 子区域；
 * 失败时退回 [BitmapFactory.decodeStream]。
 */
private fun decodeFullBitmapForCrop(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(context.contentResolver, uri),
            ) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        }
    } catch (_: Throwable) {
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
    }
}
