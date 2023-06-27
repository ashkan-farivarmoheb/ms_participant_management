create schema if not exists ms_participant_management;

create table if not exists ms_participant_management.provider (
	id SERIAL PRIMARY KEY,
	constraint provider_uk unique(id),
	name text, 
	type varchar(255),
	abn varchar(11),
	phone text,
	fax text,
	address1 text,
	address2 text,
	address3 text,
	state varchar(3),
	suburb varchar(30),
	postcode smallint,
	web_address text,
	email text,
	anzsic text,
	facebook text,
	twitter text,
	linkedin text,
	created_by TEXT NOT NULL,
    created_date bigint NOT NULL,
    last_modified_date bigint NOT NULL,
    last_modified_by TEXT NOT NULL,
    updated_by TEXT
);

Create index idx_provider_abn
	on ms_participant_management.provider(abn);
	
Create index idx_provider_name
	on ms_participant_management.provider(name)