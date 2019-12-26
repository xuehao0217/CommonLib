package com.xueh.comm_core.weight.viewstate;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.TextView;
/**
 * 创 建 人: xueh
 * 创建日期: 2019/3/6 14:32
 * 备注：
 */
public class TextViewState extends ViewState<TextView> {
    ColorStateList textColor;

    public TextViewState(TextView textView) {
        super(textView);
    }

    @Override
    public void beforeStart() {
        super.beforeStart();
        this.textColor = view.getTextColors();
        this.darker = view.getTypeface() != null && view.getTypeface().isBold();
    }

    @Override
    protected void restore() {
        this.view.setTextColor(textColor);
    }

    @Override
    public void start(boolean fadein) {
        super.start(fadein);
        view.setTextColor(Color.TRANSPARENT);
    }
}
