alter table inventory add column current_price int check (current_price >= 0)  default 0;