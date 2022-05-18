create table chair(
    id uuid not null primary key,
    version int check (version > 0), -- used for optimistic locking
    sku varchar(16) not null unique,
    name varchar(128) not null
);