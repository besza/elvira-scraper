create table mav_timetable
(
    id integer primary key,
    origin text not null,
    destination text not null,
    planned_departure timestamp not null,
    departure timestamp,
    planned_arrival timestamp not null,
    arrival timestamp
);