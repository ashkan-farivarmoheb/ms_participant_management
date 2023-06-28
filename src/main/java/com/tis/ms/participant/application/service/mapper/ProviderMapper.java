package com.tis.ms.participant.application.service.mapper;

import java.math.BigDecimal;
import java.util.Optional;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import com.tis.ms.participant.repository.model.Provider;
import com.tis.ms.participant.repository.model.cache.ProviderCache;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, builder = @Builder(disableBuilder = true), imports = {
		Optional.class, BigDecimal.class })
public abstract class ProviderMapper {

	@Mappings(@Mapping(source = "id", target = "id"))
	public abstract ProviderCache toProviderCache(Provider provider);

	@Mappings(@Mapping(source = "id", target = "id"))
	public abstract Provider toProvider(ProviderCache cache);

	public abstract ProviderDto toProviderDto(ProviderCache cache);
}
