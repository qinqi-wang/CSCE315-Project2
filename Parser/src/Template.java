import java.sql.Time;

public class Template {
	
	public String subject="";
	public String course_name="";
	public String course_num="";
	public int section_num=000;
	public String credit="";
	public int CRN=00000;
	public String professor="";
	public int capacity=0;
	public Time start_time=new Time(0,0,0);
	public Time end_time=new Time(0,0,0);
	public String days="TBA";
	public String dates="";
	public String location="";
	public String notes="";
	public int duration = 0;
	
	public String toString(){
		return subject+"\n"+course_name+"\n"+course_num+"\n"+section_num+"\n"+CRN+"\n"+professor+"\n"+capacity+"\n"+location+"\n"+start_time+"\n"+end_time+"\n"+days+"\n"+location+"\n";
	}

}
