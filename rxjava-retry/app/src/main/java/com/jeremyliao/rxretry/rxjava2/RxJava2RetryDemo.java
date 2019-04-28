package com.jeremyliao.rxretry.rxjava2;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class RxJava2RetryDemo {

    private int executeTimes = 0;


    public void testRetry1() {
        retry1();
    }

    public void testRetry2() {
        retry2();
    }

    private Observable<Boolean> sourceMayFail() {
        return Observable.just(new Random())
                .map(new Function<Random, Integer>() {
                    @Override
                    public Integer apply(Random random) throws Exception {
                        return random.nextInt(100);
                    }
                })
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        Log.d("test", "executeTimes: " + executeTimes++);
                        return integer < 50;
                    }
                });
    }

    private Observable<Integer> sourceMayFailThrow() {
        return Observable.just(new Random())
                .map(new Function<Random, Integer>() {
                    @Override
                    public Integer apply(Random random) throws Exception {
                        return random.nextInt(100);
                    }
                })
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d("test", "executeTimes: " + executeTimes++);
                        if (integer > 50) {
                            throw new RuntimeException();
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void retry1() {
        executeTimes = 0;
        new BooleanRetryWrapper(sourceMayFail())
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.d("test", "testRetry complete: " + aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("test", "testRetry error: " + throwable.toString());
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void retry2() {
        sourceMayFailThrow()
                .retryWhen(new RetryWhen(3, 500))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d("test", "testRetry complete: " + integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("test", "testRetry error: " + throwable.toString());
                    }
                });
    }
}
