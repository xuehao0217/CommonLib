package com.xueh.comm_core.helper.activityresult

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import com.xueh.comm_core.helper.no
import com.xueh.comm_core.helper.yes

/**
 * 创 建 人: xueh
 * 创建日期: 2021/7/5 11:28
 * 备注：图库选择图片 successBlock返回图片URI
 */
class AlbumLauncher(
    var isCrop: Boolean = true,
    var imgName: String = "pictureSaveImg.${Bitmap.CompressFormat.PNG}"
) : BasePictureLauncher<Void, Uri>(object : ActivityResultContract<Void, Uri>() {
    override fun createIntent(context: Context, input: Void?) =
        Intent(Intent.ACTION_PICK, null).apply {
            setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
        }

    override fun parseResult(resultCode: Int, intent: Intent?) =
        if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
}) {
    private var successBlock: (Uri) -> Unit = {}
    fun lunch(block: (Uri) -> Unit) {
        permission.lunch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            successBlock = block
            launcher.launch(null)
        }
    }

    override fun onActivityResult(result: Uri?) {
        super.onActivityResult(result)
        result?.let {
            isCrop.yes {
                cropImage.lunch(it) { cropImage ->
                    successBlock.invoke(cropImage)
                }
            }
            isCrop.no {
                successBlock.invoke(it)
            }

        }
    }
}