package com.example.annotations;

/**
 * 保存  注册了订阅者的类即有register的类和加了注解Subscribe的方法的java类
 */
public class Subscription {
    //订阅者类
    private Object subscriber;
    //订阅者方法类
    private SubscribedMethod subscribedMethod;

    public Subscription(Object subscriber, SubscribedMethod subscribedMethod) {
        this.subscriber = subscriber;
        this.subscribedMethod = subscribedMethod;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public SubscribedMethod getSubscribedMethod() {
        return subscribedMethod;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "subscriber=" + subscriber +
                ", subscribedMethod=" + subscribedMethod +
                '}';
    }
}
