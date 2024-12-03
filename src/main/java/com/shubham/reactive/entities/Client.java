package com.shubham.reactive.entities;

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
@Table("clients")
public class Client {

  @Id private String id;

  @Column("username")
  private String username;

  @Column("firstname")
  private String firstname;

  @Column("lastname")
  private String lastname;

  @Column("email")
  private String email;

  @Column("mobile_number")
  private String mobileNumber;

  @Column("street_address")
  private String streetAddress;

  @Column("age")
  private int age;

  @Column("migrated")
  private Boolean migrated;

  @Column("password")
  private String password;
}
