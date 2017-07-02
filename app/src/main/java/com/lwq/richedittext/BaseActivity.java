package com.lwq.richedittext;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * User:lwq
 * Date:2017-07-02
 * Time:14:59
 * introduction:
 */
abstract class BaseActivity extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initView());
        setListener();
        initData();
    }

    protected abstract View initView();
    protected void setListener(){}
    protected abstract void initData();
}
