package com.shubham.reactive.utils;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SampleAsynchronus {

  public static <T> Subscriber<T> subscriber() {
    return new DefaultSubscriber<>();
  }
}
