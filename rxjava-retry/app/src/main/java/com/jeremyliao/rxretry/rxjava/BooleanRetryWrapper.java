package com.jeremyliao.rxretry.rxjava;

import rx.Observable;

/**
 * Created by liaohailiang on 2018/8/10.
 */
public class BooleanRetryWrapper extends RetryWrapper<Boolean> {

    public BooleanRetryWrapper(Observable<Boolean> source) {
        super(source);
    }

    @Override
    public boolean checkResult(Boolean aBoolean) {
        return aBoolean;
    }
}
