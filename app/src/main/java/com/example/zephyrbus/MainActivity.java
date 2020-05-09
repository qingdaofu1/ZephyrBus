package com.example.zephyrbus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.annotations.Subscribe;
import com.example.annotations.ThreadMode;
import com.example.myeventbus.MyEventBus;
import com.example.myeventbus.MyEventBusBuilder;
import com.example.myeventbus.strategy.AptAnnotationInvoke;
import com.example.zephyrbus.Event.ViewEvent;
import com.example.zephyrbus.Event.WorkEvent;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean isThreadMain = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_send1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyEventBus.getDefault().post(new WorkEvent(5));
            }
        });
        findViewById(R.id.btn_send2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isThreadMain) {
                    MyEventBus.getDefault().post(new ViewEvent("主线程测试文字"));
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            MyEventBus.getDefault().post(new ViewEvent("子线程测试文字"));
                        }
                    }.start();
                }
                isThreadMain = !isThreadMain;
            }
        });
    }

    @Subscribe()
    public void onEvent(final WorkEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Thread is " + Thread.currentThread().getName() + " Thread, WorkEvent num=" + event.getNum(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleView(final ViewEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Thread is " + Thread.currentThread().getName() + " Thread, ViewEvent text=" + event.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        long startTime = System.currentTimeMillis();
        Log.d(TAG, "onStart: startTime=" + startTime);

        //反射调用方式
        //MyEventBus.getDefault().register(this);
        AptMethodFinder aptMethodFinder = new AptMethodFinder();
        //注解处理器代码的模板类
//        AptMethodFinderTemplate aptMethodFinder = new AptMethodFinderTemplate();
        //注解处理调用方式
        MyEventBus.builder().setMethodHandle(aptMethodFinder).build().register(this);
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "onStart: endTime=" + endTime);
        Log.d(TAG, "onStart: collect all subscribe method cost " + (endTime - startTime));
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyEventBus.getDefault().unregister(this);
    }


}
