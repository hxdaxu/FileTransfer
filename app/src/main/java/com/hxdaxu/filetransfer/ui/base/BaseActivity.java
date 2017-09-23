package com.hxdaxu.filetransfer.ui.base;

import android.app.Activity;
import android.os.Bundle;

import com.hxdaxu.filetransfer.event.EventManager;
import com.hxdaxu.filetransfer.event.IEventListener;
import com.hxdaxu.filetransfer.event.IEvent;

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

    /**
     * 初始化控件
     */
    protected void initView(){
    }

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
