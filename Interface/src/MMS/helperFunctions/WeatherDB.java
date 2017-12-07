/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MMS.helperFunctions;



import java.sql.*;
import java.util.*;
import com.healthmarketscience.jackcess.*;
import java.awt.Component;
import java.io.IOException;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author mike.herder
 */
public class WeatherDB {
    private Connection conn;
    private ResultSet rs;
    private PreparedStatement ps;
    private String dbName = "Weather.accdb";
    

    public List readOneColumnFromDB(String dbLocation, String table, String column, String selection ) throws SQLException{
        List results = new ArrayList();
        
        try{
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn=DriverManager.getConnection("jdbc:ucanaccess://" + dbLocation +  "/" + dbName);
//            System.out.println("Connected to Weather Database");
            
            Statement s = conn.createStatement();
            ResultSet rs;
            if (selection == null)
                rs = s.executeQuery("SELECT [" + column + "] FROM [" + table +"]");
            else
                rs = s.executeQuery("SELECT * FROM [" + table + "] WHERE [" + column + "]=\"" + selection + "\"" );
            while (rs.next()) {
                results.add(rs.getString(1));
            }
        }catch(Exception e){
            System.out.println("Connection to Database Failed");
        }
        if (results.size() == 0)
            results.add("none");
        
        conn.close();
        return results;
    }
    
    public List<String> getTable(String dbLocation, String tableName,  String selection) throws IOException{
        Database db = DatabaseBuilder.open(new File("/" +dbLocation +  "/" + dbName));
        db.setColumnOrder(Table.ColumnOrder.DISPLAY);
        boolean foundTable = false;
        boolean checkRegional = true;
        
        List <String> list = new ArrayList();        
        try  {
//            System.out.println("Connected to Weather Database - Getting Table");

            Table tbl = db.getTable(tableName);
            
            for (Row row : tbl){
                for(Column column : tbl.getColumns()) {
                    String columnName = column.getName();
                    Object value = row.get(columnName);
  
                    if (value.toString().equalsIgnoreCase(selection)){
//                        System.out.println("Column " + columnName + "(" + column.getType() + "): " + value + " (" + value.getClass() + ")");
                        foundTable = true;
                    }else if(foundTable == true){
                        foundTable = false;
                        checkRegional = true;
                        list.add(value.toString());
//                        System.out.println("Crop Table looking for is: " + tableName);
                    }else if (checkRegional == true){
                        checkRegional = false;
                        list.add(value.toString());
                    }

                }
            }
//            System.out.println("Closing Database Connection-getTable");
           
            db.flush();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        db.close();
        if (list.size() == 0)
            list.add("none");
        return list;
    }
   public List readOneRowFromDB(String dbLocation, String table, String column, String selection ) throws SQLException{
        List results = new ArrayList();
        
        try{
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn=DriverManager.getConnection("jdbc:ucanaccess://" + dbLocation +  "/" + dbName);
//            System.out.println("Connected to Weather Database");
            
            Statement s = conn.createStatement();
            ResultSet rs;
            if (selection == null)
                rs = s.executeQuery("SELECT [" + column + "] FROM [" + table +"]");
            else
                rs = s.executeQuery("SELECT * FROM [" + table + "] WHERE [" + column + "]=\"" + selection + "\"" );
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnNumber = rsmd.getColumnCount();    

            while (rs.next()) {
                for (int i = 2; i <= columnNumber; i++){
                    results.add(rs.getString(i));   //Get Crop Type
                }
              }
            if (results.size() == 0)
                results.add("none");
            
        }catch(Exception e){
            System.out.println("Connection to Database Failed");
            e.printStackTrace(System.out);
        }
//        System.out.println("Closing Database Connection - readDB");
        conn.close();
        return results;
    }
   
   public boolean addRowToTable(String dbLocation,String tableName ,List <String> Columns, List <String> Data) throws SQLException{
        Component rootPane = null;
        String sql = "";
        boolean AddRow = false;
        ResultSet rs = null;
        Statement st = null;
        int rowsUpdated = 0;

        
        if (Columns.size() == Data.size()){
            sql =  "INSERT into [" + tableName + "] (";
        
            //Adding column names for insert command
            for (int i = 0; i < Columns.size(); i++){
                Pattern pattern = Pattern.compile("\\s");
                Matcher matcher = pattern.matcher(Columns.get(i));
                boolean spaceFound = matcher.find();
                
                if (i == Columns.size()-1){
                    if (!spaceFound)
                        sql = sql + "[" + Columns.get(i) + "]";
                    else
                        sql = sql + "[" + Columns.get(i) + "]";
                }else{
                    if (!spaceFound)
                        sql = sql + "[" + Columns.get(i) + "], ";
                    else
                        sql = sql + "[" + Columns.get(i) + "], ";
                }
            }
            sql = sql + ") VALUES (";

            //Adding the values that are going into the columns
            for (int i = 0; i < Data.size(); i++){
                if (i == Columns.size()-1)
                    sql = sql + "'" + Data.get(i) + "'";
                else
                    sql = sql + "'" + Data.get(i) + "', ";
            }

            sql = sql + ")";
            
            //Checking the Connection to make sure it can be made
            try{
                conn=DriverManager.getConnection("jdbc:ucanaccess://" + dbLocation +  "/" + dbName);
                st= conn.createStatement();
            }catch(SQLException e){
                JOptionPane.showMessageDialog(rootPane, "Error 700.5: Unable to connect to Database", "Database Connection Error", JOptionPane.ERROR_MESSAGE);
                AddRow = false;
            }
            
            //Trying to Execute the SQL Statement., if made then closing the connection
            try{
                rowsUpdated = st.executeUpdate(sql);
                st.close();
                AddRow = true;
            }catch(SQLException er){
                if (rowsUpdated != 1){
                    System.out.println(er.toString());
                    JOptionPane.showMessageDialog(rootPane, "Error 701.3: SQL String could not be executed, Data was not added", "Database Error", JOptionPane.ERROR_MESSAGE);
                    AddRow = false;
                }
            }
            conn.close();
        }else
            JOptionPane.showMessageDialog(rootPane, "Error: Not enough Data for Columns", "Database Insert Error", JOptionPane.ERROR_MESSAGE);

        return AddRow;
    }
   
   public boolean createTable(String dbLocation, List <String> Param, boolean Regions) throws SQLException{
        Component rootPane = null;
        String sql = "" ;
        String TBL = Param.get(0);
        boolean CreateTable = false;
        Statement st = null;
        
        
        Param.remove(0);

        if (Regions){
            //Creating SQL Statement
            sql =  "CREATE TABLE [" +TBL + "] ([Id] COUNTER, ";

            for (int i = 0; i < Param.size(); i++){
                
                if (i == Param.size()-1)
                    sql = sql + "[" + Param.get(i) + "] VARCHAR(255), PRIMARY KEY (ID))";
                else
                    sql = sql + "[" + Param.get(i) + "] VARCHAR(255), ";
            }
        }else{
            //Creating SQL Statement
            sql =  "CREATE TABLE [" + TBL + "] ([ID] COUNTER, ";

            for (int i = 0; i < Param.size(); i++){
                if (i == Param.size()-1)
                    sql = sql + "[" + Param.get(i) + "] VARCHAR(5), PRIMARY KEY (ID))";
                else
                    sql = sql + "[" + Param.get(i) + "] VARCHAR(255), ";
            }
        }

        //SQL Testing
        //sql = "CREATE TABLE USERS (ID COUNTER, NAME VARCHAR(50), PRIMARY KEY(Id))";
        
        //Making a connection to the database
        try{
            conn=DriverManager.getConnection("jdbc:ucanaccess://" + dbLocation +  "/" + dbName);
            st = conn.createStatement();
        }catch(SQLException e){
             JOptionPane.showMessageDialog(rootPane, "Error 700.6: Unable to connect to Database", "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        
        //Executing SQL Query
        try{
            st.executeUpdate(sql);
            st.close();
            conn.close();
            CreateTable = true;
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(rootPane, "Error 701.4: SQL String could not be executed", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return CreateTable;
    }
   
   
   
   
   
//        Database db = DatabaseBuilder.open(new File("/" +dbLocation[0] + "/"+ dbLocation[1] +  "/" + dbName));
//        db.setColumnOrder(Table.ColumnOrder.DISPLAY);
//        boolean foundTable = false;
//        boolean countryFound = false;
//        boolean checkRegion = false;
//        boolean regionFound = false;
//        boolean foundLatitude = false;
//        String Latitude = "";
//        
//        List <String> list = new ArrayList();        
//        try  {
////            System.out.println("Connected to Weather Database - Getting Table");
//
//            //Finding the Country
//            Table tbl = db.getTable("__Countries");
//            
//            for (Row row : tbl){
//                for(Column column : tbl.getColumns()) {
//                    String columnName = column.getName();
//                    Object value = row.get(columnName);
//                    
//                    if (value.toString().equalsIgnoreCase(Country)){
//                       list.add(value.toString());
//                       countryFound = true;
//                    }else if (countryFound == true){
//                        list.add(value.toString());
//                        checkRegion = true;
//                        countryFound = false;
//                    }else if (checkRegion == true){
//                        list.add(value.toString());
//                        countryFound = false;
//                        checkRegion = false;
//                    }
//                }
//            }
//            
//            
//            //Finding the Region
//            tbl = db.getTable(list.get(1));
//            
//             for (Row row : tbl){
//                for(Column column : tbl.getColumns()) {
//                    String columnName = column.getName();
//                    Object value = row.get(columnName);
//                    
//                    if (list.get(2).equalsIgnoreCase("true")){  //True/False from database for regions
//                        
//                        if (value.toString().equalsIgnoreCase(Region)){ //Finds the region that was passed with the call to this function
//                            list.add(value.toString());
//                            regionFound = true;
//                        }else if(regionFound == true){  //Adds the region to the list so the region can be searched Through.
//                            list.add(value.toString());
//                            regionFound = false;
//                        }
//                    }
//                    else{
//                        
//                    }
//                }
//             }
//            
//            //Finding the file to get the Latitude
//            tbl = db.getTable(list.get(4));
//            
//            for (Row row : tbl){
//                for(Column column : tbl.getColumns()) {
//                    String columnName = column.getName();
//                    Object value = row.get(columnName);
//                    
//                    if (value.toString().equalsIgnoreCase(fileName)){
//                        foundLatitude = true;
//                    }else if (foundLatitude == true && columnName.equalsIgnoreCase("Latitude")){
//                        Latitude = value.toString();
//                        foundLatitude = false;
//                    }
//                }
//            }
//            db.flush();
//        }catch(IOException e){
//            e.printStackTrace(System.out);
//        }
//        db.close();
        
//        return Latitude;
   
   public boolean deleteRowFromDB(String dbLocation, String TBL, String columnName, String selection) throws SQLException{
        Component rootPane = null;
        String sql = "" ;
        boolean deleteRow = false;
        Statement st = null;
        
        //Creating SQL Statement
        sql =  "DELETE FROM [" +TBL + "] WHERE [" + columnName + "] = '" + selection +"'";

        
        //Making a connection to the database
        try{
            conn=DriverManager.getConnection("jdbc:ucanaccess://" + dbLocation +  "/" + dbName);
            st = conn.createStatement();
        }catch(SQLException e){
             JOptionPane.showMessageDialog(rootPane, "Error 700.6: Unable to connect to Database", "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        
        //Executing SQL Query
        try{
            st.executeUpdate(sql);
            st.close();
            conn.close();
            deleteRow = true;
        }catch(SQLException ex){
            System.out.println(ex);
            JOptionPane.showMessageDialog(rootPane, "Error 701.4: SQL String could not be executed", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return deleteRow;
   }
   public boolean deleteTable(String dbLocation, String TBL){
        Component rootPane = null;
        String sql = "" ;
        boolean deleteTable = false;
        Statement st = null;
        
        //Creating SQL Statement
        sql =  "DROP TABLE " + TBL;

        
        //Making a connection to the database
        try{
            conn=DriverManager.getConnection("jdbc:ucanaccess://" + dbLocation +  "/" + dbName);
            st = conn.createStatement();
        }catch(SQLException e){
             JOptionPane.showMessageDialog(rootPane, "Error 700.6: Unable to connect to Database", "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        
        //Executing SQL Query
        try{
            st.executeUpdate(sql);
            st.close();
            conn.close();
            deleteTable = true;
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(rootPane, "Error 701.4: SQL String could not be executed", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
       return deleteTable;
   }
    
}
