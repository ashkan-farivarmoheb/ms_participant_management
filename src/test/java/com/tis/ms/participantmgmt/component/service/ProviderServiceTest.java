package com.tis.ms.participantmgmt.component.service;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ScriptUtils;

import com.tis.ms.participant.application.service.ProviderService;
import com.tis.ms.participantmgmt.component.common.ComponentTest;

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ComponentTest
public class ProviderServiceTest {
	
	@Autowired
	private ProviderService providerService;
	
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
	void getAll_emptyCache_returnDataFromDb(@Value("classpath:/db/scripts/provider_data.sql") Resource script) {
		
		executeScriptBlocking(script);
		
		StepVerifier.create(providerService.getAll())
		.thenConsumeWhile(savedObject -> {
			assertNotNull(savedObject);
			assertNotNull(savedObject.getId());
			assertNotNull(savedObject.getAbn());
			return true;
		}).verifyComplete();
		;
	}

}
