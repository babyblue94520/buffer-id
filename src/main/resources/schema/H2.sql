create table if not exists serial
(
    id          varchar(50)     not null,
    prefix      varchar(10)     default '0',
    `number`    bigint          default '0',
    primary key (id,prefix)
);
