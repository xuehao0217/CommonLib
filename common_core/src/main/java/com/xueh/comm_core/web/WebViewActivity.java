package com.xueh.comm_core.web;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.hjq.bar.TitleBar;
import com.just.agentweb.AgentWeb;
import com.sunlands.comm_core.R;
import com.xueh.comm_core.base.BaseViewImpl;
import com.xueh.comm_core.base.DActivity;
import com.xueh.comm_core.weight.OnTitleLeftListener;


/**
 * 创 建 人: xueh
 * 创建日期: 2019/3/11 11:16
 * 备注：一个只用于显示的web页面
 */
public class WebViewActivity extends DActivity implements BaseViewImpl.OnClickListener {

    public AgentWeb agentWeb;
    private LinearLayout mRv_web_content;
    public static final String TITLE = "title";
    public static final String URL = "url";
    public TitleBar mCommtitle;
    protected String mUrl;
    protected View mView_status_bar;

    @Override
    public void onPause() {
        agentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        agentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        agentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (agentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public int getCreateViewLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    public void initView(View inflateView, Bundle savedInstanceState) {
        mRv_web_content = findViewById(R.id.rv_web_content);
        mCommtitle = findViewById(R.id.tb_title_bar);
        mView_status_bar = findViewById(R.id.view_status_bar);
    }

    @Override
    public void initDataBeforeView() {

    }

    @Override
    public void initDataAfterView() {
        mUrl = getIntent().getStringExtra(URL);
        mCommtitle.setTitle(getIntent().getStringExtra(TITLE));
        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(mRv_web_content, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(mWebChromeClient) //WebChromeClient
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .createAgentWeb()
                .ready()
                .go(mUrl);
    }

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //  super.onProgressChanged(view, newProgress);
            Log.i("WebViewActivity", "onProgressChanged:" + newProgress + "  view:" + view);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mCommtitle != null && !TextUtils.isEmpty(title)) {
                if (title.length() > 10) {
                    title = title.substring(0, 10).concat("...");
                }
            }
            mCommtitle.setTitle(title);
        }
    };


    @Override
    public void initListener() {
        mCommtitle.setOnTitleBarListener(new OnTitleLeftListener() {
            @Override
            public void onLeftClick(View v) {
                WebViewActivity.this.finish();
            }
        });
    }
}