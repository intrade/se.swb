create table consumption (
id bigint not null,
date date,
driver_id bigint not null,
price decimal(19,2),
type varchar(255),
volume double not null,
primary key (id));

create sequence seq_cons start with 1 increment by 10;

insert into consumption (date, driver_id, price, volume, type, id) values ('2020-01-15', 15, 1.21, 42.5, 'D',1);
insert into consumption (date, driver_id, price, volume, type, id) values ('2020-01-26', 15, 1.19, 38, 'D',2);
insert into consumption (date, driver_id, price, volume, type, id) values ('2020-01-17', 3, 1.23, 10, 'E95',3);
insert into consumption (date, driver_id, price, volume, type, id) values ('2020-02-05', 3, 1.16, 25, 'E95',4);
