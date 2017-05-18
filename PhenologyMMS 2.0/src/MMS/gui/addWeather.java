/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MMS.gui;

import MMS.helperFunctions.FileFunctions;
import MMS.helperFunctions.WeatherDB;
import java.awt.Color;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author mike.herder
 */
public class addWeather extends javax.swing.JFrame {
    private List <String> WeatherCountry = new ArrayList();
    private List <String> WeatherRegion = new ArrayList();
    private WeatherDB wDB = new WeatherDB();
    private List <String> CountryData = new ArrayList();
    private List <String> RegionData = new ArrayList();
    public String Directory = "";
    private Iterator itr;
    private boolean loading = false;
    private FileFunctions fp = new FileFunctions();
    
    /**
     * Creates new form addWeather
     */
    
    public addWeather() {
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
         Directory = "E:/PhenologyMMS 1.3/";
        
        loading = true;
        /*
        * Getting a list of countries from the database to populate jComboBox3
        */
        try{
        WeatherCountry = wDB.readOneColumnFromDB(Directory, "__Countries", "CountryName", null);
        }catch(SQLException e){
            JOptionPane.showMessageDialog(rootPane, "Error 201.1: Unable to retrieve Column of Data from Weather Database", "Database Error", JOptionPane.ERROR_MESSAGE);
        }    
        itr = WeatherCountry.iterator();
        while (itr.hasNext()){
            try{
                Country.addItem(itr.next().toString());
             }catch(NullPointerException ex){
                JOptionPane.showMessageDialog(rootPane, "Error 900.2: Null Pointer Exceptions", "Field Error", JOptionPane.ERROR_MESSAGE);
             }
        }
        if (Country.getItemCount() != 0){
            try {
                //Retrieving selected Country
                WeatherCountry = wDB.getTable(Directory, "__Countries", Country.getSelectedItem().toString());
            } catch (IOException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane, "Error 200.1: Unable to retrieve Table from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
            }
        
            if (WeatherCountry.get(2).equalsIgnoreCase("true")){
            //Read Weather database for Region Information
                try {
                    WeatherRegion = wDB.readOneColumnFromDB(Directory, WeatherCountry.get(1), "State", null);
                } catch (SQLException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(rootPane, "Error 201.2: Unable to retrieve Column of Data from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }

                //Adding Region Names
                try{
                    if (Country.getItemCount() != 0) {
                        Region.removeAllItems();

                        Iterator itr = WeatherRegion.iterator();
                        while (itr.hasNext())
                            Region.addItem(itr.next().toString());
                    }
                }catch(NullPointerException ex){
                    JOptionPane.showMessageDialog(rootPane, "Error 900.3: Unable to retrieve Table from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }
                
                try {
                    CountryData = wDB.readOneRowFromDB(Directory, "__Countries", "CountryName", Country.getSelectedItem().toString());
                } catch (SQLException ex) {
                    Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(rootPane, "Error 201.2: Unable to retrieve Column of Data from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }
                    
                if (CountryData.get(2).equalsIgnoreCase("true")){
                    if (Region.getItemAt(0).equals("none")){
                        WeatherStationName.setEnabled(false);
                        latitudeTextField.setEnabled(false);
                        WeatherButton.setEnabled(false);
                        JOptionPane.showMessageDialog(rootPane, "Error ###.#: Your Country has no Regions defined, please Define a region", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                    }else{
                        WeatherStationName.setEnabled(true);
                        latitudeTextField.setEnabled(true);
                        WeatherButton.setEnabled(true);
                    }    
                }
            }
        }
        loading = false;
    }
    public addWeather(String location) {
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        Directory = location;
        
        loading = true;
        /*
        * Getting a list of countries from the database to populate jComboBox3
        */
        try{
        WeatherCountry = wDB.readOneColumnFromDB(Directory, "__Countries", "CountryName", null);
        }catch(SQLException e){
            JOptionPane.showMessageDialog(rootPane, "Error 201.1: Unable to retrieve Column of Data from Weather Database", "Database Error", JOptionPane.ERROR_MESSAGE);
        }    
        itr = WeatherCountry.iterator();
        while (itr.hasNext()){
            try{
                Country.addItem(itr.next().toString());
             }catch(NullPointerException ex){
                JOptionPane.showMessageDialog(rootPane, "Error 900.2: Null Pointer Exceptions", "Field Error", JOptionPane.ERROR_MESSAGE);
             }
        }
        if (Country.getItemCount() != 0){
            try {
                //Retrieving selected Country
                WeatherCountry = wDB.getTable(Directory, "__Countries", Country.getSelectedItem().toString());
            } catch (IOException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane, "Error 200.1: Unable to retrieve Table from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
            }
        
            if (WeatherCountry.get(2).equalsIgnoreCase("true")){
            //Read Weather database for Region Information
                try {
                    WeatherRegion = wDB.readOneColumnFromDB(Directory, WeatherCountry.get(1), "State", null);
                } catch (SQLException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(rootPane, "Error 201.2: Unable to retrieve Column of Data from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }

                //Adding Region Names
                try{
                    if (Country.getItemCount() != 0) {
                        Region.removeAllItems();

                        Iterator itr = WeatherRegion.iterator();
                        while (itr.hasNext())
                            Region.addItem(itr.next().toString());
                    }
                }catch(NullPointerException ex){
                    JOptionPane.showMessageDialog(rootPane, "Error 900.3: Unable to retrieve Table from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }
                
                try {
                    CountryData = wDB.readOneRowFromDB(Directory, "__Countries", "CountryName", Country.getSelectedItem().toString());
                } catch (SQLException ex) {
                    Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(rootPane, "Error 201.2: Unable to retrieve Column of Data from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }
                    
                if (CountryData.get(2).equalsIgnoreCase("true")){
                    if (Region.getItemAt(0).equals("none")){
                        WeatherStationName.setEnabled(false);
                        latitudeTextField.setEnabled(false);
                        WeatherButton.setEnabled(false);
                        JOptionPane.showMessageDialog(rootPane, "Error ###.#: Your Country has no Regions defined, please Define a region", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                    }else{
                        WeatherStationName.setEnabled(true);
                        latitudeTextField.setEnabled(true);
                        WeatherButton.setEnabled(true);
                    }    
                }
            }
        }
        loading = false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Country = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        Region = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        latitudeTextField = new javax.swing.JTextField();
        WeatherButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        WeatherStationName = new javax.swing.JTextField();
        statusLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        AddCountry = new javax.swing.JButton();
        statusLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        Country1 = new javax.swing.JTextField();
        AlphaCode1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        HasRegions1 = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        tableName1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        Country2 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        AlphaCode2 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        tableName2 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        HasRegions2 = new javax.swing.JCheckBox();
        jLabel16 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        regionField = new javax.swing.JTextField();
        AddRegion = new javax.swing.JButton();
        statusLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        Country3 = new javax.swing.JComboBox();
        tableName3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Select Country:");

        Country.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        Country.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CountryActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Select Region (State):");

        Region.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Weather File Latitude:");

        WeatherButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        WeatherButton.setText("Add Weather File");
        WeatherButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WeatherButtonActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Weather Station Name:");

        WeatherStationName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        statusLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        statusLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statusLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(235, 235, 235)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6))
                            .addGap(26, 26, 26)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(latitudeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(WeatherStationName, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Country, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(Region, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(WeatherButton)
                            .addGap(69, 69, 69)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(311, 311, 311)
                        .addComponent(statusLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(294, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Country, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(Region, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(WeatherStationName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(latitudeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(88, 88, 88)
                .addComponent(WeatherButton)
                .addGap(47, 47, 47)
                .addComponent(statusLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel3});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {Country, Region});

        jTabbedPane1.addTab("Add Weather File", jPanel1);

        AddCountry.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AddCountry.setText("Add Country");
        AddCountry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddCountryActionPerformed(evt);
            }
        });

        statusLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        statusLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statusLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Do It Yourself", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Country Name:");

        Country1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        AlphaCode1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AlphaCode1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                AlphaCode1FocusLost(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("Country ALPHA 3 Code:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Does Your Country have Regions or States:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setText("Your New Country Table Name:");

        tableName1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tableName1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Country1)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(AlphaCode1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(HasRegions1))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel14))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                                .addComponent(tableName1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(Country1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AlphaCode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14)
                    .addComponent(tableName1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(HasRegions1))
                .addGap(27, 27, 27))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "The Easy Way", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 14))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Country Name:");

        Country2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Country2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Country2ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Country ALPHA 3 Code:");

        AlphaCode2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AlphaCode2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlphaCode2ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Your New Country Table Name:");

        tableName2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tableName2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setText("Does Your Country have Regions or States:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Country2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AlphaCode2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tableName2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(HasRegions2)))))
                .addGap(32, 32, 32))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Country2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(AlphaCode2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(tableName2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(HasRegions2))
                .addGap(24, 24, 24))
        );

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 0, 0));
        jLabel16.setText("Note: Do not add Quotation Marks (\"\") in Country Name");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(AddCountry))
                    .addComponent(statusLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(291, 291, 291))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(AddCountry)
                .addGap(18, 18, 18)
                .addComponent(statusLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Add Country", jPanel2);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Region Name:");

        regionField.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        regionField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                regionFieldFocusLost(evt);
            }
        });

        AddRegion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AddRegion.setText("Add Region");
        AddRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddRegionActionPerformed(evt);
            }
        });

        statusLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        statusLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statusLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("Your New Region Table:");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Country Name:");

        Country3.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        Country3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Country3ActionPerformed(evt);
            }
        });

        tableName3.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        tableName3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(statusLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(219, 219, 219)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel10)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(regionField, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tableName3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(AddRegion)
                                    .addGap(37, 37, 37))))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addGap(200, 200, 200)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(Country3, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(304, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {regionField, tableName3});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Country3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(37, 37, 37)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(regionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(tableName3, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                .addComponent(AddRegion)
                .addGap(61, 61, 61)
                .addComponent(statusLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {regionField, tableName3});

        jTabbedPane1.addTab("Add Region", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 855, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void WeatherButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WeatherButtonActionPerformed
        File dir = new File(Directory + "/Interface/MMSWeather");
        ImageFilter filter = new ImageFilter();
        JFileChooser fc = new JFileChooser();
        File newFile = null;

        Path CountryPath = Paths.get(Directory+ "/Interface/MMSWeather/" + Country.getSelectedItem()+ "/");
        Path RegionPath = Paths.get(Directory+ "/Interface/MMSWeather/" + Country.getSelectedItem()+ "/" + Region.getSelectedItem() + "/");
        
        boolean returnVal = false;
        boolean country = false;
        boolean region = false;
        List <String> columns = new ArrayList();
        List <String> data = new ArrayList();
        List <String> fileYears = new ArrayList();
        
        /********************************************************************
         * Getting File Data and Moving it to appropriate Directory
         *******************************************************************/

        fc.setDialogTitle("Select A Weather File");
        fc.setCurrentDirectory(dir);
        fc.setFileFilter(filter);

        if (fc.showDialog(null, "Select") == 0){
            newFile = fc.getSelectedFile();
            
            //Getting information from weatherfile
            try {
                fileYears = fp.readWeatherFile(newFile.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
            }
            
//            System.out.println(Files.exists(CountryPath));
//            System.out.println(Files.exists(RegionPath));

            //Checking to make sure the country folder exists
            if (Files.exists(CountryPath)){
                country = true;
            }else{
                //Create the Country Folder
                File cDir = new File(CountryPath.toString());
                cDir.mkdir();
                country = true;
            }
            //Checking to make sure the region exists
            if(WeatherCountry.get(2).equalsIgnoreCase("true") && !Files.exists(RegionPath)){
                //Create the Region Folder
                if (!Region.getSelectedItem().toString().equalsIgnoreCase("none")){
                    File rDir = new File (RegionPath.toString());
                    rDir.mkdir();
                    region = true;
                }
            }else if (WeatherCountry.get(2).equalsIgnoreCase("true") && Files.exists(RegionPath))
                region = true;
            else if (WeatherCountry.get(2).equalsIgnoreCase("false"))
                region = false;
            
            //Erasing data from lists, so database can be inserted or made
            columns.clear();
            data.clear();
            
            //Checking to see where we are placing the file, Country or Region
            if (country == true)
                if (region == true && WeatherCountry.get(2).equalsIgnoreCase("true")){
                    //Find table for Country
                    try{
                        CountryData = wDB.readOneRowFromDB(Directory, "__Countries", "CountryName", Country.getSelectedItem().toString());
                    } catch (SQLException ex) {
                        Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //Find table for Region
                    try {
                        //Find table for Country
                        RegionData = wDB.readOneRowFromDB(Directory, CountryData.get(1), "State", Region.getSelectedItem().toString());
                    } catch (SQLException ex) {
                        Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //Now Adding Data to Appropriate Table
                    columns.add("WeatherStation");
                    columns.add("fileName");
                    columns.add("Latitude");
                    columns.add("StartYear");
                    columns.add("EndYear");

                    data.add(WeatherStationName.getText());
                    data.add(Country.getSelectedItem()+ "/" + Region.getSelectedItem() + "/" + newFile.getName());
                    data.add(latitudeTextField.getText());
                    data.add(fileYears.get(0));
                    data.add(fileYears.get(1));

                    //Add Data to Database
                    try{
                        returnVal = wDB.addRowToTable(Directory, RegionData.get(1), columns, data);
                    }catch(SQLException ex){
                        Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Moving files into appropriate place
                  
                    
                    if (returnVal){
                        RegionPath = Paths.get(RegionPath + "/" + newFile.getName());
                        System.out.println(RegionPath);
                        try {
                            Files.copy(Paths.get(newFile.getAbsolutePath()), RegionPath, REPLACE_EXISTING);

                            System.out.println("File Copy Successful");
                        } catch (IOException ex) {
                            Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("File Copy Failed");
                        }
                    }
                }else{
                    //Now Adding Data to Appropriate Table
                    columns.add("WeatherStation");
                    columns.add("fileName");
                    columns.add("Latitude");
                    columns.add("StartYear");
                    columns.add("EndYear");

                    data.add(WeatherStationName.getText());
                    data.add(Country.getSelectedItem()+ "/" + Region.getSelectedItem() + "/" + newFile.getName());
                    data.add(latitudeTextField.getText());
                    data.add(fileYears.get(0));
                    data.add(fileYears.get(1));
            
                    //Add Data to Database
                    try{
                        returnVal = wDB.addRowToTable(Directory, CountryData.get(1), columns, data);
                    }catch(SQLException ex){
                        Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(rootPane, "Error ###.#: Your Country has no Regions defined, please Define a region", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                    if (returnVal){
                        //placing file under the Country directory
                        System.out.println(newFile.getAbsolutePath());
                        //  newFile.renameTo(new File (Directory[0] + "/" + Directory[1]+ "/Interface/MMSWeather/" + Country.getSelectedItem()+ "/" + fc.getName()));
                        CountryPath = Paths.get(CountryPath + "/" + newFile.getName());
                        System.out.println(CountryPath);
                        try {
                            Files.copy(Paths.get(newFile.getAbsolutePath()), CountryPath, REPLACE_EXISTING);

                            System.out.println("File Copy Successful");
                        } catch (IOException ex) {
                            Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("File Copy Failed");
                        }
                    }
                }
            if (returnVal){
                statusLabel4.setVisible(true);
                statusLabel4.setForeground(Color.BLUE);
                statusLabel4.setText("Successful");
            }else{
                statusLabel4.setVisible(true);
                statusLabel4.setForeground(Color.red);
                statusLabel4.setText("Failed");
            }
        }
        
                    
        //Reset Add Weather File Tab
        SetAddWeatherFileTab();
    }//GEN-LAST:event_WeatherButtonActionPerformed

    private void CountryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CountryActionPerformed
        if (Country.getItemCount() != 0){
            Region.setEnabled(true);
            WeatherStationName.setText(null);
            latitudeTextField.setText(null);
            
            
            try {
                //Retrieving selected Country
                WeatherCountry = wDB.getTable(Directory, "__Countries", Country.getSelectedItem().toString());
            } catch (IOException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane, "Error 200.1: Unable to retrieve Table from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
            }
        
            if (WeatherCountry.get(2).equalsIgnoreCase("true")){
            //Read Weather database for country Information
                try {
                    WeatherRegion = wDB.readOneColumnFromDB(Directory, WeatherCountry.get(1), "State", null);
                } catch (SQLException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(rootPane, "Error 201.2: Unable to retrieve Column of Data from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }

                //Adding Region Names
                try{

                    Region.removeAllItems();

                    Iterator itr = WeatherRegion.iterator();
                    while (itr.hasNext())
                        Region.addItem(itr.next().toString());
                }catch(NullPointerException ex){
                    JOptionPane.showMessageDialog(rootPane, "Error 900.3: Unable to retrieve Table from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }
                if (Region.getItemAt(0).equals("none")){
                    JOptionPane.showMessageDialog(rootPane, "Error ###.#: Your Country has no Regions defined, please Define a region", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                    WeatherStationName.setEnabled(false);
                    latitudeTextField.setEnabled(false);
                    WeatherButton.setEnabled(false);
                }else{
                        WeatherStationName.setEnabled(true);
                        latitudeTextField.setEnabled(true);
                        WeatherButton.setEnabled(true);
                }
            }else{
                Region.removeAllItems();
                Region.setEnabled(false);
            }
        }  
    }//GEN-LAST:event_CountryActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if (jTabbedPane1.getSelectedIndex() == 0 && Country.getItemCount() > 0){
            SetAddWeatherFileTab();
        }
        else if (jTabbedPane1.getSelectedIndex() == 1){
            SetAddCountryTab();
        }
        else if (jTabbedPane1.getSelectedIndex() == 2){
            SetAddRegionTab();
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void Country2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Country2ActionPerformed
        //Getting the ALPHA3 Code for the selected Country
        if (Country2.getItemCount() != 0 && loading == false){
            try {
                //Retrieving selected Country
                WeatherCountry = wDB.readOneRowFromDB(Directory, "ALPHA3","English short name" ,Country2.getSelectedItem().toString());
//                    WeatherCountry = wDB.getTable(Directory, "ALPHA3",Country2.getSelectedItem().toString());
            } catch (SQLException ex) {
                Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane, "Error 201.1: Unable to retrieve Column of Data from Weather Database", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            if (WeatherCountry != null || !WeatherCountry.contains("none")){
                //Adding ALPHA3 Names
                try{
                    AlphaCode2.removeAllItems();
                    tableName2.setText("");
                    
                    if (WeatherCountry.size() > 1){
                        loading = true;
                        AlphaCode2.addItem(WeatherCountry.get(1));
                        loading = false;
                        AlphaCode2.addItem("Select Option");
                        AlphaCode2.setSelectedItem("Select Option");
                    }
                }catch(NullPointerException ex){
                    JOptionPane.showMessageDialog(rootPane, "Error 900.3: Unable to retrieve Table from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_Country2ActionPerformed

    private void AlphaCode2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlphaCode2ActionPerformed
        if (loading == false){
            if (AlphaCode2.getItemCount() > 0){
                if (!AlphaCode2.getSelectedItem().toString().equalsIgnoreCase("Select Option"))
                    tableName2.setText("A_" + AlphaCode2.getSelectedItem().toString());
                else
                    tableName2.setText("");
            }
        }
    }//GEN-LAST:event_AlphaCode2ActionPerformed

    private void AlphaCode1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AlphaCode1FocusLost
        if (AlphaCode1.getText().trim().replaceAll("\\s+","").length() > 0 && AlphaCode1.getText().trim().replaceAll("\\s+","").length() < 4)
            tableName1.setText("A_" + AlphaCode1.getText().trim().replaceAll("\\s+",""));
        else if (AlphaCode1.getText().trim().replaceAll("\\s+","").length() == 0 || AlphaCode1.getText().length() > 3)
            JOptionPane.showMessageDialog(rootPane, "Error ###.#: Please use the ISO 3 Character Standard to represent your Country", "Alpha 3 Code Error", JOptionPane.ERROR_MESSAGE);
        else
            tableName1.setText(null);
    }//GEN-LAST:event_AlphaCode1FocusLost

    private void Country3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Country3ActionPerformed
        if (loading == false){
        
            regionField.setText(null);
            tableName3.setText(null);
            WeatherCountry.clear();
            if (Country.getItemCount() != 0){
                try {
                    //Retrieving selected Country
                    WeatherCountry = wDB.getTable(Directory, "__Countries", Country3.getSelectedItem().toString());
                } catch (IOException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(rootPane, "Error 200.1: Unable to retrieve Table from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_Country3ActionPerformed

    private void regionFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_regionFieldFocusLost
        if (regionField.getText().length() > 0){
            String CountryName = WeatherCountry.get(1).substring(2, WeatherCountry.get(1).length());
            tableName3.setText(CountryName + "_" + regionField.getText().trim().replaceAll("\\s+",""));
        }
    }//GEN-LAST:event_regionFieldFocusLost

    private void AddCountryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddCountryActionPerformed
        boolean DIY = false;
        boolean TEW = false;
        boolean  returnVal = false;
        
        if (Country1.getText().trim().length() > 0)
            if (AlphaCode1.getText().trim().replaceAll("\\s+","").length() > 0)
                DIY = true;
        
        if (!Country2.getSelectedItem().toString().equalsIgnoreCase("none"))
            if (!AlphaCode2.getSelectedItem().toString().equalsIgnoreCase("Select Option"))
                TEW = true;
        
        if (DIY && TEW){
            JOptionPane.showMessageDialog(rootPane, "Error ###.#: You can not Provide answers in both columns, Please Try Again", "Add Country Error", JOptionPane.ERROR_MESSAGE);
            Country1.setText(null);
            AlphaCode1.setText(null);
            tableName1.setText(null);
            HasRegions1.setSelected(false);
            
            Country2.setSelectedItem("none");
            AlphaCode2.removeAllItems();
            tableName2.setText(null);
            HasRegions2.setSelected(false);
        }else if (!DIY && !TEW){
            JOptionPane.showMessageDialog(rootPane, "Error ###.#: You must fill out at least 1 column, Please Try Again", "Add Country Error", JOptionPane.ERROR_MESSAGE);            
        }else{
            List <String> columns = new ArrayList();
            List <String> data = new ArrayList();
            
            
            //Add New Country Information to __Countries
            columns.add("CountryName");
            columns.add("Alpha-3 Code");
            columns.add("Has Regions");
            
            //Add New Table for Country
            if (DIY == true){
                data.add(Country1.getText());
                data.add(tableName1.getText());
                data.add(Boolean.toString(HasRegions1.isSelected()));
                
                try {
                     returnVal = wDB.addRowToTable(Directory, "__Countries", columns, data);
                } catch (SQLException ex) {
                    Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(rootPane, "Error ###.#: You must fill out at least 1 column, Please Try Again", "Add Country Error", JOptionPane.ERROR_MESSAGE);            
                }
                
                if (returnVal){
                    data.clear();

                    if (HasRegions1.isSelected()){
                        data.add(tableName1.getText());
                        data.add("State");
                        data.add("StateCode");

                        try {
                            returnVal = wDB.createTable(Directory, data, true);
                        } catch (SQLException ex) {
                            Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(rootPane, "Error ###.#: You must fill out at least 1 column, Please Try Again", "Add Country Error", JOptionPane.ERROR_MESSAGE);            
                        }
                    }else{
                        data.add(tableName1.getText());
                        data.add("WeatherStation");
                        data.add("fileName");
                        data.add("Latitude");
                        data.add("StartYear");
                        data.add("EndYear");

                        try {
                             returnVal = wDB.createTable(Directory, data, false);
                        } catch (SQLException ex) {
                            Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(rootPane, "Error ###.#: You must fill out at least 1 column, Please Try Again", "Add Country Error", JOptionPane.ERROR_MESSAGE);            
                        }
                    }
                    if (returnVal){
                        statusLabel2.setForeground(Color.green);
                        statusLabel2.setText("Successful");
                    }else{
                        statusLabel2.setForeground(Color.red);
                        statusLabel2.setText("Failed");
                        //Delete Inserted Row from __Country table
                        try {
                            wDB.deleteRowFromDB(Directory, "__Countries", "CountryName", WeatherCountry.get(1));
                        } catch (SQLException ex) {
                            Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }else{
                data.add(Country2.getSelectedItem().toString());
                data.add(tableName2.getText());
                data.add(Boolean.toString(HasRegions2.isSelected()));
                
                try {
                    returnVal = wDB.addRowToTable(Directory, "__Countries", columns, data);
                } catch (SQLException ex) {
                    Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(rootPane, "Error ###.#: You must fill out at least 1 column, Please Try Again", "Add Country Error", JOptionPane.ERROR_MESSAGE);            
                }
                
                if (returnVal){
                
                    data.clear();

                    if (HasRegions2.isSelected()){
                        data.add(tableName2.getText());
                        data.add("State");
                        data.add("StateCode");

                        try {
                            returnVal = wDB.createTable(Directory, data, true);
                        } catch (SQLException ex) {
                            Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(rootPane, "Error ###.#: You must fill out at least 1 column, Please Try Again", "Add Country Error", JOptionPane.ERROR_MESSAGE);            
                        }
                    }else{
                        data.add(tableName2.getText());
                        data.add("WeatherStation");
                        data.add("fileName");
                        data.add("Latitude");
                        data.add("StartYear");
                        data.add("EndYear");

                        try {
                            returnVal = wDB.createTable(Directory, data, false);
                        } catch (SQLException ex) {
                            Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(rootPane, "Error ###.#: You must fill out at least 1 column, Please Try Again", "Add Country Error", JOptionPane.ERROR_MESSAGE);            
                        }
                    }
                    if (returnVal){
                        statusLabel2.setForeground(Color.BLUE);
                        statusLabel2.setText("Successful");
                    }else{
                        statusLabel2.setForeground(Color.red);
                        statusLabel2.setText("Failed");
                        //Delete Inserted Row from __Country table
                        try {
                            wDB.deleteRowFromDB(Directory, "__Countries", "CountryName", WeatherCountry.get(1));
                        } catch (SQLException ex) {
                            Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            //Reset Add Country Tab
            SetAddCountryTab();
        }
            
    }//GEN-LAST:event_AddCountryActionPerformed

    private void AddRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddRegionActionPerformed
        boolean returnVal = false;
        List <String> columns = new ArrayList();
        List <String> data = new ArrayList();
        
        System.out.println(WeatherCountry.get(1));
        if (regionField.getText().trim().length() > 0){
            
            //Add Region info to Country Table
            columns.add("STATE");
            columns.add("STATECODE");
            
            data.add(regionField.getText());
            data.add(tableName3.getText());
            
            try {
                returnVal = wDB.addRowToTable(Directory, WeatherCountry.get(1), columns, data);
            } catch (SQLException ex) {
                Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //Create Region Table
            if (returnVal){
                columns.clear();
                data.clear();
                columns.add(tableName3.getText());
                columns.add("WeatherStation");
                columns.add("fileName");
                columns.add("Latitude");
                columns.add("StartYear");
                columns.add("EndYear");
                
                try {
                    returnVal = wDB.createTable(Directory, columns, false);
                } catch (SQLException ex) {
                    Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (returnVal){
                    statusLabel3.setForeground(Color.BLUE);
                    statusLabel3.setText("Successful");
                }else{
                    statusLabel3.setForeground(Color.red);
                    statusLabel3.setText("Failed");
                    try {
                        wDB.deleteRowFromDB(Directory, "__Countries", "CountryName", WeatherCountry.get(1));
                    } catch (SQLException ex) {
                        Logger.getLogger(addWeather.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
               
                //Reset Add Region Tab
                SetAddRegionTab();
            }
            
        }else{
            //Throw Error telling them that a Region Field must have a name
            JOptionPane.showMessageDialog(rootPane, "Error ###.#: You must provide a region name, Please Try Again", "Add Region Error", JOptionPane.ERROR_MESSAGE);            
        }
        
        
    }//GEN-LAST:event_AddRegionActionPerformed

    /* ImageFilter.java is used by FileChooserDemo2.java. */
    class ImageFilter extends FileFilter {

        //Accept all directories and all gif, jpg, tiff, or png files.
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String fileName = f.getName();
            String extension[] = null; 
            boolean rightFile = fileName.endsWith(".txt");

            if (fileName.length() > 0) {
                if (f.getName().endsWith(".wthr") || f.getName().endsWith(".tif") || f.getName().endsWith(".gif") || f.getName().endsWith(".jpeg") || f.getName().endsWith(".jpg") || f.getName().endsWith(".png")){
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }


        //The description of this filter
        public String getDescription() {
            return "Only WeatherFiles";
        }
    }







    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

        } catch (Exception e) {
        
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new addWeather().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddCountry;
    private javax.swing.JButton AddRegion;
    private javax.swing.JTextField AlphaCode1;
    private javax.swing.JComboBox AlphaCode2;
    private javax.swing.JComboBox Country;
    private javax.swing.JTextField Country1;
    private javax.swing.JComboBox Country2;
    private javax.swing.JComboBox Country3;
    private javax.swing.JCheckBox HasRegions1;
    private javax.swing.JCheckBox HasRegions2;
    private javax.swing.JComboBox Region;
    private javax.swing.JButton WeatherButton;
    private javax.swing.JTextField WeatherStationName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField latitudeTextField;
    private javax.swing.JTextField regionField;
    private javax.swing.JLabel statusLabel2;
    private javax.swing.JLabel statusLabel3;
    private javax.swing.JLabel statusLabel4;
    private javax.swing.JLabel tableName1;
    private javax.swing.JLabel tableName2;
    private javax.swing.JLabel tableName3;
    // End of variables declaration//GEN-END:variables

    private void SetAddCountryTab(){
        List <String> AddedCountries = new ArrayList();
            
            //Resetting Screen
            HasRegions1.setSelected(false);
            HasRegions2.setSelected(false);
            Country1.setText(null);
            AlphaCode1.setText(null);
            tableName1.setText(null);
            
            AlphaCode2.removeAllItems();
            tableName2.setText(null);
            //statusLabel2.setText(null);
            
            
            loading = true;
            
            //Getting All the Countries we have listed
            try{
                WeatherCountry = wDB.readOneColumnFromDB(Directory, "ALPHA3", "English short name", null);
            }catch(SQLException e){
                JOptionPane.showMessageDialog(rootPane, "Error 201.1: Unable to retrieve Column of Data from Weather Database", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            
            /*
            * Getting a list of countries for comparison
            */
            try{
                AddedCountries = wDB.readOneColumnFromDB(Directory, "__Countries", "CountryName", null);
            }catch(SQLException e){
                JOptionPane.showMessageDialog(rootPane, "Error 201.1: Unable to retrieve Column of Data from Weather Database", "Database Error", JOptionPane.ERROR_MESSAGE);
            }    
            for (int x = 0; x < AddedCountries.size(); x++){
                for (int i = 0; i < WeatherCountry.size(); i++){
                    if (WeatherCountry.get(i).equalsIgnoreCase(AddedCountries.get(x)))
                        WeatherCountry.remove(AddedCountries.get(x));
                }                
                
            }
            
            Country2.removeAllItems();
            
            itr = WeatherCountry.iterator();
            while (itr.hasNext()){
                try{
                    Country2.addItem(itr.next().toString());
                 }catch(NullPointerException ex){
                    JOptionPane.showMessageDialog(rootPane, "Error 900.2: Null Pointer Exceptions", "Field Error", JOptionPane.ERROR_MESSAGE);
                 }
            }
            Country2.addItem("none");
            Country2.setSelectedItem("none");

            loading = false;
    }

    private void SetAddRegionTab(){
        loading = true;
            Country3.removeAllItems();
            
            List <String> CountryCheck = new ArrayList();
            regionField.setText(null);
            tableName3.setText(null);
            //statusLabel3.setText(null);
            
            if (Country3.getItemCount() > 0)
                Country3.setSelectedIndex(0);
            
            /*
            * Getting a list of countries from the database to populate Country3 combobox
            */
            try{
                WeatherCountry = wDB.readOneColumnFromDB(Directory, "__Countries", "CountryName", null);
            }catch(SQLException e){
                JOptionPane.showMessageDialog(rootPane, "Error 201.1: Unable to retrieve Column of Data from Weather Database", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
 
            int x = 0;
            while (x < WeatherCountry.size()){
                try {
                    //Retrieving selected Country
                    CountryCheck = wDB.getTable(Directory, "__Countries", WeatherCountry.get(x));
                } catch (IOException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(rootPane, "Error 200.1: Unable to retrieve Table from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }
                if (CountryCheck.get(2).equalsIgnoreCase("false"))
                    WeatherCountry.remove(x);
                else{
                    x++;
                }
            }
           itr = WeatherCountry.iterator();
            while (itr.hasNext()){
                try{
                    Country3.addItem(itr.next().toString());
                 }catch(NullPointerException ex){
                    JOptionPane.showMessageDialog(rootPane, "Error 900.2: Null Pointer Exceptions", "Field Error", JOptionPane.ERROR_MESSAGE);
                 }
            }
            if (Country.getItemCount() != 0){
                try {
                    //Retrieving selected Country
                    WeatherCountry = wDB.getTable(Directory, "__Countries", Country.getSelectedItem().toString());
                } catch (IOException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(rootPane, "Error 200.1: Unable to retrieve Table from Weather Database", "Database Connectivity Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            loading = false;
    }
    private void SetAddWeatherFileTab(){
        if (!loading){
            Region.setEnabled(true);
            WeatherStationName.setText(null);
            latitudeTextField.setText(null);
            //statusLabel1.setText(null);

            //Getting All the Countries we have listed
            try{
                WeatherCountry = wDB.readOneColumnFromDB(Directory, "__Countries", "CountryName", null);
            }catch(SQLException e){
                JOptionPane.showMessageDialog(rootPane, "Error 201.1: Unable to retrieve Column of Data from Weather Database", "Database Error", JOptionPane.ERROR_MESSAGE);
            } 
            Country.removeAllItems();

            itr = WeatherCountry.iterator();
            while (itr.hasNext()){
                try{
                    Country.addItem(itr.next().toString());
                 }catch(NullPointerException ex){
                    JOptionPane.showMessageDialog(rootPane, "Error 900.2: Null Pointer Exceptions", "Field Error", JOptionPane.ERROR_MESSAGE);
                 }
            }
        }
    }
    public void setDirectory(String directory){
        Directory = directory;
        
    }

}
