package com.example.zephyrbus;

import com.example.annotations.MethodHandle;
import com.example.annotations.SubscribedMethod;
import com.example.annotations.Subscription;
import com.example.annotations.ThreadMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AptMethodFinderTemplate implements MethodHandle {

    private static final Map<Object, List<SubscribedMethod>> aptMap = new HashMap<>();

    static {
        aptMap.put(com.example.zephyrbus.MainActivity.class, findMethodsInMainActivity());
    }

    @Override
    public List<SubscribedMethod> getAllSubscribedMethods(Object subscriber) {
        return aptMap.get(subscriber);
    }

    @Override
    public void invokeMethod(Subscription subscription, Object event) {

    }

    private static List<SubscribedMethod> findMethodsInMainActivity(){
        List<SubscribedMethod> subscribedMethods = new ArrayList<>();
        subscribedMethods.add(new SubscribedMethod(com.example.zephyrbus.MainActivity.class, com.example.zephyrbus.Event.WorkEvent.class, ThreadMode.POSTING, 0, "onEvent"));
        subscribedMethods.add(new SubscribedMethod(com.example.zephyrbus.MainActivity.class, com.example.zephyrbus.Event.ViewEvent.class, ThreadMode.MAIN, 0, "handleView"));
        return subscribedMethods;
    }
}
