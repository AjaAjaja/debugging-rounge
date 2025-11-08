create table question
(
    author_id          bigint      not null,
    created_date       datetime(6) null,
    id                 bigint auto_increment
        primary key,
    last_modified_date datetime(6) null,
    title              varchar(50) not null,
    content            text        not null
);

create table question_recommend
(
    id                 bigint auto_increment
        primary key,
    created_date       datetime(6)         null,
    last_modified_date datetime(6)         null,
    question_id        bigint              not null,
    type               enum ('DOWN', 'UP') not null,
    user_id            bigint              not null,
    constraint uk_question_user unique (question_id, user_id),
    constraint fk_qr_question
        foreign key (question_id) references question(id)
            on delete cascade
);

create table answer
(
    author_id          bigint      not null,
    created_date       datetime(6) null,
    id                 bigint auto_increment
        primary key,
    last_modified_date datetime(6) null,
    question_id        bigint      not null,
    content            text        not null,
    constraint fk_a_question
        foreign key (question_id) references question(id)
            on delete cascade
);

create table answer_recommend
(
    id                 bigint auto_increment
        primary key,
    answer_id          bigint                      not null,
    type               enum ('DOWN', 'NONE', 'UP') not null,
    user_id            bigint                      not null,
    created_date       datetime(6)                 null,
    last_modified_date datetime(6)                 null,
    constraint uk_answer_user
        unique (answer_id, user_id),
    constraint fk_ar_answer
        foreign key (answer_id) references answer(id)
            on delete cascade
);

create table user
(
    created_date       datetime(6)                       null,
    id                 bigint auto_increment
        primary key,
    last_modified_date datetime(6)                       null,
    email              varchar(191)                      not null,
    role               enum ('ROLE_ADMIN', 'ROLE_USER')  not null,
    social_type        enum ('GOOGLE', 'KAKAO', 'NAVER') not null,
    constraint uk_user_email_social
        unique (email, social_type)
);

create table refresh_token
(
    created_date       datetime(6) null,
    id                 bigint auto_increment
        primary key,
    last_modified_date datetime(6) null,
    user_id            bigint      not null,
    token_hash         binary(32)  not null,
    constraint uk_refresh_token_hash
        unique (token_hash),
    constraint uk_refresh_token_user
        unique (user_id)
);

create index idx_refresh_token_hash_user
    on refresh_token (token_hash, user_id);

create table blacklisted_refresh_token
(
    created_date       datetime(6) null,
    id                 bigint auto_increment
        primary key,
    last_modified_date datetime(6) null,
    user_id            bigint      null,
    token_hash         binary(32)  not null,
    constraint uk_blacklisted_refresh_token_hash
        unique (token_hash)
);

