package com.xueh.comm_core.weight.commtitle.style;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.sunlands.comm_core.R;

/**
 * 创 建 人: xueh
 * 创建日期: 2019/5/22 17:50
 * 备注：
 */
public class TitleViewLightStyle extends BaseTitleViewStyle {

    public TitleViewLightStyle(Context context) {
        super(context);
    }

    @Override
    public Drawable getBackground() {
        return new ColorDrawable(0xFFFFFFFF);
    }

    @Override
    public Drawable getBackIcon() {
        return getDrawable(R.mipmap.bar_icon_back_black);
    }

    @Override
    public int getLeftColor() {
        return 0xFF666666;
    }

    @Override
    public int getTitleColor() {
        return 0xFF222222;
    }

    @Override
    public int getRightColor() {
        return 0xFFA4A4A4;
    }

    @Override
    public boolean isLineVisible() {
        return true;
    }

    @Override
    public Drawable getLineDrawable() {
        return new ColorDrawable(0xFFECECEC);
    }

    @Override
    public int getLineSize() {
        return 1;
    }

    @Override
    public Drawable getLeftBackground() {
        return getDrawable(R.drawable.bar_selector_selectable_white);
    }

    @Override
    public Drawable getRightBackground() {
        return getDrawable(R.drawable.bar_selector_selectable_white);
    }
}