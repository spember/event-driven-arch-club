create table order_tracker(
    id uuid not null primary key,
    customer_id varchar(128) not null,
    product_serial varchar(256) not null,
    time_initiated timestamp,
    time_completed timestamp,

    item_reserved varchar(64) not null,
    item_reserved_time timestamp,

    payment_processed varchar(64) not null,
    payment_processed_time timestamp,

    confirmation_sent varchar(64) not null,
    confirmation_sent_time timestamp,

    item_shipped varchar(64) not null,
    item_shipped_time timestamp
);

create index order_customer on order_tracker(customer_id);