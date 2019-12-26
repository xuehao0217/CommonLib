package com.xueh.comm_core.weight.viewstate;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
/**
 * 创 建 人: xueh
 * 创建日期: 2019/3/6 14:32
 * 备注：
 */
public class ViewStateOf {
    private final Context context;

    private HashMap<View, ViewState> viewsState;

    boolean fadein = true;

    private boolean started;

    public ViewStateOf(Context context) {
        this.context = context;
        this.viewsState = new HashMap<>();
    }

    public static ViewStateOf with(Context context) {
        return new ViewStateOf(context);
    }

    public ViewStateOf on(int... viewsId) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            for (int view : viewsId) {
                add(activity.findViewById(view));
            }
        }
        return this;
    }

    public ViewStateOf fadein(boolean fadein) {
        this.fadein = fadein;
        return this;
    }

    private void add(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            viewsState.put(view, new TextViewState(textView));
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            viewsState.put(view, new ImageViewState(imageView));
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; ++i) {
                View child = viewGroup.getChildAt(i);
                add(child);
            }
        }
    }

    public ViewStateOf on(View... views) {
        for (View view : views)
            add(view);
        return this;
    }

    public ViewStateOf except(View... views) {
        for (View view : views) {
            this.viewsState.remove(view);
        }
        return this;
    }

    public ViewStateOf start() {
        if (!started) {
            //prepare for starting
            for (ViewState viewState : viewsState.values()) {
                viewState.beforeStart();
            }
            started = true;
            //start
            for (ViewState viewState : viewsState.values()) {
                viewState.start(fadein);
            }
        }
        return this;
    }

    public ViewStateOf stop() {
        if (started) {
            for (ViewState viewState : viewsState.values()) {
                viewState.stop();
            }
            started = false;
        }
        return this;
    }
}
