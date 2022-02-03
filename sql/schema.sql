create table films
(
    id            int          not null
        primary key,
    title         varchar(255) null,
    episode_id    int          null,
    opening_crawl longtext     null,
    director      varchar(255) null,
    producer      varchar(255) null,
    release_date  date         null
);

create table orders
(
    id             int          not null
        primary key,
    region         varchar(255) not null,
    country        varchar(255) not null,
    item_type      varchar(255) not null,
    sales_channel  varchar(255) not null,
    order_priority varchar(255) not null,
    order_date     date         not null,
    ship_date      date         not null,
    units_sold     int          not null,
    unit_price     double       not null,
    unit_cost      double       not null,
    total_revenue  double       not null,
    total_cost     double       not null,
    total_profit   double       not null,
    batch          mediumtext   not null,
    constraint orders_id_uindex
        unique (id)
);

create table people
(
    id         int          not null
        primary key,
    name       varchar(255) not null,
    height     varchar(255) null,
    mass       varchar(255) null,
    hair_color varchar(255) null,
    skin_color varchar(255) null,
    eye_color  varchar(255) null,
    birth_year varchar(255) null
);

create table films_people
(
    film_id   int not null,
    person_id int not null,
    primary key (person_id, film_id),
    constraint fp_films
        foreign key (film_id) references films (id)
            on delete cascade,
    constraint fp_people
        foreign key (person_id) references people (id)
            on delete cascade
);

create table starships
(
    id                     int          not null
        primary key,
    name                   varchar(255) null,
    model                  varchar(255) null,
    manufacturer           varchar(255) null,
    cost_in_credits        varchar(255) null,
    length                 double       null,
    max_atmosphering_speed varchar(255) null,
    crew                   varchar(255) null,
    passengers             varchar(255) null,
    cargo_capacity         varchar(255) null,
    consumables            varchar(255) null,
    hyperdrive_rating      varchar(255) null,
    MGLT                   varchar(255) null,
    starship_class         varchar(255) null
);

create table films_starships
(
    film_id     int not null,
    starship_id int not null,
    primary key (film_id, starship_id),
    constraint fs_films
        foreign key (film_id) references films (id)
            on delete cascade,
    constraint fs_starships
        foreign key (starship_id) references starships (id)
            on delete cascade
);

create table people_starships
(
    person_id   int not null,
    starship_id int not null,
    primary key (person_id, starship_id),
    constraint ps_people
        foreign key (person_id) references people (id)
            on delete cascade,
    constraint ps_starships
        foreign key (starship_id) references starships (id)
            on delete cascade
);

