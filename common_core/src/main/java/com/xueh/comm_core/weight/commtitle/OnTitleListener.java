package com.xueh.comm_core.weight.commtitle;

import android.view.View;

/**
 * 创 建 人: xueh
 * 创建日期: 2019/5/22 17:50
 * 备注：
 */
public interface OnTitleListener {

    /**
     * 左项被点击
     *
     * @param v 被点击的左项View
     */
    void onLeftClick(View v);

    /**
     * 标题被点击
     *
     * @param v 被点击的标题View
     */
    void onTitleClick(View v);

    /**
     * 右项被点击
     *
     * @param v 被点击的右项View
     */
    void onRightClick(View v);
}