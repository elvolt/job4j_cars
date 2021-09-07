create table if not exists mark
(
    id   serial primary key,
    name varchar(60) not null unique
);

create table if not exists model
(
    id      serial primary key,
    mark_id int         not null references mark (id),
    name    varchar(60) not null unique
);

create table if not exists body
(
    id   serial primary key,
    name varchar(60) not null unique
);

create table if not exists transmission
(
    id   serial primary key,
    type varchar(60) not null unique
);

create table if not exists users
(
    id       serial primary key,
    name     varchar(100) not null,
    email    varchar(100) not null unique,
    password varchar(100) not null
);

create table if not exists photo
(
    id   serial primary key,
    path varchar(255) not null unique
);

create table if not exists post
(
    id              serial primary key,
    description     TEXT,
    created         TIMESTAMPTZ,
    is_active       boolean,
    model_id        int not null references model (id),
    body_id         int not null references body (id),
    transmission_id int not null references transmission (id),
    user_id         int not null references users (id),
    photo_id        int references photo (id),
    price           double precision
);