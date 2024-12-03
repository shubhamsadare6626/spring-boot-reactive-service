package com.shubham.reactive.repositories;

import com.shubham.reactive.entities.Client;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ClientRepository extends ReactiveCrudRepository<Client, String> {}
