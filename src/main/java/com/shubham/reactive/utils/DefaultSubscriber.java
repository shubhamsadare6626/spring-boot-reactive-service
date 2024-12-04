package com.shubham.reactive.utils;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
public class DefaultSubscriber<T> implements Subscriber<T> {

  @Override
  public void onSubscribe(Subscription s) {
    s.request(Long.MAX_VALUE);
  }

  @Override
  public void onNext(T t) {
    log.info("onNext- :{}", t);
  }

  @Override
  public void onError(Throwable t) {
    log.info("onError- : {}", t);
  }

  @Override
  public void onComplete() {
    log.info("onComplete- : Completed");
  }
}
