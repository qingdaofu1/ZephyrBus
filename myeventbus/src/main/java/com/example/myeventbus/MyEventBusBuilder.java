package com.example.myeventbus;

import com.example.annotations.MethodHandle;

public class MyEventBusBuilder {
    MethodHandle methodHandle;

    public MyEventBusBuilder setMethodHandle(MethodHandle aptInvoke){
        this.methodHandle = aptInvoke;
        return this;
    }

    public MyEventBus build(){
        MyEventBus myEventBus = new MyEventBus(this);
        if(MyEventBus.instance == null){
            MyEventBus.instance = myEventBus;
        }
        return myEventBus;
    }
}
