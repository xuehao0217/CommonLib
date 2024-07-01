package com.xueh.commonlib.ui

import android.graphics.Color
import android.os.Bundle
import com.xueh.comm_core.base.xml.DActivity
import com.xueh.commonlib.R
import com.xueh.commonlib.databinding.ActivityMainBinding

class MainActivity : DActivity<ActivityMainBinding>() {

    private val fragments = arrayOf(ComposeFragment(),HomeFragment(), MyFragment())
    private val tabs = arrayOf("Compose", "首页", "我的")

    //未选中icon
    private val normalIcon = intArrayOf(R.mipmap.ic_home_normal, R.mipmap.ic_home_normal,R.mipmap.ic_my_normal)

    //选中时icon
    private val selectIcon = intArrayOf(R.mipmap.ic_home_select,R.mipmap.ic_home_select, R.mipmap.ic_my_select)

    override fun initData() {
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.navigationBar.titleItems(tabs)
            .normalIconItems(normalIcon)
            .selectIconItems(selectIcon)
            .fragmentList(fragments.toList())
            .fragmentManager(supportFragmentManager)
            .normalTextColor(Color.parseColor("#666666"))   //Tab未选中时字体颜色
            .selectTextColor(Color.parseColor("#ff0000"))
            .iconSize(20f)     //Tab图标大小
            .tabTextSize(10)   //Tab文字大小
            .tabTextTop(2)     //Tab文字距Tab图标的距离
//            .lineHeight(10)         //分割线高度  默认1px
            .lineColor(Color.parseColor("#ff0000"))
            .canScroll(false)
            .build()
    }


    override fun initListener() {
    }

}
