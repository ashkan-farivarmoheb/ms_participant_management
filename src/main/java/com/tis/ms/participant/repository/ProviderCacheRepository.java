package com.tis.ms.participant.repository;

import java.time.Duration;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnection.KeyCommand;
import org.springframework.data.redis.connection.ReactiveStringCommands.SetCommand;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.stereotype.Repository;

import com.tis.ms.participant.repository.model.cache.ProviderCache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProviderCacheRepository {

	private static final String KEYSPACE = "Provider";
	private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

	@Value("${spring.data.redis.provider.ttl}")
	private long timeToLeave;

	public Flux<ProviderCache> saveAll(Flux<ProviderCache> providers) {

		long start = System.currentTimeMillis();
		ReactiveRedisConnection connection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
		SerializationPair<String> keySerializer = reactiveRedisTemplate.getSerializationContext()
				.getKeySerializationPair();
		SerializationPair<Object> valueSerializer = reactiveRedisTemplate.getSerializationContext()
				.getValueSerializationPair();

		var setcommands = providers.flatMap(provider -> {
			String key = createKey().apply(provider.getId().toString());
			return Flux.just(SetCommand.set(keySerializer.write(key)).value(valueSerializer.write(provider))
					.expiring(Expiration.from(Duration.ofSeconds(timeToLeave)))
					.withSetOption(RedisStringCommands.SetOption.ifAbsent()));
		});

		return connection.stringCommands().set(setcommands)
				.flatMap(response -> Flux.just(valueSerializer.read(response.getInput().getValue())))
				.flatMap(object -> Flux.just(ProviderCache.class.cast(object)))
				.doOnError(e -> log.error("message=\" Couldn't saveAll to Cache\", \"{}\"", e))
				.doOnComplete(() -> log.info("message=\"Invoked saveAll\", elapsed_time_ms=\"{}\"",
						System.currentTimeMillis() - start));
	}

	public Mono<ProviderCache> save(ProviderCache provider) {

		long start = System.currentTimeMillis();
		ReactiveRedisConnection connection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
		SerializationPair<String> keySerializer = reactiveRedisTemplate.getSerializationContext()
				.getKeySerializationPair();
		SerializationPair<Object> valueSerializer = reactiveRedisTemplate.getSerializationContext()
				.getValueSerializationPair();

		String key = createKey().apply(provider.getId().toString());
		var setcommand = Mono.just(SetCommand.set(keySerializer.write(key)).value(valueSerializer.write(provider))
				.expiring(Expiration.from(Duration.ofSeconds(timeToLeave)))
				.withSetOption(RedisStringCommands.SetOption.ifAbsent()));

		return Mono.from(connection.stringCommands().set(setcommand)
				.flatMap(response -> Flux.just(valueSerializer.read(response.getInput().getValue())))
				.flatMap(object -> Flux.just(ProviderCache.class.cast(object)))
				.doOnError(e -> log.error("message=\" Couldn't save to Cache\", \"{}\"", e))
				.doOnComplete(() -> log.info("message=\"Invoked save \", elapsed_time_ms=\"{}\"",
						System.currentTimeMillis() - start)));
	}

	public Flux<ProviderCache> getAll() {

		long start = System.currentTimeMillis();
		ReactiveRedisConnection connection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
		SerializationPair<String> keySerializer = reactiveRedisTemplate.getSerializationContext()
				.getKeySerializationPair();
		SerializationPair<Object> valueSerializer = reactiveRedisTemplate.getSerializationContext()
				.getValueSerializationPair();

		return reactiveRedisTemplate.keys(KEYSPACE + ":*")
				.flatMap(key -> connection.stringCommands().get(Mono.just(new KeyCommand(keySerializer.write(key)))))
				.flatMap(result -> Flux.just(valueSerializer.read(result.getOutput())))
				.flatMap(object -> Flux.just(ProviderCache.class.cast(object)))
				.doOnError(e -> log.error("message=\" Couldn't fetch from Cache\", \"{}\"", e))
				.doOnComplete(() -> log.info("message=\"Invoked getAll\", elapsed_time_ms=\"{}\"",
						System.currentTimeMillis() - start));

	}

	public Mono<Void> delete(Flux<ProviderCache> provider) {
		long start = System.currentTimeMillis();

		return provider.flatMap(p -> {
			String key = createKey().apply(p.getId().toString());
			return reactiveRedisTemplate.delete(key);
		}).doOnError(e -> log.error("message=\" Couldn't delete from Cache\", \"{}\"", e)).doOnComplete(() -> log
				.info("message=\"Invoked delete\", elapsed_time_ms=\"{}\"", System.currentTimeMillis() - start)).then();
	}

	private Function<String, String> createKey() {
		return key -> KEYSPACE + ":" + key;
	}
}
