package com.xueh.comm_core.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.fragment.app.Fragment;


/**
 * @author: xuehao create time: 2017/7/26 11:28
 * tag: class//
 * description:  三级统一结构basefragment
 */
public abstract class BaseFragment extends Fragment implements IBaseLogic {

    private static final String TAG = "BaseFragment";
    /**
     * Fragment Content view
     */
    private View inflateView;
    /**
     * 所属Activity
     */
    public Activity activity;
    /**
     * 记录是否已经创建了,防止重复创建
     * 使用的原因是activity没有对重走生命周期处理
     * @code http://blog.csdn.net/lmj623565791/article/details/42628537)
     */
//    private boolean viewCreated;

    @CallSuper
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @CallSuper
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == inflateView) {
            int layoutResId = getCreateViewLayoutId();
            if (layoutResId > 0) {
                inflateView = inflater.inflate(getCreateViewLayoutId(), container, false);
            }
        }
        return inflateView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initDataBeforeView();
        initView(view, savedInstanceState);
        initDataAfterView();
        initListener();
    }

    @CallSuper
    @Override
    public void onStart() {
        super.onStart();
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        inflateView = null;
        activity = null;
    }
}
