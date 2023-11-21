package com.xueh.comm_core.helper.activityresult

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.Utils
import java.io.File


/**
 * 创 建 人: xueh
 * 创建日期: 2023/9/13
 * 备注：
 */
class TakeCameraUri : ActivityResultContract<Any?, Uri?>() {
    //拍照返回的uri
    private var uri: Uri? = null
    override fun createIntent(context: Context, input: Any?): Intent {
        val mimeType = "image/jpeg"
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            context.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            //应用认证表示
            val authorities = Utils.getApp().packageName + ".utilcode.provider"
            FileProvider.getUriForFile(
                context, authorities,
                File(context.externalCacheDir!!.absolutePath, fileName)
            )
        }
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
    }

    override fun parseResult(resultCode: Int, @Nullable intent: Intent?): Uri? {
        return uri
    }
}
