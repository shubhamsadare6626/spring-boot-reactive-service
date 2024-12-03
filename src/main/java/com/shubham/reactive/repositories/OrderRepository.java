package com.shubham.reactive.repositories;

import com.shubham.reactive.entities.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveCrudRepository<Order, String> {

  Flux<Order> findAllByClientId(String clientId);

  Flux<Order> searchOrderByStatus(String status);
}
