package com.xueh.comm_core.helper.xml

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * 创 建 人: xueh
 * 创建日期: 2019/5/31 13:55
 * 备注：
 */
object GlideUtils {
    //加载图片
    fun loadImg(view: ImageView, url: String?, @DrawableRes placeholder: Int = 0) {
        Glide.with(view.context.applicationContext)
            .load(url)
            .apply(
                RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().apply {
                    if (placeholder != 0) {
                        placeholder(placeholder)
                    }
                }

            )
            .into(view)
    }

    //加载圆形图片
    fun loadCircleImg(view: ImageView, url: String?) {
        Glide.with(view.context.applicationContext)
            .load(url)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .centerCrop()
                    .circleCrop()
            )
            .into(view)
    }

    //加载圆角图片
    fun loadCircleImg(view: ImageView, url: String?, circular: Int) {
        Glide.with(view.context.applicationContext)
            .load(url)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(RoundedCorners(ConvertUtils.dp2px(circular.toFloat())))
                    .transform(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(circular.toFloat())))
            )
            .into(view)
    }
}