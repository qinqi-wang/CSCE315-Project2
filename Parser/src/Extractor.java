import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extractor {
	//17721
	ArrayList<String> text=new ArrayList<String>();
	String fileName;
	
	final String reg1="(\\d+)\t([A-Z]+)\\t?(.+?(?=\\t))\\t?(\\w+?(?=\\t))\\t[A-Z]{2,3}\\s?((?:(?:\\d?.\\d|\\d)+)-[A-Z][a-z]{1,2}|(?:\\d?.\\d|\\d)-(?:\\d?.\\d|\\d)|(?:\\d?.\\d|\\d)+)\\t((?:[A-Za-z0-9.,/\\-\\&\\:\\)\\(\\'\"]+\\s)+?(?=\"|\\t|\\s{2,}))\"?\\t(\\w*)\\s?(?:(\\d*):(\\d*)\\s?(\\w*)-(\\d*):(\\d*)\\s?(\\w*))?\\s?(\\S{1,4}\\s)(?:[0-9-]{1,4}\\s)*(TBA|[A-Za-z\\.\\,\\'\\:\"\\-\\s\\(\\)]*)\\t(TBA|\\d*\\/\\d*-\\d*\\/\\d*)\\t(TBA|[A-Z0-9]{1,6}[0-9]?\\s[0-9]{0,4}[A-Z]?)";
	final String reg2="\\t+([A-Z]{1,5})\\t+(?:(\\d*):(\\d*)\\s?(\\w*)-(\\d*):(\\d*)\\s?(\\w*))?\\t(TBA|[A-Za-z\\.\\,\\'\\:\"\\-\\s\\(\\)]*)(?:(TBA|\\d*\\/\\d*-\\d*\\/\\d*))\\s(TBA|WEB|[A-Z]{1,5}[0-9]?\\s[0-9]{0,4})";
	Scanner s;
	
	Template previous_template=new Template();
	
	public int counter=0;
	
	public int pcounter=0;
	public int ppcounter=0;
	
	
	public int labcounter=0;
	
	public Extractor(String fn){
		fileName=fn;
		try{
			s = new Scanner(new File(fileName));
			
			while(s.hasNext()){
				text.add(s.useDelimiter("\r\n").next());
			}
			
			s.close();
			
			}catch(IOException e){
				
			}
	}
	
	public Template next(){
		
		Template t = new Template();
		
		while(text.size()>0){
		
			String current = text.remove(0);
			
			Matcher lecture_matcher = Pattern.compile(reg1).matcher(current);
			Matcher lab_matcher = Pattern.compile(reg2).matcher(current);
			if(lecture_matcher.find()){
				//System.out.println(lecture_matcher.group());
				//System.out.println(lecture_matcher.group(13));
				t.CRN=Integer.parseInt(lecture_matcher.group(1));
				t.subject=lecture_matcher.group(2);
				t.course_num=lecture_matcher.group(3);
				t.section_num=Integer.parseInt(lecture_matcher.group(4));
				t.credit=lecture_matcher.group(5);
				t.course_name=lecture_matcher.group(6).replaceAll("'", "''");
				t.course_name=t.course_name.replaceAll("(Syllabus)", "").trim();
				t.days=lecture_matcher.group(7);
				if(lecture_matcher.group(8)!=null){
					int actual_start=Integer.parseInt(lecture_matcher.group(8).replaceAll("\\W", ""));
					if(lecture_matcher.group(10).matches("pm")&&actual_start!=12) actual_start+=12;
					Time start=new Time(actual_start,Integer.parseInt(lecture_matcher.group(9).replaceAll("\\W", "")), 0);
					int actual_end=Integer.parseInt(lecture_matcher.group(11));
					if(lecture_matcher.group(13).matches("pm")&&actual_end!=12) actual_end+=12;
					Time end=new Time(actual_end,Integer.parseInt(lecture_matcher.group(12).replaceAll("\\W", "")), 0);
					t.start_time=start;
					t.end_time=end;
					t.duration=(int) ((t.end_time.getTime()-t.start_time.getTime())/60000);
				}
				if(lecture_matcher.group(14).contains("TBA")||lecture_matcher.group(14).contains("WEB")){
					t.capacity=-1;
				}else{
					t.capacity=Integer.parseInt(lecture_matcher.group(14).replaceAll("\\W", ""));
				}
				t.professor=lecture_matcher.group(15).replaceAll("\t", "");
				t.professor=lecture_matcher.group(15).replaceAll("\'", "''").trim();
				t.dates=lecture_matcher.group(16);
				t.location=lecture_matcher.group(17);
				
				
				
				previous_template=t;
				
				labcounter=1;
				
				counter++;
				pcounter++;
				return t;
			}else if(lab_matcher.find()){
				//System.out.println(lab_matcher.group());
				
				t=previous_template;
				//t.course_name+=" ";
				t.days=lab_matcher.group(1);
				if(lab_matcher.group(2)!=null){
					int actual_start=Integer.parseInt(lab_matcher.group(2).replaceAll("\\W", ""));
					if(lab_matcher.group(4).matches("pm")&&actual_start!=12) actual_start+=12;
					Time start=new Time(actual_start,Integer.parseInt(lab_matcher.group(3).replaceAll("\\W", "")), 0);
					int actual_end=Integer.parseInt(lab_matcher.group(5));
					if(lab_matcher.group(7).matches("pm")&&actual_end!=12) actual_end+=12;
					Time end=new Time(actual_end,Integer.parseInt(lab_matcher.group(6).replaceAll("\\W", "")), 0);
					t.start_time=start;
					t.end_time=end;
					//System.out.println("Start: "+t.start_time+" End: "+t.end_time+" Dur: "+);
					t.duration=(int) ((t.end_time.getTime()-t.start_time.getTime())/60000);
					//System.out.println("Start: "+t.start_time+" End: "+t.end_time+" Dur: "+t.duration);
				}
				t.professor=lab_matcher.group(8).replaceAll("\t", "");
				t.professor=lab_matcher.group(8).replaceAll("\'", "''").trim();
				t.location=lab_matcher.group(10);
				t.dates=lab_matcher.group(9);
				
				counter++;
				
				ppcounter++;
				return t;
				
			}else{
				System.out.println(current);
			}
		
		}
		
		return null;
	}
	
	public void process() throws IOException{
		
		FileOutputStream outF = new FileOutputStream("processed"+fileName);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outF));
		
		Matcher lecture_matcher;
		Matcher lab_matcher;
		Matcher subj_matcher;
		
		ArrayList<String> temp=new ArrayList<String>();
		
		for(String s:text){
			lecture_matcher=Pattern.compile("^\\d{5}").matcher(s);
			lab_matcher=Pattern.compile("^\\t+[A-Z]{1,5}\\t").matcher(s);
			subj_matcher=Pattern.compile("^[A-Z]{3,4}\\s\\-").matcher(s);
			if(lecture_matcher.find()){
				//System.out.println(s);
				//pcounter++;
				//writer.write(s+"\n");
			}else if(lab_matcher.find()){
				//ppcounter++;
			}else if(subj_matcher.find()){
				Matcher temp1=Pattern.compile("^([A-Z]{3,4})\\s\\-").matcher(s); //s is the string you want to parse
				if(temp1.find()){ 
					//writer.write(temp1.group(1)+"\n"); //temp1.group(1) is the parsed output
					//System.out.println(temp1.group(1));
				}
				temp.add(s);
				//ppcounter++;
			}else{
				temp.add(s);
			}
		}
		
		text.removeAll(temp);
		
		writer.close();
	}
	
	public void close(){
		s.close();
	}
}
