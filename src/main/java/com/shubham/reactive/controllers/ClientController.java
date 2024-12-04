package com.shubham.reactive.controllers;

import com.shubham.reactive.dtos.ClientRequestDto;
import com.shubham.reactive.dtos.ClientResponseDto;
import com.shubham.reactive.services.ClientService;
import lombok.RequiredArgsConstructor;
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

@RequestMapping("/api/clients")
@RestController
@RequiredArgsConstructor
public class ClientController {

  private final ClientService clientService;

  @GetMapping("/{id}")
  public Mono<ClientResponseDto> getClientById(@PathVariable String id) {
    return clientService.findById(id);
  }

  @GetMapping
  public Flux<ClientResponseDto> getAllClients() {
    return clientService.getAllClients();
  }

  @GetMapping("/elder/{age}")
  public Flux<ClientResponseDto> getElderClients(@PathVariable String age) {
    return clientService.getElderClients(age);
  }

  @GetMapping("/migrated")
  public Flux<ClientResponseDto> getMigrated() {
    return clientService.getMigratedClients();
  }

  @PostMapping
  public Mono<ClientResponseDto> createClient(@RequestBody ClientRequestDto requestDto) {
    return clientService.createClient(requestDto);
  }

  @PutMapping("/{id}")
  public Mono<ClientResponseDto> updateClient(
      @PathVariable String id, @RequestBody ClientRequestDto requestDto) {
    return clientService.updateClient(id, requestDto);
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteClient(@PathVariable String id) {
    return clientService.deleteClient(id);
  }
}
