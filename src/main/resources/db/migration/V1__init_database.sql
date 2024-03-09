create sequence user_seq start with 1 increment by 50;

create table public.user (
                             birthdate date,
                             id integer not null,
                             email varchar(255) not null unique,
                             firstname varchar(255) not null,
                             gender varchar(255) check (gender in ('MALE','FEMALE')),
                             lastname varchar(255) not null,
                             password varchar(255),
                             is_temp_password boolean not null,
                             role varchar(255) check (role in ('USER','ADMIN')),
                             primary key (id)
);

