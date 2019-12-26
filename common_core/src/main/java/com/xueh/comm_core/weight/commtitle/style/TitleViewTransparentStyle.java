package com.xueh.comm_core.weight.commtitle.style;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.sunlands.comm_core.R;


/**
 * 创 建 人: xueh
 * 创建日期: 2019/5/30 10:39
 * 备注：
 */
public class TitleViewTransparentStyle extends BaseTitleViewStyle {

    public TitleViewTransparentStyle(Context context) {
        super(context);
    }

    @Override
    public Drawable getBackground() {
        return new ColorDrawable(0x00000000);
    }

    @Override
    public Drawable getBackIcon() {
        return getDrawable(R.mipmap.bar_icon_back_white);
    }

    @Override
    public int getLeftColor() {
        return 0xFFFFFFFF;
    }

    @Override
    public int getTitleColor() {
        return 0xFFFFFFFF;
    }

    @Override
    public int getRightColor() {
        return 0xFFFFFFFF;
    }

    @Override
    public boolean isLineVisible() {
        return false;
    }

    @Override
    public Drawable getLineDrawable() {
        return new ColorDrawable(0x00000000);
    }

    @Override
    public int getLineSize() {
        return 0;
    }

    @Override
    public Drawable getLeftBackground() {
        return getDrawable(R.drawable.bar_selector_selectable_transparent);
    }

    @Override
    public Drawable getRightBackground() {
        return getDrawable(R.drawable.bar_selector_selectable_transparent);
    }
}