package com.shubham.reactive.utils;

import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class Assignment {

  public static <T> Subscriber<T> subscriber() {
    return new DefaultSubscriber<>();
  }

  public static void main(String[] args) {
    Flux.range(1, 10).log().delayElements(Duration.ofSeconds(1)).subscribe();
    SampleAsynchronus.sleepMilliSeconds(12);

    // Passing extended subscriber or default subscriber works same by doOnNext,doOnComplete
    // functions
    Flux.range(10, 5).subscribe(subscriber());

    Flux.range(10, 5)
        .doOnNext(x -> log.info(" x: {}", x))
        .doOnComplete((() -> log.info("completion")))
        .doOnError(x -> log.info("error: {}", x))
        .subscribe();

    //
    Mono<Integer> data = Mono.just(5);
    data.subscribe(); // subscriber and fetch data
    data.map(i -> i / 0)
        .subscribe( // subscribed and performed operation of runnable
            x -> log.debug("xx " + x),
            e -> log.error("error occured  :{}", e),
            () -> log.info("Completed.."));

    //    Mono/Flux conversion
    Flux<Integer> flux = Flux.from(data);
    flux.log().subscribe();

    List<String> list = List.of("abc", "def", "ghi");

    // subscribe(consumer,throwable,runnable,subscription);
    Flux.fromIterable(list)
        .log()
        .subscribe(
            i -> log.debug("i:{}", i), // consumer
            err -> log.error("error: {}", err), // Throwable
            () -> log.info("Completed............."), // Runnable
            sub -> sub.request(5)); // Subscription

    Flux.fromIterable(list).subscribe(subscriber());

    Integer[] num = {12, 13, 24, 25};
    Flux.fromArray(num).subscribe(subscriber());

    Flux.fromStream(list::stream);
    Flux.range(1, 2).subscribe(subscriber());
  }
}
