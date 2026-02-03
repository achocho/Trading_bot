create table if not exists portfolio(
  id int primary key,
  cash numeric(18,8) not null
);

insert into portfolio(id, cash)
values (1, 100000)
on conflict (id) do nothing;

create table if not exists holdings(
  symbol varchar(20) primary key,
  qty numeric(18,8) not null,
  avg_price numeric(18,8) not null
);

create table if not exists trades(
  id bigserial primary key,
  ts timestamp not null,
  symbol varchar(20) not null,
  side varchar(4) not null,
  qty numeric(18,8) not null,
  price numeric(18,8) not null,
  fee numeric(18,8) not null,
  pnl numeric(18,8) not null
);

create table if not exists bot_state(
  id int primary key default 1,
  mode varchar(10) not null,     
  running boolean not null
);

insert into bot_state(id, mode, running)
values (1, 'TRADING', false)
on conflict (id) do nothing;
