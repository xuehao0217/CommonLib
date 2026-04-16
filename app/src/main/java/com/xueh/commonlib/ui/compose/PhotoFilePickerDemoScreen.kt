package com.xueh.commonlib.ui.compose

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.xueh.comm_core.widget.crop.SquareImageCropperDialog
import com.xueh.comm_core.widget.crop.loadBitmapPairForSquareCrop
import java.io.File
import kotlinx.coroutines.launch

/**
 * 相册与文件选择：`PickVisualMedia` / `PickMultipleVisualMedia` 走系统 Photo Picker；
 * `OpenDocument` 选通用文件；拍照使用 [ActivityResultContracts.TakePicture]；单图可选 [SquareImageCropperDialog] 方形裁剪（common_core 封装）。
 */
@Composable
fun PhotoFilePickerDemoScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var singleImageUri by remember { mutableStateOf<Uri?>(null) }
    var multiImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var documentUri by remember { mutableStateOf<Uri?>(null) }
    var lastMime by remember { mutableStateOf<String?>(null) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    var squareCropPair by remember { mutableStateOf<Pair<Bitmap, Bitmap>?>(null) }
    var squareCroppedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var squareCropLoading by remember { mutableStateOf(false) }

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            pendingCaptureUri?.let { cameraUri = it }
        }
        pendingCaptureUri = null
    }
    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            val out = createCaptureImageUri(context)
            if (out == null) {
                ToastUtils.showShort("无法创建拍照输出 Uri")
            } else {
                pendingCaptureUri = out
                takePicture.launch(out)
            }
        } else {
            ToastUtils.showShort("需要相机权限才能拍照演示")
        }
    }

    val pickSingleImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        squareCroppedBitmap?.recycle()
        squareCroppedBitmap = null
        singleImageUri = uri
    }

    val pickMultipleImages = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
    ) { uris ->
        multiImageUris = uris
    }

    val openDocument = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        documentUri = uri
        lastMime = uri?.let { context.contentResolver.getType(it) }
    }

    fun launchCamera() {
        val out = createCaptureImageUri(context)
        if (out == null) {
            ToastUtils.showShort("无法创建拍照输出 Uri")
            return
        }
        pendingCaptureUri = out
        takePicture.launch(out)
    }

    fun tryStartSquareCrop(uri: Uri?) {
        if (uri == null) {
            ToastUtils.showShort("请先选择或拍摄一张图片")
            return
        }
        if (squareCropLoading) return
        scope.launch {
            squareCropLoading = true
            try {
                ToastUtils.showShort("正在加载原图…")
                val pair = loadBitmapPairForSquareCrop(context, uri)
                if (pair == null) {
                    ToastUtils.showShort("解码失败：请换 JPEG/PNG，或检查相册权限后重试")
                } else {
                    squareCropPair = pair
                }
            } finally {
                squareCropLoading = false
            }
        }
    }

    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        DemoScreenIntro(
            text = "单图/多图使用 Photo Picker；「文件」使用 OpenDocument。拍照使用 TakePicture。" +
                "选图或拍照成功后，在同一区块点「方形裁剪」进入全屏裁切（会先 Toast「正在加载原图」）。若无任何反应请看是否解码失败 Toast。",
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Text("拍照（TakePicture）", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Text(
            text = "依赖 CAMERA 权限。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FilledTonalButton(
            onClick = {
                val ok = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
                if (ok) {
                    launchCamera()
                } else {
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("打开相机拍照")
        }
        cameraUri?.let { uri ->
            UriSummary(uri = uri, mime = context.contentResolver.getType(uri))
            AsyncImage(
                model = uri,
                contentDescription = "拍照结果",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit,
            )
            FilledTonalButton(
                onClick = { tryStartSquareCrop(uri) },
                enabled = !squareCropLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    if (squareCropLoading) {
                        "加载中…"
                    } else {
                        "方形裁剪（当前拍照）"
                    },
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Text("单张图片", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        FilledTonalButton(
            onClick = {
                pickSingleImage.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        .build(),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("PickVisualMedia（仅图片）")
        }
        singleImageUri?.let { uri ->
            UriSummary(uri = uri, mime = context.contentResolver.getType(uri))
            AsyncImage(
                model = uri,
                contentDescription = "单选预览",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit,
            )
            FilledTonalButton(
                onClick = { tryStartSquareCrop(uri) },
                enabled = !squareCropLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    if (squareCropLoading) {
                        "加载中…"
                    } else {
                        "方形裁剪（Compose 封装）"
                    },
                )
            }
        }
        squareCroppedBitmap?.let { bmp ->
            Text(
                "裁剪结果 ${bmp.width}×${bmp.height}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
            AsyncImage(
                model = bmp,
                contentDescription = "裁剪结果",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit,
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Text("多张图片（最多 5）", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        FilledTonalButton(
            onClick = {
                pickMultipleImages.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        .setMaxItems(5)
                        .build(),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("PickMultipleVisualMedia")
        }
        if (multiImageUris.isNotEmpty()) {
            Text(
                "已选 ${multiImageUris.size} 张",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(
                    items = multiImageUris,
                    key = { it.toString() },
                ) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "多选缩略",
                        modifier = Modifier.size(96.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Text("文件（文档选择器）", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        FilledTonalButton(
            onClick = {
                openDocument.launch(
                    arrayOf(
                        "image/*",
                        "video/*",
                        "application/pdf",
                        "text/plain",
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("OpenDocument（图/视频/PDF/文本）")
        }
        documentUri?.let { uri ->
            UriSummary(uri = uri, mime = lastMime)
            if (lastMime?.startsWith("image/") == true) {
                AsyncImage(
                    model = uri,
                    contentDescription = "文档预览（图）",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    squareCropPair?.let { (src, prev) ->
        SquareImageCropperDialog(
            sourceBitmap = src,
            previewBitmap = prev,
            onDismiss = {
                src.recycle()
                prev.recycle()
                squareCropPair = null
            },
            onCropped = { out ->
                src.recycle()
                prev.recycle()
                squareCropPair = null
                squareCroppedBitmap?.recycle()
                squareCroppedBitmap = out
                ToastUtils.showShort("裁剪完成 ${out.width}×${out.height}")
            },
        )
    }
}

/** 为 [ActivityResultContracts.TakePicture] 准备可写入的 content [Uri]。 */
private fun createCaptureImageUri(context: Context): Uri? {
    val mimeType = "image/jpeg"
    val fileName = "${System.currentTimeMillis()}.jpg"
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    } else {
        val cacheDir = context.externalCacheDir ?: return null
        val authorities = "${Utils.getApp().packageName}.utilcode.provider"
        val file = File(cacheDir.absolutePath, fileName)
        FileProvider.getUriForFile(context, authorities, file)
    }
}

@Composable
private fun UriSummary(uri: Uri, mime: String?) {
    Text(
        text = "Uri: $uri",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 4.dp),
    )
    Text(
        text = "MIME: ${mime ?: "（未知）"}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
