package com.tis.ms.participantmgmt.component.repository;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ScriptUtils;

import com.tis.ms.participant.repository.ProviderRepository;
import com.tis.ms.participant.repository.model.Provider;
import com.tis.ms.participantmgmt.component.common.ComponentTest;

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ComponentTest
public class ProviderRepositoryTest {

	@Autowired
	private ProviderRepository providerRepository;

	@Autowired
	private ConnectionFactory connectionFactory;

	private void executeScriptBlocking(final Resource sqlScript) {
		Mono.from(connectionFactory.create()).flatMap(connection -> ScriptUtils.executeSqlScript(connection, sqlScript))
		.block();
	}

	@BeforeEach
	@AfterEach
	private void cleanTestData(@Value("classpath:/db/scripts/cleanup_provider.sql") Resource script) {
		executeScriptBlocking(script);
	}

	@Test
	void saveAll_validDate_shouldSuccess() {

		Flux<Provider> providers = Flux.just(Provider.builder().abn("abn123").postcode(3134).build(),
				Provider.builder().abn("abn456").postcode(3109).build());

		StepVerifier.create(providerRepository.saveAll(providers)).thenConsumeWhile(savedObject -> {
			assertNotNull(savedObject);
			assertNotNull(savedObject.getId());
			return true;
		}).verifyComplete();
	}

}
