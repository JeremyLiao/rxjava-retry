# rxjava-retry

#### 封装了几个处理RxJava Retry操作的类

## OverView
我们希望对RxJava失败的场景进行重试

### RxJava的流程失败一般有两种：

- 失败的时候抛出RuntimeException，以onError结束，如Retrofit网络请求
- 失败只是一个结果，不会抛出RuntimeException，最后会以onComplete结束

### 不抛出RuntimeException的场景
我们提供[RetryWrapper](https://github.com/JeremyLiao/rxjava-retry/blob/master/rxjava-retry/app/src/main/java/com/jeremyliao/rxjava_retry/RetryWrapper.java)进行处理

特别的，如果流程以Boolean返回标识成功或者失败，我们提供了[BooleanRetryWrapper](https://github.com/JeremyLiao/rxjava-retry/blob/master/rxjava-retry/app/src/main/java/com/jeremyliao/rxjava_retry/BooleanRetryWrapper.java)进行处理

### 会抛出RuntimeException的场景
我们提供[RetryWhen](https://github.com/JeremyLiao/rxjava-retry/blob/master/rxjava-retry/app/src/main/java/com/jeremyliao/rxjava_retry/RetryWhen.java)进行处理

## Demo
### 定义source
生成一个0-100的随机整数，小于50代表成功，大于50代表失败，这代表了成功率为50%。

定义两个源，一个以Boolean值返回成功失败，另一个失败的时候会抛出RuntimeException：

```java
Observable.just(new Random())
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
```

```java
Observable.just(new Random())
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
```
### 使用
- 不抛出RuntimeException的场景

```java
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
```
- 抛出RuntimeException的场景

```java
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
```
Demo代码：

[RxJavaDemo](https://github.com/JeremyLiao/rxjava-retry/blob/master/rxjava-retry/app/src/main/java/com/jeremyliao/rxjava_retry/RxJavaDemo.java)

