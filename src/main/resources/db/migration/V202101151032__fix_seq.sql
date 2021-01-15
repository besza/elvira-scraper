create sequence if not exists "hibernate_sequence"
    as integer
    owned by mav_timetable.id;

select setval(
    'hibernate_sequence',
    (select max(id) from mav_timetable)
);

alter table mav_timetable
    alter column id set default nextval('hibernate_sequence');
