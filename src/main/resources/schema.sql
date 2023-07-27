create table address(
    id identity primary key,
    city varchar not null,
    street varchar not null,
    house int not null
);

create table storage(
    id bigint primary key references address(id)
);

create table client(
    id identity primary key,
    email varchar not null,
    name varchar(30) not null
);

create table bike(
    id identity primary key,
    color varchar not null,
    size varchar not null,
    label varchar not null,
    available boolean,
    storage_id bigint not null references storage(id)
);

create table orders(
    id identity primary key,
    client_id bigint not null references client(id),
    storage_id bigint not null references storage(id)
);

create table bike_orders(
    id bigint references bike(id),
    order_id bigint references orders(id),
    primary key (id, order_id)
);

create table comment(
    id identity primary key,
    client_id bigint references client(id),
    description varchar not null
);