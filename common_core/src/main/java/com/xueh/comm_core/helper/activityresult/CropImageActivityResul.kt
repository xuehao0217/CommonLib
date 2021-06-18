package com.xueh.comm_core.helper.activityresult

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContract
import java.io.File

/**
 * 创 建 人: xueh
 * 创建日期: 2021/6/18 15:12
 * 备注：
 */

class CropImageActivityResul : ActivityResultContract<CropImageResult, Uri>() {
    var outUri: Uri? = null

    //构建意图
    override fun createIntent(context: Context, input: CropImageResult): Intent {
        //把CropImageResult转换成裁剪图片的意图
        val intent = Intent("com.android.camera.action.CROP")
        val mimeType = context.contentResolver.getType(input.uri)
        val imageName = "${System.currentTimeMillis()}.${
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }"
        outUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            //PathUtils.getExternalAppCachePath()
            Uri.fromFile(File(context.externalCacheDir!!.absolutePath, imageName))
        }
        context.grantUriPermission(
            context.packageName,
            outUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra("noFaceDetection", true) //去除默认的人脸识别，否则和剪裁匡重叠
        intent.setDataAndType(input.uri, mimeType)
        intent.putExtra("crop", "true") // crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("output", outUri)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()) // 返回格式
        intent.putExtra("return-data", true)


        if (input.outputX != 0 && input.outputY != 0) {
            intent.putExtra("outputX", input.outputX)
            intent.putExtra("outputY", input.outputY)
        }
        if (input.aspectX != 0 && input.aspectY != 0) {
            if (input.aspectY == input.aspectX && Build.MANUFACTURER == "HUAWEI") {
                intent.putExtra("aspectX", 9999)
                intent.putExtra("aspectY", 9998)
            } else {
                intent.putExtra("aspectX", input.aspectX)
                intent.putExtra("aspectY", input.aspectY)
            }
        }

        return intent
    }

    //接收意图并处理数据
    override fun parseResult(resultCode: Int, intent: Intent?): Uri {
        if (outUri != null)
            return outUri!!
        else
            return Uri.parse("")
    }

}

/**
 * uri:需要裁剪的图片
 * aspect:长宽比例
 * output:图片输出长宽
 * uri 要裁剪的图片
 * aspect 剪裁比例
 * output 输入图片长宽
 */
class CropImageResult(
    val uri: Uri,
    val aspectX: Int = 1,
    val aspectY: Int = 1,
    @androidx.annotation.IntRange(from = 0, to = 1080)
    val outputX: Int = 350,
    @androidx.annotation.IntRange(from = 0, to = 1080)
    val outputY: Int = 350
)
