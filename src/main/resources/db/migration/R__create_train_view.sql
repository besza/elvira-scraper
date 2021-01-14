create or replace view train_view as
select origin,
       destination,
       planned_departure::time depart,
       array_agg(extract(minutes from arrival - planned_arrival)) delay
from mav_timetable
where arrival is not null
group by origin, destination, planned_departure::time;
