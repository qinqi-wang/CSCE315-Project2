import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	
	public static void main(String[] args) throws IOException, SQLException{
		
		String file_name="ClassScheduleFall2016.txt";
		
		Extractor e=new Extractor(file_name);
		Template t=new Template();
		Writer w=new Writer();
		
		e.process();
		
		w.connect();
		
		while(true){
			
			t=e.next();
			if(t!=null){
				//System.out.println(t.toString());
				w.write(t);
			}else{
				break;
			}
		}
		
		//w.write(t);
		System.out.println(e.counter);
		System.out.println(e.pcounter);
		System.out.println(e.ppcounter);
		//e.close();
	}
	
}
