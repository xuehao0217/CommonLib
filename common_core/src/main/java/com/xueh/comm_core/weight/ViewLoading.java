package com.xueh.comm_core.weight;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.sunlands.comm_core.R;
/**
 * 创 建 人: xueh
 * 创建日期: 2019/3/6 14:32
 * 备注：
 */
public class ViewLoading extends Dialog {

    private static ViewLoading loadDialog;
    private boolean isDimEnabled = false;
    private ViewLoading(Context context, boolean canNotCancel) {
        super(context);
        // 加载布局
        setContentView(R.layout.dialog_progressbar);
        // 设置Dialog参数
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            if (isDimEnabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
            window.setAttributes(params);
        }
        if (canNotCancel) {
            this.setCanceledOnTouchOutside(false);
            this.setCancelable(false);
        }
    }
    public static void show(Context context) {
        show(context,true);
    }

    /**
     * 展示加载窗
     * @param context               上下文
     * @param isCancel              是否可以取消
     */
    public static void show(Context context,  boolean isCancel) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        if (loadDialog != null && loadDialog.isShowing()) {
            return;
        }
        loadDialog = new ViewLoading(context,isCancel);
        loadDialog.show();
    }


    /**
     * 销毁加载窗
     *
     * @param context 上下文
     */
    public static void dismiss(Context context) {
        try {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    loadDialog = null;
                    return;
                }
            }
            if (loadDialog != null && loadDialog.isShowing()) {
                Context loadContext = loadDialog.getContext();
                if (loadContext instanceof Activity) {
                    if (((Activity) loadContext).isFinishing()) {
                        loadDialog = null;
                        return;
                    }
                }
                loadDialog.dismiss();
                loadDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadDialog = null;
        }
    }

}
