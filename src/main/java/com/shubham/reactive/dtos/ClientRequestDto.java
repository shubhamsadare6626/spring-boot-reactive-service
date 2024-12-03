package com.shubham.reactive.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequestDto {
  private String username;
  private String firstname;
  private String lastname;
  private String email;
  private String mobileNumber;
  private String streetAddress;
  private int age;
  private Boolean migrated;
  private String password;
}
