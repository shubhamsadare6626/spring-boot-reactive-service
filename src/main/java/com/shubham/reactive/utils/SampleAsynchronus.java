package com.shubham.reactive.utils;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SampleAsynchronus {

  public static <T> Subscriber<T> subscriber() {
    return new DefaultSubscriber<>();
  }

  public static void main(String[] args) {
    // Mono.just("Hello")
    // .map(x -> null)
    // .error(new ClassNotFoundException("Not available"))
    // .subscribe(subscriber());
    //

    getNames().subscribe(subscriber());

    //    Mono<String> deferredMono =
    //        Mono.defer(
    //            () -> {
    //              System.out.println("Creating Mono dynamically");
    //              return Mono.just("Hello, World!");
    //            });
    //
    //    // No subscription yet, so nothing happens
    //    System.out.println("No subscription yet");
    //
    //    // Subscription triggers the creation
    //    deferredMono.subscribe(System.out::println);

    Mono<String> eagerMono = Mono.just("Hello declared mono just");
    //     No subscription yet, so nothing happens
    System.out.println("No subscription yet");

    // Subscription triggers the creation
    eagerMono.subscribe(System.out::println);
  }

  public static String getUpperCase(String name) {
    return name.toUpperCase();
  }

  public static Mono<String> getNames() {
    String names = List.of("shubham", "akash", "Dinesh").toString();
    return Mono.fromSupplier(() -> getUpperCase(names));
  }
}
