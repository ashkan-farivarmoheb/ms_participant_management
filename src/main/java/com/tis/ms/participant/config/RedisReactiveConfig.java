package com.tis.ms.participant.config;

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.tis.ms.participant.common.KryoSerializer;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ClientOptions.DisconnectedBehavior;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.resource.DefaultClientResources;

@Configuration
public class RedisReactiveConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Value("${spring.data.redis.password}")
	private String password;

	@Value("${spring.data.redis.connectionTimeOut:2000}")
	private long connectionTimeOut;

	@Value("${spring.data.redis.lettucePoolMinIdle:20}")
	private int lettucePoolMinIdle;

	@Value("${spring.data.redis.lettucePoolMaxIdle:150}")
	private int lettucePoolMaxIdle;

	@Value("${spring.data.redis.lettucePoolMaxTotal:150}")
	private int lettucePoolMaxTotal;

	@Bean(name = "reactiveRedisConnectionFactory")
	@ConditionalOnProperty(value = "spring.data.redis.clusterEnabled", havingValue = "true")
	ReactiveRedisConnectionFactory reactiveClusterRedisConnectionFactory() {
		RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
		clusterConfiguration.setPassword(RedisPassword.of(password));
		clusterConfiguration.clusterNode(host, port);

		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(clusterConfiguration,
				getCacheCLientConfig());
		lettuceConnectionFactory.setShareNativeConnection(false);
		lettuceConnectionFactory.setPipeliningFlushPolicy(LettuceConnection.PipeliningFlushPolicy.buffered(50));
		lettuceConnectionFactory.afterPropertiesSet();
		return lettuceConnectionFactory;
	}

	@Bean(name = "reactiveRedisConnectionFactory")
	@ConditionalOnProperty(value = "spring.data.redis.clusterEnabled", havingValue = "false")
	ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
		RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(host, port);
		standaloneConfiguration.setPassword(RedisPassword.of(password));
		return new LettuceConnectionFactory(standaloneConfiguration);
	}

	@Bean(name = "reactiveRedisTemplate")
	ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
			ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {

		KryoSerializer kryoSerializer = new KryoSerializer();
		JdkSerializationRedisSerializer valueSerializer = new JdkSerializationRedisSerializer(
				new SerializingConverter(kryoSerializer), new DeserializingConverter(kryoSerializer));
		StringRedisSerializer keySerializer = new StringRedisSerializer();

		RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder = RedisSerializationContext
				.newSerializationContext(keySerializer);
		RedisSerializationContext<String, Object> context = builder.value(valueSerializer).build();

		return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
	}

	private LettuceClientConfiguration getCacheCLientConfig() {
		return LettucePoolingClientConfiguration.builder().clientResources(DefaultClientResources.create())
				.clientOptions(ClientOptions.builder().autoReconnect(true)
						.disconnectedBehavior(DisconnectedBehavior.REJECT_COMMANDS)
						.timeoutOptions(
								TimeoutOptions.builder().fixedTimeout(Duration.ofMillis(connectionTimeOut)).build())
						.build())
				.poolConfig(getPoolConfig()).build();
	}

	private GenericObjectPoolConfig getPoolConfig() {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMinIdle(lettucePoolMinIdle);
		poolConfig.setMaxIdle(lettucePoolMaxIdle);
		poolConfig.setMaxTotal(lettucePoolMaxTotal);
		return poolConfig;
	}
}
