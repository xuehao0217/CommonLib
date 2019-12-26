package com.xueh.commonlib.ui

import android.os.Bundle
import android.view.View
import com.xueh.comm_core.base.DActivity
import com.xueh.commonlib.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : DActivity() {
    private val fragments = arrayOf(HomeFragment(), MyFragment())
    private val tabs = arrayOf("首页", "我的")
    //未选中icon
    private val normalIcon = intArrayOf(R.mipmap.ic_home_normal, R.mipmap.ic_my_normal)
    //选中时icon
    private val selectIcon = intArrayOf(R.mipmap.ic_home_select, R.mipmap.ic_my_select)

    override fun initView(inflateView: View?, savedInstanceState: Bundle?) {
        main_cnb.titleItems(tabs).normalIconItems(normalIcon).selectIconItems(selectIcon)
            .fragmentList(fragments.toList())
            .fragmentManager(supportFragmentManager)
            .build()
    }

    override fun initDataAfterView() {
    }

    override fun initListener() {
    }

    override fun getCreateViewLayoutId() = R.layout.activity_main
}
