package com.xueh.comm_core.helper.activityresult;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.Serializable;

/**
 *  //使用
 *  https://www.jianshu.com/p/ca1573f7b35c
 *
 *  简单用法
 *
 *  ActivityResult.of(this).className(Main2Activity::class.java).forResult(object : ActivityResultListener {
 *                     override fun onReceiveResult(resultCode: Int, data: Intent?) {
 *                         ToastUtils.showShort(data?.getStringExtra("test"))
 *                     }
 *                 })
 *
 *
 *  Activity跳转结果回调处理
 */
public final class ActivityResult {
  static final String TAG_ACTIVITY_RESULT_FRAGMENT = "ActivityResult";

  ActivityResultFragment resultFragment;

  private IntentBuilder intentBuilder;

  /**
   * 绿色通道
   */
  boolean greenChannel = false;

  /**
   * 当前拦截停止的的拦截器,继续执行时从此拦截器执行
   */
  Intercept currentIntercept;

  /**
   * 需要执行的拦截器
   */
  Class[] intercepts;

  /**
   * Transition 动画
   */
  Bundle bundle;

  public ActivityResult(AppCompatActivity activity) {
    intentBuilder = IntentBuilder.builder(activity);
    resultFragment = getActivityResultFragment(activity);
  }

  public static ActivityResult of(@NonNull AppCompatActivity activity) {
    return new ActivityResult(activity);
  }

  public static ActivityResult of(@NonNull Fragment fragment) {
    return new ActivityResult((AppCompatActivity) fragment.getActivity());
  }

  public Intercept getCurrentIntercept() {
    return currentIntercept;
  }

  /**
   * 拦截器，一次性的，使用后会自动移除
   */
  public ActivityResult intercept(Intercept intercept) {
    ActivityResultManager.get()
        .registerIntercept(intercept,true);
    return this;
  }
  /**
   * transition动画 {@link ActivityOptions#toBundle()}
   */
  public ActivityResult options(Bundle options) {
    bundle = options;
    return this;
  }
  /**
   * 绿色通道，不走任何拦截器,在拦截器中使用ActivityResult跳转需要使用绿色通道，否则会造成死循环
   */
  public ActivityResult greenChannel() {
    greenChannel = true;
    return this;
  }

  /**
   * 绿色通道，只执行指定拦截器,在拦截器中使用ActivityResult跳转需要使用绿色通道，否则会造成死循环
   */
  public ActivityResult greenChannel(Class<Intercept>... intercepts) {
    greenChannel = true;
    this.intercepts = intercepts;
    return this;
  }

  /**
   * 添加启动标记
   */
  public ActivityResult flag(int flags) {
    intentBuilder.flag(flags);
    return this;
  }

  /**
   * 动作
   */
  public ActivityResult action(String action) {
    intentBuilder.action(action);
    return this;
  }

  public ActivityResult params(Bundle bundle) {
    intentBuilder.params(bundle);
    return this;
  }

  /**
   * 参数添加
   */
  public ActivityResult params(String key, String value) {
    intentBuilder.params(key, value);
    return this;
  }

  /**
   * 参数添加
   */
  public ActivityResult params(String key, int value) {
    intentBuilder.params(key, value);
    return this;
  }

  /**
   * 参数添加
   */
  public ActivityResult params(String key, boolean value) {
    intentBuilder.params(key, value);
    return this;
  }

  /**
   * 参数添加
   */
  public ActivityResult params(String key, Serializable value) {
    intentBuilder.params(key, value);
    return this;
  }

  /**
   * 参数添加
   */
  public ActivityResult params(String key, Parcelable value) {
    intentBuilder.params(key, value);
    return this;
  }

  /**
   * 设置类名
   */
  public ActivityResult className(Class clazz) {
    intentBuilder.className(clazz);
    return this;
  }

  /**
   * 设置类名
   */
  public ActivityResult className(String className) {
    intentBuilder.className(className);
    return this;
  }

  /**
   * 构建Intent
   */
  public Intent build() {
    return intentBuilder.build();
  }

  /**
   * 以返回值方式打开
   * @param activityResultListener 可以为 null,null表示不需要处理返回值
   */
  public void forResult(@Nullable ActivityResultListener activityResultListener) {
    if(Utils.isFastClick()){
      //绿色通道不走拦截器
      resultFragment.setActivityResultListener(activityResultListener);
      if (!greenChannel) {
        execIntercepts();
      } else {
        startActivity();
      }
    }
  }

  /**
   * 继续跳转，恢复跳转逻辑
   */
  public void onContinue() {
    execIntercepts();
  }

  private void execIntercepts() {
    ActivityResultManager.get().intercept(resultFragment.getActivity(), this, intercepts);
  }

  protected void startActivity() {
    if(Build.VERSION.SDK_INT >= 16) {
      resultFragment.startActivityForResult(build(), 1, bundle);
    } else {
      resultFragment.startActivityForResult(build(), 1);
    }
  }

  /**
   * 查找ActivityResultFragment
   */
  Fragment findActivityResultFragment(AppCompatActivity activity) {
    return activity.getSupportFragmentManager().findFragmentByTag(TAG_ACTIVITY_RESULT_FRAGMENT);
  }

  private ActivityResultFragment getActivityResultFragment(AppCompatActivity activity) {
    Fragment activityResultFragment = findActivityResultFragment(activity);
    if (activityResultFragment == null) {
      FragmentManager fragmentManager = activity.getSupportFragmentManager();
      activityResultFragment = new ActivityResultFragment();
      fragmentManager.beginTransaction()
          .add(activityResultFragment, TAG_ACTIVITY_RESULT_FRAGMENT)
          .commitAllowingStateLoss();
      fragmentManager.executePendingTransactions();
    }
    return (ActivityResultFragment) activityResultFragment;
  }
}