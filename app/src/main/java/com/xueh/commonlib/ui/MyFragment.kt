package com.xueh.commonlib.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.blankj.utilcode.util.ToastUtils
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.helper.takePicture
import com.xueh.comm_core.helper.takePicturePreview
import com.xueh.comm_core.utils.TakePictureUtils
import com.xueh.comm_core.utils.time.Interval
import com.xueh.commonlib.databinding.FragmentMyBinding
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class MyFragment : DFragment<FragmentMyBinding>() {
    val takePictureUtils by lazy {
        TakePictureUtils(this, object : TakePictureUtils.takePictureCallBackListener {
            override fun failed(errorCode: Int, deniedPermissions: MutableList<String>?) {
            }

            override fun successful(isTailor: Boolean, outFile: File?, filePath: Uri?) {
                binding.ivMy.setImageURI(Uri.fromFile(outFile?.absoluteFile))
            }
        })
    }

    override fun initListener() {
        with(binding) {
            tvAlbum.setOnClickListener {
                takePictureUtils.startTakeWayByAlbum()
            }
            tvCarema.setOnClickListener {
//                takePictureUtils.startTakeWayByCarema()
                activity?.takePicturePreview {
                    showState(UiStatus.CONTENT)
                    binding.ivMy.setImageBitmap(it)
                }
            }
            btStartTime.setOnClickListener {
                interval.start()
            }
            btStartPause.setOnClickListener {
                interval.pause()
            }
            btStartResume.setOnClickListener {
                interval.resume()
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

    private lateinit var interval: Interval
    override fun initView(savedInstanceState: Bundle?) {
        bindStateView(binding.ivMy)
        interval = Interval(
            0,
            1,
            TimeUnit.SECONDS,
            20
        ).life(this) // 自定义计数器个数的轮循器, 当[start]]比[end]值大, 且end不等于-1时, 即为倒计时
        // interval = Interval(1, TimeUnit.SECONDS) // 每秒回调一次, 不会自动结束
        interval.subscribe {
            binding.tvTime.text = it.toString()
        }.finish {
            binding.tvTime.text = "计时完成"
        }
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