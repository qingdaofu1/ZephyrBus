package com.example.myeventbus.bean;

import com.example.myeventbus.ThreadMode;

import java.lang.reflect.Method;

public class SubscribedMethod {
    private Method method;
    private Class<?> eventType;
    private ThreadMode threadMode;
    private int priority;

    public SubscribedMethod(Method method, Class<?> eventType, ThreadMode threadMode, int priority) {
        this.method = method;
        this.eventType = eventType;
        this.threadMode = threadMode;
        this.priority = priority;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "SubscribedMethod{" +
                "method=" + method +
                ", eventType=" + eventType +
                ", threadMode=" + threadMode +
                ", priority=" + priority +
                '}';
    }
}
