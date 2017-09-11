package com.hxdaxu.filetransfer.event;


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
    public void register(){

    }



}
