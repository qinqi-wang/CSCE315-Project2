package sample;

import com.mysql.jdbc.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.Date;

public class SampleController implements Initializable {

    //@FXML
    //private TableView SearchTable = new TableView<>();
    @FXML
    private ComboBox<String> StartAM, EndAM, TimeSetCombo, CourseCombo = new ComboBox<>(); //CourseCombo;
    @FXML
    private TextField InstructorName, CourseNum, SectionNum, Title, MaxCap, StartTime1, StartTime2, EndTime1, EndTime2, CourseDur,
            BldgRoom;
    @FXML
    private CheckBox Mon, Tue, Wed, Thu, Fri, Sat, Sun;
    @FXML
    public Button ResetButton, Search;
    @FXML
    private ObservableList<ObservableList<String>> SearchData;
    private void Connects() {
            String DBLocation = "database2.cse.tamu.edu"; //The host
            String DBname = "jwallace"; //Generally your CS username or username-text like explained above
            String DBUser = "jwallace"; //CS username
            String DBPass = "test123"; //password setup via CSNet for the MySQL database
            Connection conn = null;
            try {
                String connectionString = "jdbc:mysql://database2.cse.tamu.edu/jwallace";
                conn = DriverManager.getConnection(connectionString, DBUser, DBPass);
                System.out.println("Database connection established");
            }
            catch (SQLException e) {
                System.out.println("Connection Issue: " + e.getMessage());
                System.out.println("Connection Issue: " + e.getSQLState());
                System.out.println("Connection Issue: " + e.getErrorCode());
            }
    }

    private void ConnectDisplay(TableView tableview, Statement stmt, ResultSet rs, String QueryString) {
        SearchData = FXCollections.observableArrayList();

        try {
            String connectionString = "jdbc:mysql://database2.cse.tamu.edu/jwallace";
            Connection conn = DriverManager.getConnection(connectionString, "jwallace", "test123");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(QueryString);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            //Table column added
            for(int i=0; i<columnsNumber; i++) {
                final int j = i;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(rsmd.getColumnName(i+1));
                System.out.println(rsmd.getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(CellDataFeatures<ObservableList<String>, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                tableview.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }
            //Data added to SearchData, observableList
            while(rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1; i<=rsmd.getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                //System.out.println("Row [1] added " + row);
                writeRow(row);
                SearchData.add(row);
            }
            tableview.setItems(SearchData);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Subjects are hard coded, set only 10 visible at a time
        CourseCombo.setVisibleRowCount(10);
        Connects();
    }

    private String TotalDays = "";
    @FXML
    private void DaysCheckBox(ActionEvent e) {
        StringBuilder DaysofWeek = new StringBuilder();
        if(Mon.isSelected()) { DaysofWeek.append("M"); }
        if(Tue.isSelected()) { DaysofWeek.append("T"); }
        if(Wed.isSelected()) { DaysofWeek.append("W"); }
        if(Thu.isSelected()) { DaysofWeek.append("R"); }
        if(Fri.isSelected()) { DaysofWeek.append("F"); }
        if(Sat.isSelected()) { DaysofWeek.append("S"); }
        if(Sun.isSelected()) { DaysofWeek.append("U"); }
        TotalDays = DaysofWeek.toString();
    }


    private String TimeQuery(String TimeH, String TimeM, String AM) {
        //Null values, don't want to print anything if nothing is entered
        Integer STH = null;
        Integer STM = null;
        StringBuilder StartFinal = new StringBuilder();
        String StartQuery = "";
        //Time returns
        if (!TimeH.equals("")) {
            // don't forget to change StartTime1 to a CustomTextField!!!!!
            STH = Integer.valueOf(TimeH);
            if( STH > 12 ) {
                System.out.println("Hours cannot be greater than 12");
            }
        }
        if (!TimeM.equals("")) {
            // don't forget to change StartTime1 to a CustomTextField!!!!!
            STM = Integer.valueOf(TimeM);
            if( STM > 60) {
                System.out.println("Minutes cannot be greater than 60");
            }
        }
        if(STH == null && STM == null) {
            return "";
        }

        if(AM.equals("PM")) {
            if(STH != 12) STH+=12;
        }
        if(AM.equals("AM") && STH == 12) {
            STH = 0;
        }
        //System.out.println("Start hours: " + STH);
        TimeH = String.valueOf(STH);
        TimeM= String.valueOf(STM);
        if(TimeH.equals("0")) {
            StartFinal.append("00");
        }
        else if(STH < 10) {
            StartFinal.append("0" + TimeH);
        }
        else StartFinal.append(TimeH);
        StartFinal.append(":");
        if( STM < 10) StartFinal.append("0");
        StartFinal.append(TimeM);
        StartFinal.append(":00");
        StartQuery = StartFinal.toString();
        return StartQuery;
    }

    private boolean AddQuery(StringBuilder FString, String QueryCheck, String Query, String QTerm, boolean multiterm) {
        if( !QueryCheck.equals("")) {
            if (multiterm == true) {
                FString.append(" AND ");
            }
            FString.append(QTerm + " like ");
            FString.append("'" + Query + "'");
            return true;
        }
        return multiterm;
    }

    private boolean AddIntQuery(StringBuilder FString, String QueryCheck, String Query, String QTerm, boolean multiterm) {
        if (!QueryCheck.equals("")) {
            if (multiterm == true) {
                FString.append(" AND ");
            }
            FString.append(QTerm);
            if (StringUtils.isStrictlyNumeric(Query)) {
                FString.append("=" + Query);
            } else FString.append(Query);
            return true;
        }
        return multiterm;
    }

    public void writeRow(ObservableList<String> row) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("Log.txt", true))) {
            bw.write(String.valueOf(row));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeQuery(Vector<String> QueryVec) {
        PrintWriter pw = null;
        Vector<String> EntryVec = new Vector<String>();
        EntryVec.addElement("SUBJECT: ");
        EntryVec.addElement("PROFESSOR: ");
        EntryVec.addElement("COURSE_NUM: ");
        EntryVec.addElement("SECTION_NUM: ");
        EntryVec.addElement("TITLE: ");
        EntryVec.addElement("MAX_CAP: ");
        EntryVec.addElement("START_TIME: ");
        EntryVec.addElement("END_TIME: ");
        EntryVec.addElement("DAYS: ");
        EntryVec.addElement("DURATION: ");
        EntryVec.addElement("BLDG ROOM: ");
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("Log.txt", true))){
            //pw = new PrintWriter("Log.txt");
            //BufferedWriter bw = new BufferedWriter(pw);
            bw.newLine(); //done for readability when multiple queries are added
            Date now = new Date();
            String currTime = new SimpleDateFormat("d MMM yyyy HH:mm:ss").format(now);
            bw.write(currTime);
            bw.newLine();
            for(int i=0; i<EntryVec.size(); ++i) {
                bw.write(EntryVec.get(i));
                bw.write(QueryVec.get(i));
                bw.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            pw.close();
        }

    }

    public void GetQuery(ActionEvent actionEvent) throws IOException {
        boolean multiterm = false; //will check for multiple queries, adding "AND" if necessary
        StringBuilder FinalStringB = new StringBuilder();
        String SUBJECT, PROFESSOR, COURSE_NUM, SECTION_NUM, TITLE, MAX_CAP, BLDG_ROOM = "";
        String START_TIMEH, START_TIMEM, START_AM, END_TIMEH, END_TIMEM, END_AM, COURSE_DUR = "";
        Vector<String> QueryVec = new Vector<String>();

        SUBJECT = CourseCombo.getSelectionModel().getSelectedItem().trim();
        multiterm = AddQuery(FinalStringB, SUBJECT, SUBJECT, "s.subj", multiterm);
        QueryVec.addElement(SUBJECT);

        PROFESSOR = InstructorName.getText().trim();
        multiterm = AddQuery(FinalStringB, PROFESSOR, PROFESSOR, "p.name", multiterm);
        QueryVec.addElement(PROFESSOR);

        COURSE_NUM = CourseNum.getText().trim();
        multiterm = AddQuery(FinalStringB, COURSE_NUM, COURSE_NUM, "c.course_num", multiterm);
        QueryVec.addElement(COURSE_NUM);

        SECTION_NUM = SectionNum.getText().trim();
        multiterm = AddQuery(FinalStringB, SECTION_NUM, SECTION_NUM, "c.section_num", multiterm);
        QueryVec.addElement(SECTION_NUM);

        TITLE = Title.getText().trim();
        multiterm = AddQuery(FinalStringB, TITLE, TITLE, "ti.title", multiterm);
        QueryVec.addElement(TITLE);

        MAX_CAP = MaxCap.getText().trim();
        //Modified AddQuery, allowing capacity to check if class sizes are lesser/greater than the value written
        multiterm = AddIntQuery(FinalStringB, MAX_CAP, MAX_CAP, "l.capacity", multiterm);
        QueryVec.addElement(MAX_CAP);

        START_TIMEH = StartTime1.getText().trim();
        START_TIMEM = StartTime2.getText().trim();
        START_AM = StartAM.getSelectionModel().getSelectedItem();
        String StartQuery = TimeQuery( START_TIMEH, START_TIMEM, START_AM);

        END_TIMEH = EndTime1.getText().trim();
        END_TIMEM = EndTime2.getText().trim();
        END_AM = EndAM.getSelectionModel().getSelectedItem();
        String EndQuery = TimeQuery( END_TIMEH, END_TIMEM, END_AM);

        String TimeSet = TimeSetCombo.getSelectionModel().getSelectedItem();
        if(!TimeSet.equals("")) {
            if (TimeSet.equals("Exact Match")) {
                multiterm = AddQuery(FinalStringB, START_TIMEH, StartQuery, "t.start_time", multiterm);
                multiterm = AddQuery(FinalStringB, END_TIMEH, EndQuery, "t.end_time", multiterm);
            }
            else if (TimeSet.equals("Between Start/End Time")) {
                if (!START_TIMEH.equals("") && !END_TIMEH.equals("")) {
                    if (multiterm == true) {
                        FinalStringB.append(" AND ");
                    }
                    // t.start_time BETWEEN '8:00:00' AND '12:00:00' AND t.end_time BETWEEN '8:00:00' AND '12:00:00'
                    FinalStringB.append("t.start_time BETWEEN '" + StartQuery + "' AND '" + EndQuery + "'");
                    FinalStringB.append(" AND t.end_time BETWEEN '" + StartQuery + "' AND '" + EndQuery + "'");
                }
                //return multiterm;
                multiterm = true;
            }
            else if(TimeSet.equals("NOT Between Start/End Time")) {
                if (!START_TIMEH.equals("") && !END_TIMEH.equals("")) {
                    if (multiterm == true) {
                        FinalStringB.append(" AND ");
                    }
                    FinalStringB.append("(t.end_time <= '" + StartQuery + "' OR '" + EndQuery + "' <= t.start_time) AND t.start_time != '00:00:00'" );
                }
            }
        }
        QueryVec.addElement(StartQuery);
        QueryVec.addElement(EndQuery);

        COURSE_DUR = CourseDur.getText().trim();
        multiterm = AddIntQuery(FinalStringB, COURSE_DUR, COURSE_DUR, "t.duration", multiterm);
        QueryVec.addElement(COURSE_DUR);

        multiterm = AddQuery(FinalStringB, TotalDays, TotalDays, "t.days", multiterm);
        QueryVec.addElement(TotalDays);

        BLDG_ROOM = BldgRoom.getText().trim();
        multiterm = AddQuery(FinalStringB, BLDG_ROOM, BLDG_ROOM, "l.bldg_room", multiterm);
        QueryVec.addElement(BLDG_ROOM);

        //showing how we submit a query to the database
        String FinalString = " where " + FinalStringB.toString() + ";";
        String QueryString = "SELECT cl.crn, cl.credit, c.course_num, c.section_num, p.name, ti.title, s.subj, " +
                "l.bldg_room, l.capacity, t.start_time, t.end_time, cl.dates, t.days, t.duration" + " FROM class cl " +
                "join courses c ON(cl.course = c.id) " +
                "join professors p ON(cl.prof = p.id) " +
                "join titles ti ON(cl.title = ti.id) " +
                "join subjects s ON(cl.subj = s.id) " +
                "join locations l ON(cl.location = l.id) " +
                "join time_day t ON(cl.time_day = t.id)" + FinalString;
        System.out.println(QueryString);
        Statement stmt = null;
        ResultSet rs = null;
        Stage ResultWindow;
        ResultWindow = new Stage();
        ResultWindow.setTitle("Search Results");
        TableView tableview = new TableView();
        //writes the search entries to a file
        writeQuery(QueryVec);
        ConnectDisplay(tableview, stmt, rs, QueryString);
        ResultWindow.setScene(new Scene(tableview));
        ResultWindow.initModality(Modality.APPLICATION_MODAL);
        ResultWindow.initOwner(Search.getScene().getWindow());
        ResultWindow.showAndWait();
    }

    //setting an action for the Clear button
    public void ResetField(ActionEvent actionEvent) {
        InstructorName.clear();
        CourseNum.clear();
        SectionNum.clear();
        Title.clear();
        MaxCap.clear();
        StartTime1.clear();
        StartTime2.clear();
        EndTime1.clear();
        EndTime2.clear();
        CourseDur.clear();
        BldgRoom.clear();
        MaxCap.clear();
        StartAM.setValue("");
        EndAM.setValue("");
        TimeSetCombo.setValue("");
        CourseCombo.setValue("");
        Mon.setSelected(false);
        Tue.setSelected(false);
        Wed.setSelected(false);
        Thu.setSelected(false);
        Fri.setSelected(false);
        Sat.setSelected(false);
        Sun.setSelected(false);
    }

}