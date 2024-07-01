package com.xueh.comm_core.base.xml

import android.os.Bundle

/**
 * @author: xuehao create time: 2017/7/26 11:29
 * tag: class//
 * description:基础接口
 */
interface IBaseLogic {

    fun initData()

    /**
     * 此方法用于设置View显示数据
     */
    fun initView(savedInstanceState: Bundle?)

    fun initListener()
}