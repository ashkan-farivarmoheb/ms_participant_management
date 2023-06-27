package com.tis.ms.participant.application.service;

import org.springframework.stereotype.Service;

import com.tis.ms.participant.application.service.mapper.ProviderMapper;
import com.tis.ms.participant.repository.ProviderCacheRepository;
import com.tis.ms.participant.repository.ProviderRepository;
import com.tis.ms.participant.repository.model.Provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderService {
	
	private final ProviderRepository providerRepository;
	private final ProviderCacheRepository providerCacheRepository;
	private final ProviderMapper providerMapper;
	
	public Flux<Provider> getAll() {
		return providerCacheRepository.getAll()
		.flatMap(cache -> Flux.just(providerMapper.toProvider(cache)))
		.switchIfEmpty(providerRepository.findAll())
		.flatMap(provider -> Flux.just(providerMapper.toProviderCache(provider)))
		.flatMap(cache -> providerCacheRepository.saveAll(Flux.just(cache)))
		.flatMap(savedRresult -> Flux.just(providerMapper.toProvider(savedRresult)));
		
//		.flatMap(cache -> {
//			log.info("Ashkan: id={}, abn={}", cache.getId(), cache.getAbn());
//			providerCacheRepository.saveAll(Flux.just(cache));
//			return Flux.just(providerMapper.toProvider(cache));
//		});
	}
	
	public void saveAll(Flux<Provider> providers) {
//		List<Provider> persisted = new ArrayList<>();
//		providerRepository.saveAll(providers).subscribe(persisted::add);
//		
//		providerCacheRepository.saveAll(Flux.fromIterable(persisted));
	}

}
