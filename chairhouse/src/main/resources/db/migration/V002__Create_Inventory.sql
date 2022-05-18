-- Creates the Table used to track individual Chair instances, our inventory

create table inventory(
    serial varchar(50) primary key,
    chair_id uuid references chair(id),
    version int check (version > 0), -- used for optimistic locking
    arrived timestamp with time zone not null,
    purchased timestamp with time zone,
    shipped timestamp with time zone
);