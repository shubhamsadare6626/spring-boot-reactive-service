package com.shubham.reactive.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("orders")
public class Order {

  @Id private String id;

  @Column("client_id")
  private String clientId;

  @Column("order_date")
  private LocalDateTime orderDate;

  @Column("status")
  private String status;

  @Column("total_amount")
  private Double totalAmount;

  @Column("billing_address")
  private String billingAddress;

  @Column("expected_delivery_date")
  private LocalDate expectedDeliveryDate;

  @Column("payment_method")
  private String paymentMethod;

  @Column("tracking_number")
  private String trackingNumber;
}
