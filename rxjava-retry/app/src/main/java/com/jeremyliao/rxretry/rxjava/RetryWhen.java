package com.jeremyliao.rxretry.rxjava;

import android.util.Pair;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by liaohailiang on 2018/8/10.
 */
public class RetryWhen implements Func1<Observable<? extends Throwable>, Observable<?>> {

    private final int retryTimes;
    private final int delayMillis;

    public RetryWhen(int retryTimes, int delayMillis) {
        this.retryTimes = retryTimes;
        this.delayMillis = delayMillis;
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> observable) {
        return observable.zipWith(Observable.range(1, retryTimes),
                new Func2<Throwable, Integer, Pair<Integer, Throwable>>() {
                    @Override
                    public Pair<Integer, Throwable> call(Throwable throwable, Integer integer) {
                        return Pair.create(integer, throwable);
                    }
                })
                .flatMap(new Func1<Pair<Integer, Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Pair<Integer, Throwable> pair) {
                        if (pair.first < retryTimes) {
                            return Observable.timer(delayMillis, TimeUnit.MILLISECONDS);
                        } else {
                            return Observable.error(pair.second);
                        }
                    }
                });
    }
}
