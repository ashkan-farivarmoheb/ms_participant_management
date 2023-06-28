package com.tis.ms.participant.application.service;

import org.springframework.stereotype.Service;

import com.tis.ms.participant.application.service.mapper.ProviderDto;
import com.tis.ms.participant.application.service.mapper.ProviderMapper;
import com.tis.ms.participant.repository.ProviderCacheRepository;
import com.tis.ms.participant.repository.ProviderRepository;
import com.tis.ms.participant.repository.model.Provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderService {

	private final ProviderRepository providerRepository;
	private final ProviderCacheRepository providerCacheRepository;
	private final ProviderMapper providerMapper;

	public Flux<ProviderDto> getAll() {
		return providerCacheRepository.getAll()
				.flatMap(cache -> Flux.just(providerMapper.toProvider(cache)))
				.switchIfEmpty(providerRepository.findAll())
				.flatMap(provider -> Flux.just(providerMapper.toProviderCache(provider)))
				.flatMap(cache -> providerCacheRepository.saveAll(Flux.just(cache)))
				.flatMap(savedRresult -> Flux.just(providerMapper.toProviderDto(savedRresult)));
	}

	public Mono<ProviderDto> save(Provider provider) {
		return providerRepository.save(provider)
				.flatMap(p -> Mono.just(providerMapper.toProviderCache(p)))
				.flatMap(providerCacheRepository::save)
				.flatMap(p -> Mono.just(providerMapper.toProviderDto(p)));
	}

}
