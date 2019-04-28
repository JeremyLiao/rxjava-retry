package com.jeremyliao.rxretry.rxjava2;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

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
                .doOnNext(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        if (checkResult(t)) {
                            return;
                        }
                        retryCount++;
                        if (retryCount < retryTimes) {
                            throw new ResultFailException();
                        }
                    }
                })
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> observable) throws Exception {
                        return observable.zipWith(Observable.range(1, retryTimes),
                                new BiFunction<Throwable, Integer, Integer>() {
                                    @Override
                                    public Integer apply(Throwable throwable, Integer integer) throws Exception {
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
