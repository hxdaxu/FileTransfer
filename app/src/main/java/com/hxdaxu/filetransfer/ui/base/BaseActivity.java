package com.hxdaxu.filetransfer.ui.base;

import android.app.Activity;
import android.os.Bundle;

import com.hxdaxu.filetransfer.event.EventManager;
import com.hxdaxu.filetransfer.event.IEventListener;
import com.hxdaxu.filetransfer.event.IEvent;

import java.lang.reflect.Array;
import java.util.Arrays;

public abstract class BaseActivity extends Activity implements IEventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        setContentView(getContentView());
        initView();
        prepareData();
        EventManager.getInstance().registerListener(Arrays.asList(getEvent()),this);
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

    /**
     * @return 监听的消息集合
     */
    protected IEvent[] getEvent(){
        return new IEvent[0];
    }


    @Override
    public void onEvent(IEvent event, Object data) {
    }

    @Override
    protected void onDestroy() {

        EventManager.getInstance().unRegisterListener(Arrays.asList(getEvent()),this);
        super.onDestroy();
    }
}
