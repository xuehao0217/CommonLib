package com.xueh.comm_core.helper;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xueh.comm_core.R;


/**
 * tag: class//转圈的dialog
 * description: loading
 */
public class ProgressDialog extends BaseDialogHelper {

    private TextView titleTv;
    private String data;
    private boolean isBack=true;

    public ProgressDialog setTitleTv(String titleSt) {
        this.data = titleSt;
        return this;
    }

    public void setBack(boolean back) {
        isBack = back;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            data = savedInstanceState.getString("title");
        }
        View view = getView();
        titleTv = view.findViewById(R.id.tv_show);
        setCanceledBack(isBack);
        setCanceledOnTouchOutside(false);
        setGravity(Gravity.CENTER);
        setWidth(1f);
        setDimEnabled(false);
        if (titleTv != null && !TextUtils.isEmpty(data)) {
            titleTv.setText(data);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_progressbar;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(data)) {
            outState.putString("title", data);
        }
    }
}
