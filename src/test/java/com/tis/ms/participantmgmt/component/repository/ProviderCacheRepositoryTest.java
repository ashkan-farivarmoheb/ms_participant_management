package com.tis.ms.participantmgmt.component.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveRedisConnection.KeyCommand;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import com.tis.ms.participant.repository.ProviderCacheRepository;
import com.tis.ms.participant.repository.model.cache.ProviderCache;
import com.tis.ms.participantmgmt.component.common.ComponentTest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ComponentTest
public class ProviderCacheRepositoryTest {

	@Autowired
	private ProviderCacheRepository providerCacheRepository;

	@Autowired
	private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

	@BeforeEach
	void init() {
		deleteAll();
	}

	@AfterEach
	void post() {
		deleteAll();
	}

	@Test
	void saveAll_validData_shouldSuccess() {
		Flux<ProviderCache> providers = Flux
				.fromIterable(Arrays.asList(ProviderCache.builder().id(12345690l).abn("abn4").build(),
						ProviderCache.builder().id(123456778l).abn("abn3").build()));

		StepVerifier.create(providerCacheRepository.saveAll(providers)).thenConsumeWhile(cachedList -> {
			assertNotNull(cachedList);
			assertNotNull(cachedList.getAbn());
			return true;
		}).verifyComplete();

		StepVerifier.create(providerCacheRepository.getAll()).thenConsumeWhile(cachedList -> {
			assertNotNull(cachedList);
			assertNotNull(cachedList.getId());
			return true;
		}).verifyComplete();
	}
	
	private void deleteAll() {
		SerializationPair<String> keySerializer = reactiveRedisTemplate.getSerializationContext()
				.getKeySerializationPair();

		reactiveRedisTemplate.keys("*").flatMap(key -> reactiveRedisTemplate.getConnectionFactory()
				.getReactiveConnection().keyCommands().del(Mono.just(new KeyCommand(keySerializer.write(key))))).blockFirst();
	}
}
