package com.jeremyliao.rxretry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jeremyliao.rxretry.rxjava.RxJavaRetryDemo;
import com.jeremyliao.rxretry.rxjava2.RxJava2RetryDemo;

public class RxRetryActivity extends AppCompatActivity {

    RxJavaRetryDemo rxJavaRetryDemo = new RxJavaRetryDemo();
    RxJava2RetryDemo rxJava2RetryDemo = new RxJava2RetryDemo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava_demo);
    }

    public void testRxjavaRetry1(View view) {
        rxJavaRetryDemo.retry1();
    }

    public void testRxjavaRetry2(View view) {
        rxJavaRetryDemo.retry2();
    }

    public void testRxjava2Retry1(View view) {
        rxJava2RetryDemo.retry1();
    }

    public void testRxjava2Retry2(View view) {
        rxJava2RetryDemo.retry2();
    }

}
