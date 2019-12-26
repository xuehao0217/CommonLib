package com.xueh.comm_core.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;


/**
 * @author: xuehao create time: 2017/7/26 11:29
 * tag: class//
 * description:三级统一结构型baseActivity
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseLogic {
    private static final String TAG = "BaseActivity";
    /**
     * activity Content view
     */
    private View inflateView;
    protected LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        inflater = this.getLayoutInflater();
        inflateView = inflater.inflate(getCreateViewLayoutId(), null);
        setContentView(inflateView);
        super.onCreate(savedInstanceState);
        initDataBeforeView();
        initView(inflateView, savedInstanceState);
        initListener();
        initDataAfterView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inflateView = null;
        inflater = null;
    }
}
