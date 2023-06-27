package com.tis.ms.participant.repository.model.cache;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.tis.ms.participant.repository.model.OrganisationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@RedisHash("Provider")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCache implements Serializable {

	private static final long serialVersionUID = 2015371531257603634L;
	
	@Id
	private Long id;	
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
