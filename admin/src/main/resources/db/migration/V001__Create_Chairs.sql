create table chair(
    id uuid not null primary key,
    sku varchar(16) not null unique,
    version int check (version > 0), -- used for optimistic locking
    name varchar(128) not null,
    description text
);