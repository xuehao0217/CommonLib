package com.xueh.commonlib.ui.xml

import android.graphics.Color
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.xml.DActivity
import com.xueh.commonlib.R
import com.xueh.commonlib.databinding.ActivityMainBinding
import androidx.core.graphics.toColorInt

class MainActivity : DActivity<ActivityMainBinding>() {

    private val fragments = arrayOf(HomeFragment(), MyFragment())
    private val tabs = arrayOf( "首页", "我的")

    //未选中icon
    private val normalIcon = intArrayOf( R.mipmap.ic_home_normal,R.mipmap.ic_my_normal)

    //选中时icon
    private val selectIcon = intArrayOf(R.mipmap.ic_home_select, R.mipmap.ic_my_select)

    override fun initData() {
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.navigationBar.titleItems(tabs)
            .normalIconItems(normalIcon)
            .selectIconItems(selectIcon)
            .fragmentList(fragments.toList())
            .fragmentManager(supportFragmentManager)
            .normalTextColor("#666666".toColorInt())   //Tab未选中时字体颜色
            .selectTextColor("#ff0000".toColorInt())
            .iconSize(20f)     //Tab图标大小
            .tabTextSize(10)   //Tab文字大小
            .tabTextTop(2)     //Tab文字距Tab图标的距离
//            .lineHeight(10)         //分割线高度  默认1px
            .lineColor("#ff0000".toColorInt())
            .canScroll(false)
            .build()
    }

    private var  backPressedTime=0L
    override fun initListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
//                if (System.currentTimeMillis() - backPressedTime <= 2000) {
//                    AppUtils.exitApp()
//                } else {
//                    backPressedTime = System.currentTimeMillis()
//                    ToastUtils.showShort("Press again to exit the app")
//                }
            }
        })
    }

}
