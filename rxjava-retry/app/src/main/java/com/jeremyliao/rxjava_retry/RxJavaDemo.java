package com.jeremyliao.rxjava_retry;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import com.jeremyliao.rxjava_retry.databinding.ActivityRxjavaDemoBinding;

import java.util.Random;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxJavaDemo extends AppCompatActivity {

    private ActivityRxjavaDemoBinding binding;
    private int executeTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rxjava_demo);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
    }

    public void testRetry1() {
        retry1();
    }

    public void testRetry2() {
        retry2();
    }

    private Observable<Boolean> sourceMayFail() {
        return Observable.just(new Random())
                .map(new Func1<Random, Integer>() {
                    @Override
                    public Integer call(Random random) {
                        return random.nextInt(100);
                    }
                })
                .map(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        Log.d("test", "executeTimes: " + executeTimes++);
                        return integer < 50;
                    }
                });
    }

    private Observable<Integer> sourceMayFailThrow() {
        return Observable.just(new Random())
                .map(new Func1<Random, Integer>() {
                    @Override
                    public Integer call(Random random) {
                        return random.nextInt(100);
                    }
                })
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d("test", "executeTimes: " + executeTimes++);
                        if (integer > 50) {
                            throw new RuntimeException();
                        }
                    }
                });
    }

    private void retry1() {
        executeTimes = 0;
        new BooleanRetryWrapper(sourceMayFail())
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Toast.makeText(RxJavaDemo.this, "testRetry complete: " + aBoolean,
                                Toast.LENGTH_SHORT).show();
                        Log.d("test", "testRetry complete: " + aBoolean);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(RxJavaDemo.this, "testRetry error: " + throwable.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("test", "testRetry error: " + throwable.toString());
                    }
                });
    }

    private void retry2() {
        executeTimes = 0;
        sourceMayFailThrow()
                .retryWhen(new RetryWhen(3, 500))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Toast.makeText(RxJavaDemo.this, "testRetry complete: " + integer,
                                Toast.LENGTH_SHORT).show();
                        Log.d("test", "testRetry complete: " + integer);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(RxJavaDemo.this, "testRetry error: " + throwable.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("test", "testRetry error: " + throwable.toString());
                    }
                });
    }
}
