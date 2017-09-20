package com.hxdaxu.filetransfer.ui.base;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beforeSetContentView();
        setContentView(getContentView());
        initView();
        prepareData();


    }


    protected void prepareData(){
    }

    /**
     * setContentView 之前执行，可以用来设置一些window属性
     */
    protected void beforeSetContentView(){
    }


    protected abstract int getContentView();



    private void initView(){
        getView();
        setListener();
        setContent();
    }


    /**
     * 初始化view控件
     */
    protected void getView(){}
    /**
     * 为view控件设置监听
     */
    protected void setListener(){}
    /**
     * 设置控件初始数据
     */
    protected void setContent(){}



















}
