package com.xueh.comm_core.weight.commtitle;

import android.graphics.drawable.Drawable;
/**
 * 创 建 人: xueh
 * 创建日期: 2019/5/22 17:50
 * 备注：
 */
public interface ITitleBarStyle {

    int getTitleBarHeight(); // 标题栏高度（默认为ActionBar的高度）
    Drawable getBackground(); // 背景颜色
    Drawable getBackIcon(); // 返回按钮图标

    int getLeftColor(); // 左边文本颜色
    int getTitleColor(); // 标题文本颜色
    int getRightColor(); // 右边文本颜色

    float getLeftSize(); // 左边文本大小
    float getTitleSize(); // 标题文本大小
    float getRightSize(); // 右边文本大小

    boolean isLineVisible(); // 分割线是否可见
    Drawable getLineDrawable(); // 分割线背景颜色
    int getLineSize(); // 分割线的大小

    Drawable getLeftBackground(); // 左边背景资源
    Drawable getRightBackground(); // 右边背景资源
}