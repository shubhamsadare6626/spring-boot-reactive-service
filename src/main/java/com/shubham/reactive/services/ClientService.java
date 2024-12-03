package com.shubham.reactive.services;

import com.shubham.reactive.dtos.ClientRequestDto;
import com.shubham.reactive.dtos.ClientResponseDto;
import com.shubham.reactive.entities.Client;
import com.shubham.reactive.mappers.ClientMapper;
import com.shubham.reactive.repositories.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class ClientService {

  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;

  public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
    this.clientRepository = clientRepository;
    this.clientMapper = clientMapper;
  }

  public Mono<ClientResponseDto> createClient(ClientRequestDto requestDto) {
    return Mono.defer(
            () -> {
              // Asynchronously build the entity to avoid blocking even during transformation.
              Client entity = buildEntity(requestDto);
              return clientRepository.save(entity);
            })
        .map(clientMapper::clientToResponse)
        .doOnSuccess(response -> log.info("Client created successfully: {}", response))
        .doOnError(error -> log.error("Error while creating client: {}", error.getMessage(), error))
        .onErrorResume(
            error ->
                Mono.error(new RuntimeException("Failed to create client: " + error.getMessage())));
  }

  public Mono<ClientResponseDto> findById(String userId) {
    return clientRepository
        .findById(userId)
        .switchIfEmpty(Mono.error(new RuntimeException("Client not found with id :" + userId)))
        .map(clientMapper::clientToResponse);
  }

  public Flux<ClientResponseDto> getAllClients() {
    return clientRepository.findAll().map(clientMapper::clientToResponse);
  }

  public Flux<ClientResponseDto> getElderClients() {
    return clientRepository
        .findAll()
        .map(clientMapper::clientToResponse)
        .filter(x -> x.getAge() > 30);
  }

  public Flux<ClientResponseDto> getMigrated() {

    Flux<Client> findAll = clientRepository.findAll();
    Flux<String> usernameList = findAll.flatMap(data -> Flux.just(data.getUsername()));
    usernameList.subscribe(log::info);
    return clientRepository
        .findAll()
        .map(clientMapper::clientToResponse)
        .filter(x -> x.getMigrated().equals(Boolean.TRUE));
  }

  public Mono<ClientResponseDto> updateClient(String id, ClientRequestDto requestDto) {

    return clientRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new RuntimeException("Client not found with id: " + id)))
        .flatMap(
            existingClient ->
                updateEntityAsync(existingClient, requestDto).flatMap(clientRepository::save))
        .map(clientMapper::clientToResponse)
        .doOnSuccess(response -> log.info("Client updated successfully: {}", response))
        .doOnError(error -> log.error("Error during client update: {}", error.getMessage(), error))
        .onErrorResume(
            e -> Mono.error(new RuntimeException("Failed to update client: " + e.getMessage())));
  }

  private Mono<Client> updateEntityAsync(Client existingClient, ClientRequestDto requestDto) {
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
        .subscribeOn(
            Schedulers.boundedElastic()); // Offload heavy computation to a bounded thread pool
  }

  public Mono<Void> deleteClient(String id) {
    return clientRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new RuntimeException("Client not found with id: " + id)))
        .flatMap(clientRepository::delete)
        .onErrorResume(
            error -> {
              log.error(
                  " Error while deleting client by id : {}. Reason: {} ",
                  id,
                  error.getMessage(),
                  error);
              return Mono.error(error);
            });
  }

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
}
