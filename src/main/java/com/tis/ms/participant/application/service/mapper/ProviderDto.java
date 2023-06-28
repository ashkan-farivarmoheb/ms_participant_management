package com.tis.ms.participant.application.service.mapper;

import com.tis.ms.participant.repository.model.OrganisationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDto {

	private String name;
	private OrganisationType type;
	private String abn;
	private String phone;
	private String fax;
	private String address1;
	private String address2;
	private String address3;
	private String state;
	private String suburb;
	private int postcode;
	private String webAddress;
	private String email;
	private String anzsic;
	private String facebook;
	private String twitter;
	private String linkedin;
}
