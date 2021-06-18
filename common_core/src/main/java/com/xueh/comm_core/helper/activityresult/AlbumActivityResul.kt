package com.xueh.comm_core.helper.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

/**
 * 创 建 人: xueh
 * 创建日期: 2021/6/18 16:46
 * 备注：
 */
class AlbumActivityResul : ActivityResultContract<Void, Uri>() {
    override fun createIntent(context: Context, input: Void?) =
        Intent(Intent.ACTION_PICK, null).apply {
            setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
        }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }
}