create table if not exists posts(
  id bigserial primary key,
  name varchar(256) not null,
  text varchar(256) null,
  tags varchar(256) null,
  image LONGBLOB,
  likes_count integer);

  create table if not exists comments(
    id bigserial primary key,
    text varchar(256) not null,
    post_id bigserial not null);