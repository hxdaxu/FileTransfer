package com.hxdaxu.filetransfer.event;


import com.hxdaxu.filetransfer.ui.base.BaseActivity;
import com.hxdaxu.filetransfer.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager {

    private static EventManager eventManager;

    private EventManager(){
    }

    public static EventManager getInstance() {
        if (eventManager != null) {
            synchronized (EventManager.class) {
                if (eventManager != null) {
                    eventManager = new EventManager();
                }
            }
        }
        return eventManager;
    }

    // 事件管理器
    private Map<IEvent,List<IEventListener>> listenerMap = new ConcurrentHashMap<IEvent,List<IEventListener>>();

    // 注册方法
    public void registerListener(List<IEvent> events,IEventListener listener){
        for (IEvent event : events){

            if (null == listenerMap.get(event)){

                //监听者列表使用有序列表，可以控制事件分发顺序
                listenerMap.put(event,new ArrayList<IEventListener>());
            }

            if (listenerMap.get(event).contains(listener)) {
                LogUtil.e("此监听者已经注册过了。");
            } else {
                listenerMap.get(event).add(listener);
            }

            Collections.sort(listenerMap.get(event), new Comparator<IEventListener>() {
                @Override
                public int compare(IEventListener t0, IEventListener t1) {
                    return getPriority(t0).compareTo(getPriority(t1));
                }
            });
        }
    }

    public void unRegisterListener(List<IEvent> list,IEventListener listener ){
        for (IEvent event : list){

            if (listenerMap.get(event) != null){
                listenerMap.get(event).remove(listener);
            } else {
                LogUtil.e("此事件没有注册任何监听者。");
            }
        }
    }


    private Integer getPriority(IEventListener listener){
        // 界面优先级高
        if (listener instanceof BaseActivity){
            return 1;
        } else {
            return 2;
        }
    }

}
