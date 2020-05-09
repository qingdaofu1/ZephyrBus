package com.example.myeventbus;

import android.util.Log;

import com.example.annotations.MethodHandle;
import com.example.annotations.Subscribe;
import com.example.annotations.SubscribedMethod;
import com.example.annotations.Subscription;
import com.example.myeventbus.strategy.AptAnnotationInvoke;
import com.example.myeventbus.strategy.MethodInvokeStrategy;
import com.example.myeventbus.strategy.ReflectInvoke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyEventBus {
    private static final String TAG = "MyEventBus";
    static MyEventBus instance = null;
    MethodInvokeStrategy invokeStrategy;
    /**
     * 同一类型EventType类与所有注册方法的集合
     */
    private Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType = new HashMap<>();
    /**
     * 所有类有注册MyEventBus的  类与其内部所有处理方法集合。
     */
    private static final Map<Object, List<Class<?>>> typesBySubscriber = new HashMap<>();

    private static MyEventBusBuilder DEFAULT_BUILDER = new MyEventBusBuilder();

    public MyEventBus() {
        this(DEFAULT_BUILDER);
    }

    public static MyEventBusBuilder builder() {
        return new MyEventBusBuilder();
    }

    public MyEventBus(MyEventBusBuilder myEventBusBuilder) {
        //注解处理器获取订阅者方法和调用
//        invokeStrategy = new AptAnnotationInvoke();
        if (myEventBusBuilder != null && myEventBusBuilder.methodHandle != null) {
            //注解处理器获取订阅者方法和调用
            invokeStrategy = new AptAnnotationInvoke(myEventBusBuilder.methodHandle);
        } else {
            //反射获取订阅者方法和反射调用方法
            invokeStrategy = new ReflectInvoke();
        }
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
        if (allSubscribedMethods == null) {
            Log.e(TAG, "register: null");
            return;
        }
        if (allSubscribedMethods.size() <= 0) {
            Log.e(TAG, "register: there is no mehod founded!");
            return;
        }
        for (SubscribedMethod subscribedMethod : allSubscribedMethods) {
            Class<?> eventType = subscribedMethod.getEventType();
            CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
            if (subscriptions == null) {
                subscriptions = new CopyOnWriteArrayList<>();
                subscriptionsByEventType.put(eventType, subscriptions);
            }
            // TODO: 2020/4/18  如果做priority逻辑，则在此处需要排序添加
            subscriptions.add(new Subscription(subscriber, subscribedMethod));
            printSubscriptionsByEventType(subscriptionsByEventType);
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

    private void printSubscriptionsByEventType(Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType) {
        Set<Class<?>> classes = subscriptionsByEventType.keySet();
        for (Class<?> aClass : classes) {
            CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(aClass);
            for (Subscription subscription : subscriptions) {
                Log.d(TAG, "printSubscriptionsByEventType: aClass=" + aClass.getName() + " subscription=" + subscription.toString());
            }

        }
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

        Log.d(TAG, "event.getClass()=" + event.getClass().getName());
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
