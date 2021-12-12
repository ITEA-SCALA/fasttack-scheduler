create table public.device_info
(
  token_ref_id       varchar(64) not null
    constraint device_info_pkey
      primary key,
  device_name        varchar(128),
  serial_number      varchar(64),
  os_name            varchar(64),
  os_version         varchar(64),
  imei               varchar(64),
  storage_technology varchar(255),
  device_type        varchar(64)
);
