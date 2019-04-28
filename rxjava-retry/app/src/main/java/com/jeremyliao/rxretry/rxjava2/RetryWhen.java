package com.jeremyliao.rxretry.rxjava2;

import android.util.Pair;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;


/**
 * Created by liaohailiang on 2018/8/10.
 */
public class RetryWhen implements Function<Observable<Throwable>, ObservableSource<?>> {

    private final int retryTimes;
    private final int delayMillis;

    public RetryWhen(int retryTimes, int delayMillis) {
        this.retryTimes = retryTimes;
        this.delayMillis = delayMillis;
    }

    @Override
    public ObservableSource<?> apply(Observable<Throwable> observable) throws Exception {
        return observable.zipWith(Observable.range(1, retryTimes),
                new BiFunction<Throwable, Integer, Pair<Integer, Throwable>>() {
                    @Override
                    public Pair<Integer, Throwable> apply(Throwable throwable, Integer integer) throws Exception {
                        return Pair.create(integer, throwable);
                    }
                })
                .flatMap(new Function<Pair<Integer, Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Pair<Integer, Throwable> pair) throws Exception {
                        if (pair.first < retryTimes) {
                            return Observable.timer(delayMillis, TimeUnit.MILLISECONDS);
                        } else {
                            return Observable.error(pair.second);
                        }
                    }
                });
    }
}
