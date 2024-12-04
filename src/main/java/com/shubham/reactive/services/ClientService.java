package com.shubham.reactive.services;

import com.shubham.reactive.dtos.ClientRequestDto;
import com.shubham.reactive.dtos.ClientResponseDto;
import com.shubham.reactive.entities.Client;
import com.shubham.reactive.mappers.ClientMapper;
import com.shubham.reactive.repositories.ClientRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;

  // Create Client
  public Mono<ClientResponseDto> createClient(ClientRequestDto requestDto) {
    return Mono.just(requestDto)
        .map(this::buildEntity)
        .flatMap(clientRepository::save)
        .map(clientMapper::clientToResponse)
        .doOnSuccess(response -> log.info("Client created successfully: {}", response))
        .doOnError(error -> log.error("Error creating client: {}", error.getMessage(), error))
        .onErrorResume(
            error ->
                Mono.error(new RuntimeException("Failed to create client: " + error.getMessage())));
  }

  // Get Client by ID
  public Mono<ClientResponseDto> findById(String userId) {
    return clientRepository
        .findById(userId)
        .switchIfEmpty(Mono.error(new RuntimeException("Client not found with ID: " + userId)))
        .map(clientMapper::clientToResponse)
        .doOnError(error -> log.error("Error fetching client: {}", error.getMessage(), error));
  }

  // Get All Clients
  public Flux<ClientResponseDto> getAllClients() {
    return clientRepository
        .findAll()
        .delayElements(Duration.ofMillis(1500))
        .map(clientMapper::clientToResponse)
        .doOnError(error -> log.error("Error fetching all clients: {}", error.getMessage(), error));
  }

  // Elder Clients
  public Flux<ClientResponseDto> getElderClients(String age) {
    return clientRepository
        .findAll()
        .filter(client -> client.getAge() > Integer.parseInt(age))
        .flatMap(this::convertToLowercaseAsync)
        .log()
        .delayElements(Duration.ofMillis(1000))
        .map(clientMapper::clientToResponse)
        .doOnError(
            error -> log.error("Error fetching elder clients: {}", error.getMessage(), error));
  }

  // Migrated Clients
  public Flux<ClientResponseDto> getMigratedClients() {
    return clientRepository
        .findAll()
        .filter(Client::getMigrated)
        .delayElements(Duration.ofMillis(750))
        .map(clientMapper::clientToResponse)
        .doOnError(
            error -> log.error("Error fetching migrated clients: {}", error.getMessage(), error));
  }

  // Update Client
  public Mono<ClientResponseDto> updateClient(String id, ClientRequestDto requestDto) {
    return clientRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new RuntimeException("Client not found with Id: " + id)))
        .flatMap(existingClient -> updateEntity(existingClient, requestDto))
        .flatMap(clientRepository::save)
        .map(clientMapper::clientToResponse)
        .doOnSuccess(response -> log.info("Client updated successfully: {}", response))
        .doOnError(error -> log.error("Error updating client: {}", error.getMessage(), error))
        .onErrorResume(
            error ->
                Mono.error(new RuntimeException("Failed to update client: " + error.getMessage())));
  }

  // Delete Client
  public Mono<Void> deleteClient(String id) {
    return clientRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new RuntimeException("Client not found with ID: " + id)))
        .flatMap(clientRepository::delete)
        .doOnSuccess(x -> log.info("Client deleted successfully with ID: {}", id))
        .doOnError(error -> log.error("Error deleting client: {}", error.getMessage(), error));
  }

  // Helper Methods
  private Client buildEntity(ClientRequestDto requestDto) {
    return Client.builder()
        .username(requestDto.getUsername())
        .firstname(requestDto.getFirstname())
        .lastname(requestDto.getLastname())
        .email(requestDto.getEmail())
        .mobileNumber(requestDto.getMobileNumber())
        .streetAddress(requestDto.getStreetAddress())
        .age(requestDto.getAge())
        .migrated(requestDto.getMigrated())
        .password(requestDto.getPassword())
        .build();
  }

  private Mono<Client> updateEntity(Client existingClient, ClientRequestDto requestDto) {
    return Mono.fromCallable(
            () -> {
              existingClient.setUsername(requestDto.getUsername());
              existingClient.setFirstname(requestDto.getFirstname());
              existingClient.setLastname(requestDto.getLastname());
              existingClient.setEmail(requestDto.getEmail());
              existingClient.setMobileNumber(requestDto.getMobileNumber());
              existingClient.setStreetAddress(requestDto.getStreetAddress());
              existingClient.setAge(requestDto.getAge());
              existingClient.setMigrated(requestDto.getMigrated());
              return existingClient;
            })
        .subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<Client> convertToLowercaseAsync(Client client) {
    return Mono.fromCallable(
            () -> {
              client.setUsername(client.getUsername().toLowerCase());
              return client;
            })
        .subscribeOn(Schedulers.boundedElastic());
  }
}
