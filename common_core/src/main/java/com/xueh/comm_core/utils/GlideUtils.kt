package com.xueh.comm_core.utils;

import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

/**
 * 创 建 人: xueh
 * 创建日期: 2019/5/31 13:55
 * 备注：
 */
public class GlideUtils {


    //加载图片
    public static void loadImg(ImageView view, String url) {
        Glide.with(view.getContext().getApplicationContext())
                .load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop())
                .into(view);
    }

    //加载圆形图片
    public static void loadCircleImg(ImageView view, String url) {
        Glide.with(view.getContext().getApplicationContext())
                .load(url)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .centerCrop()
                        .circleCrop())
                .into(view);
    }

    //加载圆角图片
    public static void loadCircleImg(ImageView view, String url, int circular) {
        Glide.with(view.getContext().getApplicationContext())
                .load(url)
                .apply(new RequestOptions()
                        .transform(new RoundedCorners(ConvertUtils.dp2px(circular))).transform(new CenterCrop(), new RoundedCorners(ConvertUtils.dp2px(circular))))
                .into(view);
    }
}
