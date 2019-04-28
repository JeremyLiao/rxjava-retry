package com.jeremyliao.rxretry.rxjava;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by liaohailiang on 2018/8/8.
 */
public abstract class RetryWrapper<T> {

    private Observable<T> source;
    private int retryCount = 0;

    public RetryWrapper(Observable<T> source) {
        this.source = source;
    }

    public Observable<T> retry(final int retryTimes) {
        retryCount = 0;
        return source
                .doOnNext(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        if (checkResult(t)) {
                            return;
                        }
                        retryCount++;
                        if (retryCount < retryTimes) {
                            throw new ResultFailException();
                        }
                    }
                })
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Throwable> observable) {
                        return observable.zipWith(Observable.range(1, retryTimes),
                                new Func2<Throwable, Integer, Integer>() {
                                    @Override
                                    public Integer call(Throwable throwable, Integer integer) {
                                        return integer;
                                    }
                                });
                    }
                });
    }

    abstract public boolean checkResult(T t);

    private static class ResultFailException extends RuntimeException {
    }
}
