create table question_image
(
    id                 bigint auto_increment
        primary key,
    created_date       datetime(6) null,
    last_modified_date datetime(6) null,
    question_id        bigint      not null,
    image_url          varchar(500) not null,
    display_order      int         not null,
    constraint fk_qi_question
        foreign key (question_id) references question(id)
            on delete cascade
);

create index idx_question_image_question_id
    on question_image (question_id);

create table answer_image
(
    id                 bigint auto_increment
        primary key,
    created_date       datetime(6) null,
    last_modified_date datetime(6) null,
    answer_id          bigint      not null,
    image_url          varchar(500) not null,
    display_order      int         not null,
    constraint fk_ai_answer
        foreign key (answer_id) references answer(id)
            on delete cascade
);

create index idx_answer_image_answer_id
    on answer_image (answer_id);


