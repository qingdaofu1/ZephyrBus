package com.example.myeventbus.strategy;

import com.example.myeventbus.bean.SubscribedMethod;
import com.example.myeventbus.bean.Subscription;

import java.util.List;

public class AptAnnotationInvoke extends MethodInvokeStrategy{
    @Override
    public List<SubscribedMethod> getAllSubscribedMethods(Object subscriber) {
        // TODO: 2020/4/19  
        return null;
    }

    @Override
    public void invokeMethod(Subscription subscription, Object event) {
        // TODO: 2020/4/19  
    }
}
