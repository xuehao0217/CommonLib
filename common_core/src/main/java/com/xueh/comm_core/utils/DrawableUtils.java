package com.xueh.comm_core.utils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.noober.background.drawable.DrawableCreator;


/**
 * 创 建 人: xueh
 * 创建日期: 2019/6/4 15:41
 * 备注：
 */
public class DrawableUtils {
    /**
     * 设置圆角 TextView
     */
    public static void setRoundBg(TextView view, int CornersRadius, @ColorRes int bgColor, @ColorRes int tvCollor) {
        Drawable drawable = new DrawableCreator.Builder().setCornersRadius(CommonUtils.dip2px(CornersRadius))
                .setSolidColor(CommonUtils.getColor(bgColor))
                .setStrokeColor(CommonUtils.getColor(bgColor))
                .build();
        view.setBackground(drawable);
        view.setTextColor(CommonUtils.getColor(tvCollor));
    }

    /**
     * 设置圆线 TextView
     */
    public static void setRoundLineBg(TextView view, int radius, @ColorRes int lineColor, @ColorRes int tvColor) {
        Drawable drawable = new DrawableCreator.Builder().setCornersRadius(CommonUtils.dip2px(radius))
                .setStrokeColor(CommonUtils.getColor(lineColor))
                .setStrokeWidth(CommonUtils.dip2px(1))
                .build();
        view.setBackground(drawable);
        view.setTextColor(CommonUtils.getColor(tvColor));
    }

    /**
     * 设置圆线 View
     */
    public static void setRoundLineBg(View view, int radius, @ColorRes int lineColor) {
        Drawable drawable = new DrawableCreator.Builder().setCornersRadius(CommonUtils.dip2px(radius))
                .setStrokeColor(CommonUtils.getColor(lineColor))
                .setStrokeWidth(CommonUtils.dip2px(1))
                .build();
        view.setBackground(drawable);
    }

    /**
     * 设置圆线 View 带宽度
     */
    public static void setRoundBg(TextView view, int radius, @ColorRes int lineColor, @ColorRes int tvColor,int width) {
        Drawable drawable = new DrawableCreator.Builder().setCornersRadius(CommonUtils.dip2px(radius))
                .setStrokeColor(CommonUtils.getColor(lineColor))
                .setStrokeWidth(CommonUtils.dip2px(width))
                .build();
        view.setBackground(drawable);
        view.setTextColor(CommonUtils.getColor(tvColor));
    }

    public static void setRoundBg(View view, int CornersRadius, @ColorRes int bgColor) {
        Drawable drawable = new DrawableCreator.Builder().setCornersRadius(CommonUtils.dip2px(CornersRadius))
                .setSolidColor(CommonUtils.getColor(bgColor))
                .setStrokeColor(CommonUtils.getColor(bgColor))
                .build();
        view.setBackground(drawable);
    }

    /**
     * 设置圆线 带背景
     */
    public static void setRoundLineBg(View view, @ColorRes int SolidColor, int radius, @ColorRes int lineColor) {
        Drawable drawable = new DrawableCreator.Builder().setCornersRadius(CommonUtils.dip2px(radius))
                .setStrokeColor(CommonUtils.getColor(lineColor))
                .setStrokeWidth(CommonUtils.dip2px(1))
                .setSolidColor(CommonUtils.getColor(SolidColor))
                .build();
        view.setBackground(drawable);
    }

    public static void setRoundLineSolidBg(View view, @ColorRes int SolidColor, int radius, @ColorRes int lineColor) {
        Drawable drawable = new DrawableCreator.Builder().setCornersRadius(CommonUtils.dip2px(radius))
                .setStrokeColor(CommonUtils.getColor(lineColor))
                .setStrokeWidth(CommonUtils.dip2px(1))
                .setSolidColor(CommonUtils.getColor(SolidColor))
                .build();
        view.setBackground(drawable);
    }

}
