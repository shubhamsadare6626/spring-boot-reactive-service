package com.shubham.reactive.mappers;

import com.shubham.reactive.dtos.ClientResponseDto;
import com.shubham.reactive.entities.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

  public ClientResponseDto clientToResponse(Client client) {
    return new ClientResponseDto(
        client.getId(),
        client.getUsername(),
        client.getFirstname(),
        client.getLastname(),
        client.getEmail(),
        client.getMobileNumber(),
        client.getStreetAddress(),
        client.getAge(),
        client.getMigrated());
  }
}
