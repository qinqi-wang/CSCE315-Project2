import java.sql.*;


public class Writer {
	
	Connection connection;
	
	public void connect(){
		System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");

		/*try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;

		}*/

		System.out.println("PostgreSQL JDBC Driver Registered!");

		connection = null;

		try {

			connection = DriverManager.getConnection(
					"jdbc:mysql://database2.cse.tamu.edu/jwallace", "jwallace",
					"test123");

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}
	}
	
	public void write(Template t) throws SQLException{
		
		Statement st = null;
		st = connection.createStatement();
		if (connection != null) {
			/*try{
				st.executeQuery("INSERT INTO classes(crn,subj,course_num,section_num,title,prof,location,start_time,end_time,days) VALUES("+ t.CRN +",'"+t.subject+"','"+t.course_num+"',"+t.section_num+",'"+t.course_name+"','"+t.professor+"','"+t.location+"','"+t.start_time+"','"+t.end_time+"','"+t.days+"')");
			}catch(SQLException e){
				
			}*/
			try{
			
		    st.executeUpdate("INSERT INTO courses(course_num,section_num) VALUES ('"+ t.course_num +"',"+ t.section_num +")");
		    }catch(SQLException e){
				
			}
			try{
				st.executeUpdate("INSERT INTO professors(name) VALUES ('" + t.professor + "')");
			}catch(SQLException e){
				
			}
			
			try{
				st.executeUpdate("INSERT INTO locations(bldg_room,capacity) VALUES ('"+t.location+"',"+t.capacity+")");
			}catch(SQLException e){
				
			}
			
			try{
				st.executeUpdate("INSERT INTO titles(title) VALUES ('"+t.course_name+"')");
			}catch(SQLException e){
				
			}
			
			try{
				st.executeUpdate("INSERT INTO subjects(subj) VALUES ('" + t.subject + "')");
			    }catch(SQLException e){
				
			}
			
			try{
				st.executeUpdate("INSERT INTO time_day(start_time,end_time,days) VALUES ('"+t.start_time.toString()+"','"+t.end_time.toString()+"','"+t.days+"')");
				}catch(SQLException e){
				
			}
			
			/*try{
				}catch(SQLException e){
				
			}*/
			st.executeUpdate("INSERT INTO class(crn,course,prof,title,subj,location,time_day,credit,dates) VALUES ("+t.CRN+", (select id from courses where courses.course_num = '"+t.course_num+"' AND section_num="+t.section_num+" LIMIT 1),(select id from professors where professors.name = '"+t.professor+"' LIMIT 1),(select id from titles where titles.title = '"+t.course_name+"' LIMIT 1),(select id from subjects where subjects.subj = '"+t.subject+"' LIMIT 1),(select id from locations where locations.bldg_room = '"+t.location+"' AND locations.capacity = "+t.capacity+" LIMIT 1),(select id from time_day where time_day.start_time = '"+t.start_time.toString()+"' AND time_day.end_time = '"+t.end_time.toString()+"' AND time_day.days = '"+t.days+"' LIMIT 1),'"+t.credit+"','"+t.dates+"')");
			
		} else {
			System.out.println("Failed to make connection!");
		}
		
	}
	
}
