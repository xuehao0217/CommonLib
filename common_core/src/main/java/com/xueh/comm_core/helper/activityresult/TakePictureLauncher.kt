package com.xueh.comm_core.helper.activityresult

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.PathUtils
import com.xueh.comm_core.helper.getUriForFile
import com.xueh.comm_core.helper.no
import com.xueh.comm_core.helper.yes
import java.io.File

class TakePictureLauncher(
    var isCrop: Boolean = true,
    var imgName: String = "pictureSaveImg.${Bitmap.CompressFormat.PNG}",
) :
    BasePictureLauncher<Uri, Boolean>(ActivityResultContracts.TakePicture()) {
    private var onSuccess: (path: Uri) -> Unit = {}
    private var onError: (path: Uri) -> Unit = {}
    private var uri = File(PathUtils.getExternalAppCachePath(), imgName).getUriForFile()

    /**
     * 打开相机拍照，无需手动请求权限，内部已请求
     * [onSuccess] 成功回调，返回图片路径
     */
    fun lunch(
        onSuccess: (path: Uri) -> Unit
    ) {
        this.onError = onError
        this.onSuccess = onSuccess

        permission.lunch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            launcher.launch(uri)
        }
    }

    override fun onActivityResult(result: Boolean?) {
        super.onActivityResult(result)
        result?.yes {
            isCrop.yes {
                cropImage.lunch(uri) {
                    onSuccess.invoke(it)
                }
            }
            isCrop.no {
                onSuccess.invoke(uri)
            }
        }
    }
}