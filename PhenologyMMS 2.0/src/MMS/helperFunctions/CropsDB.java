/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MMS.helperFunctions;
import com.healthmarketscience.jackcess.*;
import com.healthmarketscience.jackcess.Table.ColumnOrder;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;




/**
 *
 * @author mike.herder
 */
public class CropsDB {
    private Connection conn;
    private ResultSet rs;
    private PreparedStatement ps;
    private String dbName = "Phenology.accdb";
    public Table.ColumnOrder DATA;
    private Component rootPane;


    public List readOneColumnFromDB(String dbLocation, String table, String column, int sort) throws SQLException{
        List results = new ArrayList();
        Statement s = null;
        ResultSet rs = null;
        try{
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn=DriverManager.getConnection("jdbc:ucanaccess://" + dbLocation +  "/" + dbName);
            s = conn.createStatement();
            
            
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(rootPane, "Error 700.1: Unable to connect to Database", "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        try{    
            if (sort == 0)
                rs = s.executeQuery("SELECT [" + column + "] FROM [" + table + "]");
            else{
                System.out.println("SELECT [" + column + "] FROM [" + table + "] WHERE [GDD METHOD]]=\"" + sort + "\"");
                rs = s.executeQuery("SELECT [" + column + "] FROM [" + table + "] WHERE [GDD METHOD]=\"" + sort + "\"");
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(rootPane, "Error 701.1: SQL String could not be executed", "Database Error", JOptionPane.ERROR_MESSAGE);
        }    
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        if (results.size() == 0)
            results.add("none");
                
        conn.close();
        return results;
    }

    /*
    *   Returns 1 row of data from a table in the crops database
    *   
    */
    public List <String> readOneRowFromDB(String dbLocation, String tableName,  String column, String selection) throws IOException, SQLException{
        
        List results = new ArrayList();
        Statement s = null;
        ResultSet rs = null;
        int columnNumber = 0;
        
        Database db = DatabaseBuilder.open(new File("/" +dbLocation +  "/" + dbName));
        db.setColumnOrder(ColumnOrder.DISPLAY);
                
        Table table = db.getTable(tableName);
        for (Row row : table){
//            System.out.println(row);
            for (Column col : table.getColumns()){
                if (col.getName().equalsIgnoreCase(column))
                    if (row.get(col.getName()).toString().equalsIgnoreCase(selection)){
//                        System.out.println("WOOOO HOOOOOO :" + row);
                        results.addAll(row.values());
                    }
            }
        }
        
        if (!results.isEmpty())
            results.remove(0);
 

//        try{
//            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
//            conn=DriverManager.getConnection("jdbc:ucanaccess://" + dbLocation[0] + "/"+ dbLocation[1] +  "/" + dbName);
//            s = conn.createStatement();
//        }catch(Exception e){
//            JOptionPane.showMessageDialog(rootPane, "Error 700.2: Unable to connect to Database", "Database Connection Error", JOptionPane.ERROR_MESSAGE);
//        }
//
//        try{
//             if (column == null)
//                rs = s.executeQuery("SELECT * FROM [" + tableName + "] WHERE [CropType]=\"" + selection + "\"" );
//            else
//                rs = s.executeQuery("SELECT * FROM [" + tableName + "] WHERE [" + column + "]=\"" + selection + "\"" );
//            
//            ResultSetMetaData rsmd = rs.getMetaData();
//            columnNumber = rsmd.getColumnCount();    
//        }catch(SQLException er){
//            JOptionPane.showMessageDialog(rootPane, "Error 701.2: SQL String could not be executed", "Database Error", JOptionPane.ERROR_MESSAGE);
//        }
//
//        while (rs.next()) {
//            for (int i = 2; i <= columnNumber; i++){
//                results.add(rs.getString(i));   //Get Crop Type
//            }
//        }
//        if (results.size() == 0)
//            results.add("none");
//
//        conn.close();
        return results;
    } 
    
    /*
    *   Retrieves only the column header information for the Growth Stages
    */
    public List<String> getColumnHeaders(String dbLocation, String tableName) throws IOException{
        Database db = DatabaseBuilder.open(new File("/" +dbLocation +  "/" + dbName));
        db.setColumnOrder(ColumnOrder.DISPLAY);
        List <String> columnNames = new ArrayList();
        
        // Jackcess
        try {
            Table tbl = db.getTable(tableName);

            for (Column col : tbl.getColumns()) {
//                System.out.println(col.getName());
                
                if (col.getColumnIndex() > 0){
                    columnNames.add(col.getName());
                }
            }
            db.flush();
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(rootPane, "Error 700.3: Unable to connect to Database", "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        if (columnNames.size() == 0)
            columnNames.add("none");
        
        db.close();
        columnNames.remove(0);
        return columnNames;
    }
    
    public String getTable(String dbLocation[], String tableName,  String selection) throws IOException{
        boolean found = false;
        Database db = DatabaseBuilder.open(new File("/" +dbLocation +  "/" + dbName));
        db.setColumnOrder(ColumnOrder.DISPLAY);
        
        try {
            Table tbl = db.getTable(tableName);
            for (Row row : tbl){
                for(Column column : tbl.getColumns()) {
                    String columnName = column.getName();
                    Object value = row.get(columnName);
                    if (value != null){
                        if (value.toString().equalsIgnoreCase(selection)){
                            found = true;
                        }else if(found == true){
                            found = false;
                            tableName = value.toString();
                        }
                    }
                }
            }
            db.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(rootPane, "Error 700.4: Unable to connect to Database", "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        if (tableName.length() == 0)
            tableName = "none";
        
        db.close();
        return tableName;
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
                    JOptionPane.showMessageDialog(rootPane, "Error 701.3: SQL String could not be executed, Data was not added", "Database Error", JOptionPane.ERROR_MESSAGE);
                    AddRow = false;
                }
            }
            conn.close();
        }else
            JOptionPane.showMessageDialog(rootPane, "Error: Not enough Data for Columns", "Database Insert Error", JOptionPane.ERROR_MESSAGE);

        return AddRow;
    }
    
    public boolean createTable(String dbLocation, List <String> Param) throws SQLException{
        Component rootPane = null;
        String sql = "" ;
        String TBL = Param.get(0);
        boolean CreateTable = false;
        Statement st = null;
        
        
        Param.remove(0);

        //Creating SQL Statement
        sql =  "CREATE TABLE " + TBL + " (ID COUNTER, ";
        
        for (int i = 0; i < Param.size(); i++){
            if (i == 0)
                sql = sql + "[" + Param.get(i) + "] VARCHAR(255), ";
            else
                sql = sql + "[" + Param.get(i) + "] VARCHAR(7), ";
        }
        sql = sql + " PRIMARY KEY (ID))";

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
        sql =  "DROP TABLE [" + TBL + "]";

        
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

