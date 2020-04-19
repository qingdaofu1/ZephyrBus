package com.example.myeventbus;

import android.util.Log;

import com.example.myeventbus.bean.SubscribedMethod;
import com.example.myeventbus.bean.Subscription;
import com.example.myeventbus.strategy.MethodInvokeStrategy;
import com.example.myeventbus.strategy.ReflectInvoke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyEventBus {
    private static final String TAG = "MyEventBus";
    private static MyEventBus instance = null;
    MethodInvokeStrategy invokeStrategy;
    /**
     * 同一类型EventType类与所有注册方法的集合
     */
    private Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType = new HashMap<>();
    /**
     * 所有类有注册MyEventBus的  类与其内部所有处理方法集合。
     */
    private static final Map<Object, List<Class<?>>> typesBySubscriber = new HashMap<>();


    private MyEventBus() {
        invokeStrategy = new ReflectInvoke();
    }

    /**
     * 单例
     *
     * @return
     */
    public static MyEventBus getDefault() {
        if (instance == null) {
            synchronized (MyEventBus.class) {
                if (instance == null) {
                    instance = new MyEventBus();
                }
            }
        }
        return instance;
    }


    /**
     * 注册subscriber到MyEventBus，并获取其所有加了{@link Subscribe} 的方法，并放入集合中
     *
     * @param subscriber 订阅者类，即通过register将this参数传过来的类，可以是activity、service、fragment、thread等。
     */
    public void register(Object subscriber) {
        List<SubscribedMethod> allSubscribedMethods = invokeStrategy.getAllSubscribedMethods(subscriber);
        for (SubscribedMethod subscribedMethod : allSubscribedMethods) {
            Class<?> eventType = subscribedMethod.getEventType();
            CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
            if (subscriptions == null) {
                subscriptions = new CopyOnWriteArrayList<>();
                subscriptionsByEventType.put(eventType, subscriptions);
            }
            // TODO: 2020/4/18  如果做priority逻辑，则在此处需要排序添加
            subscriptions.add(new Subscription(subscriber, subscribedMethod));
            // 获取这个订阅者类中记录的所有的eventType类型
            List<Class<?>> eventTypesInSubscriber = typesBySubscriber.get(subscriber);
            if (eventTypesInSubscriber == null) {
                eventTypesInSubscriber = new ArrayList<>();
                typesBySubscriber.put(subscriber, eventTypesInSubscriber);
            }
            eventTypesInSubscriber.add(eventType);
        }
        printTypesBySubscriber(typesBySubscriber, subscriber);
    }

    private void printTypesBySubscriber(Map<Object, List<Class<?>>> list, Object subscriber) {
        List<Class<?>> classes = list.get(subscriber);
        if (classes != null) {
            for (Class<?> aClass : classes) {
                Log.d(TAG, "register: typesBySubscriber=" + aClass.getName());
            }
        }
    }

    /**
     * 发送event消息到订阅者 处理方法
     *
     * @param event
     */
    public void post(Object event) {
        if (subscriptionsByEventType.size() <= 0) {
            Log.e(TAG, "post: no any eventbus registed named" + event.toString());
            return;
        }
        CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(event.getClass());
        for (Subscription subscription : subscriptions) {
            invokeStrategy.invokeMethod(subscription, event);
        }
    }

    /**
     * 解注册eventbus
     *
     * @param subscriber
     */
    public void unregister(Object subscriber) {
        if (typesBySubscriber != null) {
            typesBySubscriber.remove(subscriber);
        }
    }
}
