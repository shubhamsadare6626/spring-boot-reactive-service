package com.shubham.reactive.services;

import com.shubham.reactive.adapter.VonageAdapter;
import com.shubham.reactive.dtos.OrderDto;
import com.shubham.reactive.entities.Client;
import com.shubham.reactive.entities.Order;
import com.shubham.reactive.repositories.ClientRepository;
import com.shubham.reactive.repositories.OrderRepository;
import com.shubham.reactive.utils.SampleAsynchronus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  @Value("${sms.message}")
  private String messageFormat;

  public static final String FROM = "ABC Distributors";
  private final OrderRepository orderRepository;
  private final ClientRepository clientRepository;
  private final VonageAdapter vonageAdapter;

  public Mono<ClientOrderDto> createOrder(OrderDto requestDto) {
    return validateClient(requestDto.getClientId())
        .flatMap(client -> saveOrder(requestDto, client))
        .flatMap(
            orderDto ->
                sendOrderSuccessNotification(orderDto)
                    .thenReturn(orderDto) // Chain the notification logic
            )
        .doOnError(this::logError)
        .onErrorResume(this::handleError);
  }

  private Mono<Client> validateClient(String clientId) {
    return clientRepository
        .findById(clientId)
        .switchIfEmpty(Mono.error(new RuntimeException("Client not found with ID: " + clientId)));
  }

  private Mono<ClientOrderDto> saveOrder(OrderDto requestDto, Client client) {
    Order order = buildOrderEntity(requestDto);
    return orderRepository.save(order).map(savedOrder -> buildClientOrderDto(savedOrder, client));
  }

  private Mono<Void> sendOrderSuccessNotification(ClientOrderDto dto) {
    String message =
        String.format(
            messageFormat,
            dto.getUsername(),
            dto.getTrackNumber(),
            dto.getTotalAmount(),
            dto.getBillingAddress());
    log.info("Notification Thread :{}", Thread.currentThread().getName());
    return vonageAdapter
        .sendSms(FROM, dto.getMobileNumber(), message)
        .doOnSuccess(
            ignored -> log.info("Order success SMS sent for order: {}", dto.getTrackNumber()))
        .doOnError(
            error -> log.error("Failed to send SMS for order {}: {}", dto, error.getMessage()))
        .then();
  }

  public Mono<OrderDto> findById(String id) {
    return orderRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new RuntimeException("Order not found for id: " + id)))
        .map(this::toResponseDto)
        .log();
  }

  public Flux<OrderDto> getAllOrders() {
    log.info(
        "Thread name: {} && active {} ", Thread.currentThread().getName(), Thread.activeCount());
    SampleAsynchronus.sleepMilliSeconds(2000);
    return orderRepository
        .findAll()
        .map(this::toResponseDto)
        .delayElements(Duration.ofMillis(250)) // half half second
        .log();
  }

  public Flux<Order> searchOrderByStatus(String status) {
    return orderRepository.searchOrderByStatus(status);
  }

  public Flux<OrderDto> getOrdersByClientId(String clientId) {
    log.info(
        "Thread :{} & active count :{}", Thread.currentThread().getName(), Thread.activeCount());
    return orderRepository
        .findAllByClientId(clientId)
        .switchIfEmpty(
            Flux.error(new RuntimeException("No orders found for client id: " + clientId)))
        .map(this::toResponseDto)
        .delayElements(Duration.ofMillis(1000))
        .log();
  }

  public Mono<OrderDto> updateOrder(String id, OrderDto requestDto) {
    return orderRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new RuntimeException("Order not found with id: " + id)))
        .flatMap(
            existingOrder ->
                updateOrderEntityAsync(existingOrder, requestDto).flatMap(orderRepository::save))
        .map(this::toResponseDto)
        .doOnSuccess(response -> log.info("Order updated successfully: {}", response))
        .doOnError(this::logError)
        .onErrorResume(
            error ->
                Mono.error(new RuntimeException("Failed to update order: " + error.getMessage())));
  }

  public Mono<Void> deleteOrder(String id) {
    return orderRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new RuntimeException("Order not found with id: " + id)))
        .flatMap(orderRepository::delete)
        .doOnSuccess(aVoid -> log.info("Order deleted successfully with id: {}", id))
        .doOnError(this::logError);
  }

  private Mono<Order> updateOrderEntityAsync(Order existingOrder, OrderDto requestDto) {
    return Mono.fromCallable(
            () -> {
              existingOrder.setClientId(requestDto.getClientId());
              existingOrder.setOrderDate(requestDto.getOrderDate());
              existingOrder.setStatus(requestDto.getStatus());
              existingOrder.setTotalAmount(requestDto.getTotalAmount());
              return existingOrder;
            })
        .subscribeOn(Schedulers.boundedElastic());
  }

  private void logError(Throwable error) {
    log.error("Error while Performing order: {}", error.getMessage(), error);
  }

  private Mono<ClientOrderDto> handleError(Throwable error) {
    return Mono.error(new RuntimeException("Failed to create order: " + error.getMessage()));
  }

  private Order buildOrderEntity(OrderDto requestDto) {
    return Order.builder()
        .clientId(requestDto.getClientId())
        .orderDate(LocalDateTime.now())
        .status(requestDto.getStatus())
        .totalAmount(requestDto.getTotalAmount())
        .trackingNumber(generateTrackingNumber())
        .billingAddress(requestDto.getBillingAddress())
        .paymentMethod(requestDto.getPaymentMethod())
        .expectedDeliveryDate(LocalDateTime.now().plusDays(6).toLocalDate()) // After 6 days
        .build();
  }

  private OrderDto toResponseDto(Order order) {
    return OrderDto.builder()
        .id(order.getId())
        .clientId(order.getClientId())
        .orderDate(order.getOrderDate())
        .status(order.getStatus())
        .totalAmount(order.getTotalAmount())
        .trackingNumber(order.getTrackingNumber())
        .expectedDeliveryDate(order.getExpectedDeliveryDate())
        .billingAddress(order.getBillingAddress())
        .paymentMethod(order.getPaymentMethod())
        .build();
  }

  private ClientOrderDto buildClientOrderDto(Order order, Client client) {
    return ClientOrderDto.builder()
        .trackNumber(order.getTrackingNumber())
        .status(order.getStatus())
        .clientId(client.getId())
        .email(client.getEmail())
        .mobileNumber(client.getMobileNumber())
        .username(client.getUsername())
        .totalAmount(order.getTotalAmount())
        .billingAddress(order.getBillingAddress())
        .build();
  }

  public static String generateTrackingNumber() {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder trackingNumber = new StringBuilder(8);

    for (int i = 0; i < 8; i++) {
      trackingNumber.append(characters.charAt(new Random().nextInt(characters.length())));
    }

    return trackingNumber.toString();
  }

  @Data
  @AllArgsConstructor
  @Builder
  @NoArgsConstructor
  public static class ClientOrderDto {
    private String trackNumber;
    private String clientId;
    private String status;
    private String email;
    private String mobileNumber;
    private String username;
    private Double totalAmount;
    private String billingAddress;
  }
}
