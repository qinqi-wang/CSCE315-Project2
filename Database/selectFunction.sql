SELECT 
cl.crn,
cl.credit,
c.course_num,
c.section_num,
p.name,
ti.title,
s.subj,
l.bldg_room,
l.capacity,
t.start_time,
t.end_time,
cl.dates,
t.days,
t.duration
FROM class cl 
join courses c ON(cl.course=c.id)
join professors p on(cl.prof = p.id)
join titles ti on(cl.title = ti.id)
join subjects s on(cl.subj=s.id)
join locations l on(cl.location=l.id)
join time_day t on(cl.time_day=t.id)

-- Add specific searching flags
