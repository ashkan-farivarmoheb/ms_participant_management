package com.tis.ms.participant.repository.model;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
	
	@CreatedBy
	@Column("created_by")
	private String createdBy;
	
	@CreatedDate
	@Column("created_date")
	private Long createdDate;
	
	@LastModifiedBy
	@Column("last_modified_by")
	private String lastModifiedBy;
	
	@LastModifiedDate
	@Column("last_modified_date")
	private Long lastModifiedDate;

}
