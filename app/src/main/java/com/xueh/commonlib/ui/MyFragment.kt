package com.xueh.commonlib.ui

import android.Manifest
import android.os.Bundle
import com.dylanc.activityresult.launcher.*
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.utils.time.Interval
import com.xueh.commonlib.databinding.FragmentMyBinding
import java.util.concurrent.TimeUnit


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class MyFragment : DFragment<FragmentMyBinding>() {
    private val requestMultiplePermissionsLauncher = RequestMultiplePermissionsLauncher(this)

    private val cropPictureLauncher = CropPictureLauncher(this)
    private val takePictureLauncher = TakePictureLauncher(this)
    private val requestPermissionLauncher = RequestPermissionLauncher(this)
    private val pickContentLauncher = PickContentLauncher(this)
    override fun initListener() {
        with(binding) {
            tvAlbum.setOnClickListener {
                showState(UiStatus.CONTENT)
                pickContentLauncher.launchForImage(
                    onActivityResult = { uri ->
                        binding.ivMy.setImageURI(uri)
                    },
                    onPermissionDenied = { settingsLauncher ->

                    },
                    onExplainRequestPermission = {

                    }
                )
            }
            tvCarema.setOnClickListener {
                requestMultiplePermissionsLauncher.launch(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    onAllGranted = {
                        showState(UiStatus.CONTENT)
                        takePictureLauncher.launch { uri ->
                            if (uri != null) {
                                cropPictureLauncher.launch(uri) { croppedUri ->
                                    activity?.contentResolver?.delete(uri, null, null)
                                    binding.ivMy.setImageURI(croppedUri)
                                }
                            }
                        }
                    },
                    onDenied = { list, settingsLauncher ->
                    },
                    onExplainRequest = { list ->
                    }
                )

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

    override fun initData() {

    }

    private lateinit var interval: Interval
    override fun initView(savedInstanceState: Bundle?) {
        //调用这个方法就会默认展示loading 需要show content
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

}