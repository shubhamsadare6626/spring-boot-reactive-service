package com.shubham.reactive.adapter;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.messages.TextMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class VonageAdapter {

  @Value("${nexmo.api.key}")
  private String nexmoApiKey;

  @Value("${nexmo.api.secret}")
  private String nexmoApiSecret;

  private VonageClient vonageClient;

  @PostConstruct
  public void doInit() {
    vonageClient = VonageClient.builder().apiKey(nexmoApiKey).apiSecret(nexmoApiSecret).build();
  }

  public Mono<Void> sendSms(String from, String to, String message) {
    log.info("order Thread :{}", Thread.currentThread().getName());
    return Mono.fromCallable(
            () -> {
              TextMessage smsMessage = new TextMessage(from, to, message);
              return vonageClient.getSmsClient().submitMessage(smsMessage);
            })
        .subscribeOn(Schedulers.boundedElastic())
        .doOnSuccess(response -> log.info("Response {} ", response.getMessages()))
        .doOnError(error -> log.error("Error while sending SMS: {}", error.getMessage(), error))
        .then();
  }
}
