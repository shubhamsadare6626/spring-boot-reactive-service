package com.shubham.reactive.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

  private String id;
  private String clientId;
  private LocalDateTime orderDate;
  private String status;
  private Double totalAmount;
  private String billingAddress;
  private String paymentMethod;
  private LocalDate expectedDeliveryDate;
  private String trackingNumber;
}
