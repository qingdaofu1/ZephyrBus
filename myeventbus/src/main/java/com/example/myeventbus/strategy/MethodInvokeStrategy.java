package com.example.myeventbus.strategy;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.example.myeventbus.bean.SubscribedMethod;
import com.example.myeventbus.bean.Subscription;

import java.util.List;

public abstract class MethodInvokeStrategy {
    private static final String TAG = "MethodInvokeStrategy";

    private static HandlerThread handlerThread = new HandlerThread("workThread");

    protected static Handler mainHandler;

    protected static Handler workHander;

    public MethodInvokeStrategy() {
        handlerThread.start();
        mainHandler = new Handler(Looper.getMainLooper());
        workHander = new Handler(handlerThread.getLooper());
    }

    public List<SubscribedMethod> getAllSubscribedMethods(Object subscriber) {
        return null;
    }

    public void invokeMethod(Subscription subscription, Object event) {

    }


}
