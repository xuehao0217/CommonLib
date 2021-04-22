package com.xueh.commonlib.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.utils.TakePictureUtils
import com.xueh.commonlib.R
import com.xueh.commonlib.databinding.FragmentMyBinding
import java.io.File


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class MyFragment : DFragment<FragmentMyBinding>() {
    lateinit var takePictureUtils: TakePictureUtils

    override fun initListener() {
        with(binding) {
            tvAlbum.setOnClickListener {
                takePictureUtils.startTakeWayByAlbum()
            }
            tvCarema.setOnClickListener {
                takePictureUtils.startTakeWayByCarema()
            }
            tvLoading.setOnClickListener {
                showState(UiStatus.LOADING)
            }
            tvContent.setOnClickListener {
                showState(UiStatus.CONTENT)
            }
        }

    }

    override fun initDataAfterView() {
    }

    override fun initView(savedInstanceState: Bundle?) {
        bindStateView(binding.ivMy)
        takePictureUtils =
            TakePictureUtils(this, object : TakePictureUtils.takePictureCallBackListener {
                override fun failed(errorCode: Int, deniedPermissions: MutableList<String>?) {
                }

                override fun successful(isTailor: Boolean, outFile: File?, filePath: Uri?) {
                    binding.ivMy.setImageURI(Uri.fromFile(outFile?.absoluteFile))
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        takePictureUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takePictureUtils.attachToActivityForResult(requestCode, resultCode, data)
    }

}