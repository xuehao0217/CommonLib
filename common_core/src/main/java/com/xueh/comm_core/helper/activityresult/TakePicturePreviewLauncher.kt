package com.xueh.comm_core.helper.activityresult

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.result.contract.ActivityResultContracts


class TakePicturePreviewLauncher :
    BasePictureLauncher<Void, Bitmap>(ActivityResultContracts.TakePicturePreview()) {
    private var onSuccess: (bitmap: Bitmap) -> Unit = {}

    /**
     * 打开相机拍照，无需手动请求权限，内部已请求
     * [onSuccess] 成功回调 Bitmap
     */
    fun lunch(
        onSuccess: (bitmap: Bitmap) -> Unit = {}
    ) {
        this.onSuccess = onSuccess
        permission.lunch(arrayOf(Manifest.permission.CAMERA)) {
            launcher.launch(null)
        }
    }

    override fun onActivityResult(result: Bitmap?) {
        result?.let(onSuccess)
    }
}