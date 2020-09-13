create table mp_loginlog
(
    id int auto_increment,
    mcid varchar(16) null,
	uuid varchar(36) null,
	address varchar(15) null
	constraint mp_loginlog_pk
		primary key (id)
);

create index mp_loginlog_mcid_uuid_index
    on mp_loginlog (mcid, uuid, address);