create table if not exists student_notification_states (
                                                           student_id bigint not null ,
                                                           notification_id bigint not null,
                                                           notification_states boolean,
                                                           primary key  (student_id, notification_id),
    foreign key (student_id) references students(id) on delete cascade ,
    foreign key (notification_id) references notifications(id) on delete cascade
    );


create table if not exists instructor_notification_states (
                                                              instructor_id bigint not null ,
                                                              notification_id bigint not null,
                                                              notification_states boolean,
                                                              primary key  (instructor_id, notification_id),
    foreign key (instructor_id) references instructors(id) on delete cascade ,
    foreign key (notification_id) references notifications(id) on delete cascade
    );


create table if not exists student_announcements (
                                                     student_id BIGINT not null,
                                                     announcement_id BIGINT not null,
                                                     announcement_state boolean,
                                                     primary key (student_id, announcement_id),
    foreign key (student_id) references students(id) on delete cascade,
    foreign key (announcement_id) references announcements(id) on delete cascade
    );
