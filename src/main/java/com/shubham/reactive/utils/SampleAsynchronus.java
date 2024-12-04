package com.shubham.reactive.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleAsynchronus {

  public static <T> Subscriber<T> subscriber() {
    return new DefaultSubscriber<>();
  }

  public static void sleepMilliSeconds(int milliSeconds) {
    try {
      Thread.sleep(milliSeconds);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
