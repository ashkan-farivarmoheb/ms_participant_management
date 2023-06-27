package com.tis.ms.participant.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.tis.ms.participant.repository.model.Provider;

@Repository
public interface ProviderRepository extends ReactiveCrudRepository<Provider, Long>{
}
