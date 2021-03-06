package com.xueh.commonlib.ui

import android.Manifest
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.UriUtils
import com.fengchen.uistatus.annotation.UiStatus
import com.xueh.comm_core.base.DFragment
import com.xueh.comm_core.helper.*
import com.xueh.comm_core.helper.activityresult.AlbumLauncher
import com.xueh.comm_core.helper.activityresult.TakePictureLauncher
import com.xueh.comm_core.helper.activityresult.TakePicturePreviewLauncher
import com.xueh.comm_core.helper.activityresult.permission.PermissionLauncher
import com.xueh.comm_core.utils.time.Interval
import com.xueh.commonlib.databinding.FragmentMyBinding
import java.util.concurrent.TimeUnit


/**
 * 创 建 人: xueh
 * 创建日期: 2019/11/29 13:29
 * 备注：
 */
class MyFragment : DFragment<FragmentMyBinding>() {

    private val pictureLauncher by lazy { TakePictureLauncher() }
    private val albumLauncher by lazy { AlbumLauncher() }
    private val previewLauncher by lazy { TakePicturePreviewLauncher() }
    private val permissionLauncher by lazy { PermissionLauncher() }
    
    override fun initListener() {
        with(binding) {
            tvAlbum.setOnClickListener {
                albumLauncher.lunch {
                    showState(UiStatus.CONTENT)
                    ToastUtils.showShort("${UriUtils.uri2File(it)}")
                    binding.ivMy.setImageURI(it)
                }
            }
            tvCarema.setOnClickListener {
                pictureLauncher.lunch {
                    showState(UiStatus.CONTENT)
                    ToastUtils.showShort("${UriUtils.uri2File(it)}")
                    binding.ivMy.setImageURI(it)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(pictureLauncher)
        lifecycle.addObserver(albumLauncher)
        lifecycle.addObserver(previewLauncher)
        lifecycle.addObserver(permissionLauncher)
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