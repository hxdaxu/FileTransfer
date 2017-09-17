package com.hxdaxu.filetransfer.event;


import android.support.annotation.NonNull;

import com.hxdaxu.filetransfer.ui.base.BaseActivity;

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
    private Map<IEvent,List<EventListener>> listenerMap = new ConcurrentHashMap<IEvent,List<EventListener>>();

    // 注册方法
    public void registerListener(List<IEvent> events,List<EventListener> listeners){
        for (IEvent event : events){

            if (null == listenerMap.get(event)){

                //监听者列表使用有序列表，可以控制事件分发顺序
                listenerMap.put(event,new ArrayList<EventListener>());
            }

            for (EventListener listener : listeners){

                if(listenerMap.get(event).contains(listener)){
                    // log.e  监听者已注册
                } else {
                    listenerMap.get(event).add(listener);
                }

            }

            Collections.sort(listenerMap.get(event), new Comparator<EventListener>() {
                @Override
                public int compare(EventListener t0, EventListener t1) {
                    return getPriority(t0).compareTo(getPriority(t1));
                }
            });




        }



    }

    private Integer getPriority(EventListener listener){
        // 界面优先级高
        if (listener instanceof BaseActivity){
            return 1;
        } else {
            return 2;
        }
    }

}
