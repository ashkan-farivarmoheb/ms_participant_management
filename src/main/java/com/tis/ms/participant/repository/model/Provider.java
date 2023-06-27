package com.tis.ms.participant.repository.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "provider")
public class Provider extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column
	private String name;
	
	@Column
	private OrganisationType type;
	
	@Column
	private String abn;
	
	@Column
	private String phone;
	
	@Column
	private String fax;
	
	@Column
	private String address1;
	
	@Column
	private String address2;
	
	@Column
	private String address3;
	
	@Column
	private String state;
	
	@Column
	private String suburb;
	
	@Column
	private int postcode;
	
	@Column
	private String webAddress;
	
	@Column
	private String email;
	
	@Column
	private String anzsic;
	
	@Column
	private String facebook;
	
	@Column
	private String twitter;
	
	@Column
	private String linkedin;
	
}
