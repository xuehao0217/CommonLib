package com.xueh.comm_core.base

import android.os.Bundle

/**
 * @author: xuehao create time: 2017/7/26 11:29
 * tag: class//
 * description:基础接口
 */
interface IBaseLogic {
    /**
     * 此方法用于返回Fragment设置ContentView的布局文件资源ID
     *
     * @return 布局文件资源ID
     */
    fun getLayoutId(): Int

    /**
     * 此方法用于初始化成员变量及获取Intent传递过来的数据
     * 注意：这个方法中不能调用所有的View，因为View还没有被初始化，要使用View在initView方法中调用
     */
    fun initDataBeforeView()

    /**
     * 此方法用于初始化成员变量及获取Intent传递过来的数据
     * 注意：这个方法中不能调用所有的View，因为View还没有被初始化，要使用View在initView方法中调用
     */
    fun initDataAfterView()

    /**
     * 此方法用于设置View显示数据
     */
    fun initView( savedInstanceState: Bundle?)

    fun initListener()
}