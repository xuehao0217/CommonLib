package com.xueh.comm_core.weight.viewstate;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * 创 建 人: xueh
 * 创建日期: 2019/3/6 14:32
 * 备注：
 */
public class ImageViewState extends ViewState<ImageView> {
    Drawable source;

    public ImageViewState(ImageView imageView) {
        super(imageView);
    }

    @Override
    public void beforeStart() {
        super.beforeStart();
        this.source = view.getDrawable();
        view.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void restore() {
        this.view.setImageDrawable(source);
    }
}
