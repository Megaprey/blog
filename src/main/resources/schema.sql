create table if not exists posts(
  id bigserial primary key,
  name varchar(256) not null,
  text varchar(256) null,
  tags varchar(256) null,
  image LONGBLOB,
  likes_count integer);

create table if not exists comment(
  id bigserial primary key,
  text varchar(256) not null,
  author varchar(100) not null,
  post_id bigint not null,
  created_at timestamp default current_timestamp,
  foreign key (post_id) references posts(id) on delete cascade
);