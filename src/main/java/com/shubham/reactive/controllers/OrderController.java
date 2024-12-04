package com.shubham.reactive.controllers;

import com.shubham.reactive.dtos.OrderDto;
import com.shubham.reactive.entities.Order;
import com.shubham.reactive.services.OrderService;
import com.shubham.reactive.services.OrderService.ClientOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService orderService;

  @GetMapping("/{id}")
  public Mono<OrderDto> getOrderById(@PathVariable String id) {
    return orderService.findById(id);
  }

  @GetMapping
  public Flux<OrderDto> getAllOrders() {
    return orderService.getAllOrders();
  }

  // Stream event
  //  SSE streaming endpoint
  @GetMapping(path = "/client/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<OrderDto> getOrdersByClientId(@PathVariable String clientId) {
    return orderService.getOrdersByClientId(clientId);
  }

  @PostMapping
  public Mono<ClientOrderDto> createOrder(@RequestBody OrderDto requestDto) {
    return orderService.createOrder(requestDto);
  }

  @PutMapping("/{id}")
  public Mono<OrderDto> updateOrder(@PathVariable String id, @RequestBody OrderDto requestDto) {
    return orderService.updateOrder(id, requestDto);
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteOrder(@PathVariable String id) {
    return orderService.deleteOrder(id);
  }

  @GetMapping("/search/{status}")
  public Flux<Order> searchOrderByStatus(@PathVariable String status) {
    return orderService.searchOrderByStatus(status);
  }
}
