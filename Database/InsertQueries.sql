-- INSERT FUNCTION FOR COURSES --
st.executeUpdate("INSERT INTO courses(course_num,section_num) VALUES ('"+ t.course_num +"',"+ t.section_num +")");

-- INSERT FUNCTION FOR PROFESSORS --
st.executeUpdate("INSERT INTO professors(name) VALUES ('" + t.professor + "')");

-- INSERT FUNCTION FOR LOCATIONS --
st.executeUpdate("INSERT INTO locations(bldg_room,capacity) VALUES ('"+t.location+"',"+t.capacity+")");

-- INSERT FUNCTION FOR LOCATIONS --
st.executeUpdate("INSERT INTO titles(title) VALUES ('"+t.course_name+"')");

-- INSERT FUNCTION FOR SUBJECTS --
st.executeUpdate("INSERT INTO subjects(subj) VALUES ('" + t.subject + "')");

-- INSERT FUNCTION FOR TIME/DAY --
st.executeUpdate("INSERT INTO time_day(start_time,end_time,days) VALUES ('"+t.start_time.toString()+"','"+t.end_time.toString()+"','"+t.days+"')");

-- INSERT FUNCTION FOR CLASSES --
st.executeUpdate("INSERT INTO class(crn,course,prof,title,subj,location,time_day,credit,dates) 
		VALUES ("+t.CRN+",
			(select id from courses where courses.course_num = '"+t.course_num+"' AND section_num="+t.section_num+" LIMIT 1),
			(select id from professors where professors.name = '"+t.professor+"' LIMIT 1),
			(select id from titles where titles.title = '"+t.course_name+"' LIMIT 1),
			(select id from subjects where subjects.subj = '"+t.subject+"' LIMIT 1),
			(select id from locations where locations.bldg_room = '"+t.location+"' AND locations.capacity = "+t.capacity+" LIMIT 1),
			(select id from time_day where time_day.start_time = '"+t.start_time.toString()+"' AND time_day.end_time = '"+t.end_time.toString()+"' AND time_day.days = '"+t.days+"' LIMIT 1),
			'"+t.credit+"',
			'"+t.dates+"')");
