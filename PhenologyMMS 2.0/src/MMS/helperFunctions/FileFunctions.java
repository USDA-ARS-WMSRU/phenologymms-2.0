/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MMS.helperFunctions;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mike.herder
 */
public class FileFunctions {
    private String baseDirectory = "";
    private cropObject Crop;
    private int countRows = 0;
    private List <String> Abbrev = new ArrayList();
    private List <String> Events = new ArrayList();
    private String header[] = {};
    private int countTextRows = 0;
    private int totalRows = 0;
        
    /*
    * This returns a string of the base directory.
    * This is needed so the databases can be found easily
    */
    public String getBaseDirectory(String NameOfProgram){
        String temp[];
        
        
        String f = new File(System.getProperty("user.dir")).getParentFile().toURI().getPath();

        temp = f.split("/");
        
        boolean found = false;
        for (int x = 0; x < temp.length; x ++){
            if (found == false)
                if (temp[0].equalsIgnoreCase("PhenologyMMS 1.3")){
                    baseDirectory = baseDirectory + temp[x] + "/";
                    found = true;
                }else{
                    if (!temp[x].equals(""))
                        baseDirectory = baseDirectory + temp[x] + "/";
                }
                
                
        }
        return baseDirectory;
    }
    public boolean writeTINPUTS_DAT(cropObject crop, String path, String runType, boolean runProgram) throws FileNotFoundException, UnsupportedEncodingException{
        Crop = crop;
        boolean finished = false;
        boolean starting = false;
        
        
        if (runType.equalsIgnoreCase("B")){
            // Delete existing directory
			Path cropDir = Paths.get(path, crop.getFileName());
			try {
				deleteDirectory(cropDir);
			} catch(IOException ex) {
				Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
				return false;
			}
			
            //Running over the Rate Range
            if (crop.getRateRange().size() == 3){
                for (int x = Integer.parseInt(crop.getRateRange().get(0)); x <= Integer.parseInt(crop.getRateRange().get(1)); x += Integer.parseInt(crop.getRateRange().get(2))){
                    simulationRate(crop, path, Integer.toString(x));
                    //Running Fortran Code
                    if (runProgram && x == Integer.parseInt(crop.getRateRange().get(0)) && starting == false){
                        finished = runFortran(path, crop.getFileName(),"Rate",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, true);
                        starting = true;
                    }else if (runProgram && x == Integer.parseInt(crop.getRateRange().get(0)) && starting == true){
                        finished = runFortran(path, crop.getFileName(),"Rate",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, false);
                    }else if (runProgram)
                        finished = runFortran(path, crop.getFileName(),"Rate",crop.getCropType()+"_"+crop.getCropVariety(), runType, false, false);
                }
            }

            //Running over the Depth Range
            if(crop.getDepthRange().size() == 3){
                for (int x = Integer.parseInt(crop.getDepthRange().get(0)); x <= Integer.parseInt(crop.getDepthRange().get(1)); x += Integer.parseInt(crop.getDepthRange().get(2))){
                    simulationDepth(crop, path, Integer.toString(x));
                    //Running Fortran Code
                    if (runProgram && x == Integer.parseInt(crop.getDepthRange().get(0)) && starting == false){
                        finished = runFortran(path, crop.getFileName(), "Depth",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, true);                    
                        starting = true;
                    }else if (runProgram && x == Integer.parseInt(crop.getDepthRange().get(0))&& starting == true){
                        finished = runFortran(path, crop.getFileName(), "Depth",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, false);                    
                    }else if (runProgram)
                        finished = runFortran(path, crop.getFileName(), "Depth",crop.getCropType()+"_"+crop.getCropVariety(), runType, false, false);                    
                }
            }
            
            //Running over the Max Canopy Ht Range
            if(crop.getMaxCanopyHtRange().size() == 3){
                try{
                    for (int x = Integer.parseInt(crop.getMaxCanopyHtRange().get(0)); x <= Integer.parseInt(crop.getMaxCanopyHtRange().get(1)); x += Integer.parseInt(crop.getMaxCanopyHtRange().get(2))){
                        simulationMaxCanopyHt(crop, path, Integer.toString(x));
                        //Running Fortran Code
                        if (runProgram && x == Integer.parseInt(crop.getMaxCanopyHtRange().get(0)) && starting == false){
                            finished = runFortran(path, crop.getFileName(), "MaxCanopyHt",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, true);
                            starting = true;
                        }else if (runProgram && x == Integer.parseInt(crop.getMaxCanopyHtRange().get(0)) && starting == true){
                            finished = runFortran(path, crop.getFileName(), "MaxCanopyHt",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, false);                    
                        }else if (runProgram)
                            finished = runFortran(path, crop.getFileName(), "MaxCanopyHt",crop.getCropType()+"_"+crop.getCropVariety(), runType, false, false);                    
                    }
                }catch(Exception err){
                    System.out.println();
                }
            }
            
            //Running over the Soil Moisture Range
            if(crop.getSoilMoistureRange().size() == 3 && !crop.getSimulatePeriodMoisture()){
                int startLoop = 0;
                int endLoop = 0;
                
                if (crop.getSoilMoistureRange().get(0).equalsIgnoreCase("Optimum"))
                    startLoop = 0;
                else if (crop.getSoilMoistureRange().get(0).equalsIgnoreCase("Medium"))
                    startLoop = 1;
                else if (crop.getSoilMoistureRange().get(0).equalsIgnoreCase("Dry"))
                    startLoop = 2;
                else if (crop.getSoilMoistureRange().get(0).equalsIgnoreCase("Planted in Dust"))
                    startLoop = 3;
                
                if (crop.getSoilMoistureRange().get(1).equalsIgnoreCase("Optimum"))
                    endLoop = 0;
                else if (crop.getSoilMoistureRange().get(1).equalsIgnoreCase("Medium"))
                    endLoop = 1;
                else if (crop.getSoilMoistureRange().get(1).equalsIgnoreCase("Dry"))
                    endLoop = 2;
                else if (crop.getSoilMoistureRange().get(1).equalsIgnoreCase("Planted in Dust"))
                    endLoop = 3;
                
                for (int x = startLoop; x <= endLoop; x += Integer.parseInt(crop.getSoilMoistureRange().get(2))){
                    simulationSoilMoisture(crop, path, x);
                     //Running Fortran Code
                    if (runProgram && x == startLoop && starting == false){
                        finished = runFortran(path, crop.getFileName(), "SoilMoisture",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, true);
                        starting = true;
                    }else if (runProgram && x == startLoop && starting == true){
                        finished = runFortran(path, crop.getFileName(), "SoilMoisture",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, false);
                    }else if (runProgram)
                        finished = runFortran(path, crop.getFileName(), "SoilMoisture",crop.getCropType()+"_"+crop.getCropVariety(), runType, false, false);
                }
            }
            
            /***************************************************
            *   This is more technical.  This is a double
            *   for loop, first is for the year, and second is 
            *   for incrementing through the days.
            ****************************************************/
            if (crop.getWeatherFileRanges().size() > 0 && !crop.getSimulatePeriodMoisture()){
                /******************************************************************************
                    Used Java date options to control the loops end and start times, however,
                    I ran into a lot of incremental problems
                ***********************************************************************************/
                String location = "";

                String startDayMonth[] = null;
                String endDayMonth[] = null;
                String incrementBy = "";
                String dateToPrint[] = new String[3];
                 
                //Looping through all WeatherFile data
                for (int i = 0; i < crop.getWeatherFileRanges().size(); i++){
                    location =  crop.getWeatherFileRanges().get(i).get(2).toString().trim();                    
                    startDayMonth = crop.getWeatherFileRanges().get(i).get(7).toString().trim().split("/");
                    endDayMonth = crop.getWeatherFileRanges().get(i).get(8).toString().trim().split("/");
                    incrementBy = crop.getWeatherFileRanges().get(i).get(9).toString().trim();
                    int D = 0;

                    
                    //Incrementing through years
                    for (int Y = Integer.parseInt(crop.getWeatherFileRanges().get(i).get(4).toString().trim()); Y <= Integer.parseInt(crop.getWeatherFileRanges().get(i).get(5).toString().trim()); Y += Integer.parseInt(crop.getWeatherFileRanges().get(i).get(6).toString().trim())){
                        //Incrementing through Months
                        for (int M = Integer.parseInt(startDayMonth[0]); M <= Integer.parseInt(endDayMonth[0]); M += 1){
                            
                            if (M != Integer.parseInt(startDayMonth[0]))
                                D = 1;
                            else
                                 D = Integer.parseInt(startDayMonth[1]);
                            //Incrementing through Days
                            while(true){
                                
                                if (!checkingDayInMonth(M,D,Y) || (D > Integer.parseInt(endDayMonth[1]) && M == Integer.parseInt(endDayMonth[0])))
                                    break;
                                
                                //Put code below this
                                dateToPrint[0] = Integer.toString(D);
                                dateToPrint[1] = Integer.toString(M);
                                dateToPrint[2] = Integer.toString(Y);
//                                System.out.println(Integer.toString(M) + "/" + Integer.toString(D) + "/" + Integer.toString(Y));
                                simulationDate(crop, path, location, dateToPrint);
                                //Running Fortran Code
                                if (runProgram && (D == Integer.parseInt(startDayMonth[1])) && M == Integer.parseInt(startDayMonth[0]) && Y == Integer.parseInt(crop.getWeatherFileRanges().get(i).get(4).toString().trim()) && starting == false){
                                    finished = runFortran(path, crop.getFileName(), "SimulationPeriod",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, true);
                                    starting = true;
                                }else if (runProgram && (D == Integer.parseInt(startDayMonth[1])) && M == Integer.parseInt(startDayMonth[0]) && Y == Integer.parseInt(crop.getWeatherFileRanges().get(i).get(4).toString().trim()) && starting == true){
                                    finished = runFortran(path, crop.getFileName(), "SimulationPeriod",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, false);
                                }else if (runProgram)
                                    finished = runFortran(path, crop.getFileName(), "SimulationPeriod",crop.getCropType()+"_"+crop.getCropVariety(), runType, false, false);                                
                                D += Integer.parseInt(incrementBy);
                            }       
                        }

                    }
                }
                FinishFile(path, crop.getFileName());
            }
            if (crop.getSimulatePeriodMoisture()){
                int startLoop = 0;
                int endLoop = 0;
                
                if (crop.getSoilMoistureRange().get(0).equalsIgnoreCase("Optimum"))
                    startLoop = 0;
                else if (crop.getSoilMoistureRange().get(0).equalsIgnoreCase("Medium"))
                    startLoop = 1;
                else if (crop.getSoilMoistureRange().get(0).equalsIgnoreCase("Dry"))
                    startLoop = 2;
                else if (crop.getSoilMoistureRange().get(0).equalsIgnoreCase("Planted in Dust"))
                    startLoop = 3;
                
                if (crop.getSoilMoistureRange().get(1).equalsIgnoreCase("Optimum"))
                    endLoop = 0;
                else if (crop.getSoilMoistureRange().get(1).equalsIgnoreCase("Medium"))
                    endLoop = 1;
                else if (crop.getSoilMoistureRange().get(1).equalsIgnoreCase("Dry"))
                    endLoop = 2;
                else if (crop.getSoilMoistureRange().get(1).equalsIgnoreCase("Planted in Dust"))
                    endLoop = 3;
                //Looping through Soil Moisture Types
                for (int j = 0; j <= endLoop; j += Integer.parseInt(crop.getSoilMoistureRange().get(2))){
                    //Looping through all WeatherFile data
                    for (int i = 0; i < crop.getWeatherFileRanges().size(); i++)
                        //Incrementing through years
                        for (int f = Integer.parseInt(crop.getWeatherFileRanges().get(i).get(4).toString().trim()); f <= Integer.parseInt(crop.getWeatherFileRanges().get(i).get(5).toString().trim()); f += Integer.parseInt(crop.getWeatherFileRanges().get(i).get(6).toString().trim())){
                            String location =  crop.getWeatherFileRanges().get(i).get(2).toString().trim();
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                            Calendar start = Calendar.getInstance();
                            java.util.Date startDate = null;
//                            String location = "\"" + crop.getWeatherFileRanges().get(i).get(0).toString() + "/" + crop.getWeatherFileRanges().get(i).get(1).toString().trim() + "/" + crop.getWeatherFileRanges().get(i).get(2).toString().trim() + "\"";
                            
                            try {
                                startDate = sdf.parse(crop.getWeatherFileRanges().get(i).get(7)+ "/" + Integer.toString(f));
                            } catch (ParseException ex) {
                                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            start.setTime(startDate);
                            Calendar end = Calendar.getInstance();
                            java.util.Date endDate = null;
                            try {
                                endDate = sdf.parse(crop.getWeatherFileRanges().get(i).get(8)+ "/" + Integer.toString(f));
                            } catch (ParseException ex) {
                                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            end.setTime(endDate);
                            //Incrementing through days.
                            for (java.util.Date date = start.getTime(); !start.after(end); start.add(Calendar.DATE, 1)){
                                simulatePeriodMoisture(crop, path, j, location, start);
                                //Running Fortran Code
                                if (runProgram && f == Integer.parseInt(crop.getWeatherFileRanges().get(i).get(4).toString().trim()) && starting == false){
                                    finished = runFortran(path, crop.getFileName(), "SimulationPeriod",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, true);
                                    starting = true;
                                }else if (runProgram && f == Integer.parseInt(crop.getWeatherFileRanges().get(i).get(4).toString().trim()) && starting == true){
                                    finished = runFortran(path, crop.getFileName(), "SimulationPeriod",crop.getCropType()+"_"+crop.getCropVariety(), runType, true, false);
                                }else if (runProgram)
                                    finished = runFortran(path, crop.getFileName(), "SimulationPeriod",crop.getCropType()+"_"+crop.getCropVariety(), runType, false, false);
                            }
                        }
                    
                }
            }

        }else{

            //createResultsFile(path+"\\Interface\\results", Crop.getFileName(), Crop.getCropType(), "Rate", true);

            singleBatchRun(crop, path);
            //Running Fortran Code
            if (runProgram)
                finished = runFortran(path, crop.getFileName(), null,crop.getCropType()+"_"+crop.getCropVariety(), runType, true, true);
        }
        
  
        return finished;
    }
        
    private boolean checkingDayInMonth(int Month, int Day, int year){
        boolean validDay = false;
        
        switch(Month){
            case 1:
                    if (Day <= 31)
                        validDay =  true;
                    else
                        validDay =  false;
                break;
            case 2: 
                    if ((year%4 == 0) && year%100 != 0){
                        if (Day <= 29)
                            validDay =  true;
                    }else if ((year%4 == 0) && (year%100 == 0) && (year%400 == 0)){
                        if (Day <= 29)
                            validDay =  true;
                    }else if (Day <= 28){
                        validDay =  true;
                    }else{
                        validDay = false;
                    }
                break;    
            case 3: 
                    if (Day <= 31)
                        validDay =  true;
                    else
                        validDay =  false;
                break;
            case 4: 
                    if (Day <= 30)
                        validDay =  true;
                    else
                        validDay =  false;
                break;
            case 5: 
                    if (Day <= 31)
                        validDay =  true;
                    else
                        validDay =  false;
                break;
            case 6: 
                    if (Day <= 30)
                        validDay =  true;
                    else
                        validDay =  false;
                break;
            case 7: 
                    if (Day <= 31)
                        validDay =  true;
                    else
                        validDay =  false;
                break;
            case 8: 
                    if (Day <= 31)
                        validDay =  true;
                    else
                        validDay =  false;
                break;
            case 9: 
                    if (Day <= 30)
                        validDay =  true;
                    else
                        validDay =  false;
                break;
            case 10: 
                    if (Day <= 31)
                        validDay =  true;
                    else
                        validDay =  false;
                break;
            case 11: 
                    if (Day <= 30)
                        validDay =  true;
                    else
                        validDay =  false;
                break;
            case 12: 
                    if (Day <= 31)
                        validDay =  true;
                    else
                        validDay =  false;
                break;
        }
        return validDay;
    }
    private int getNumberOfDaysInMonth(int Month, int year){
        int numberDays = 0;
        switch(Month){
            case 1:
                    numberDays = 31;
                break;
            case 2: 
                    if ((year%4 == 0) && year%100 != 0)
                        numberDays = 29;
                    else if ((year%4 == 0) && (year%100 == 0) && (year%400 == 0))
                        numberDays = 29;
                    else
                        numberDays = 28;
                break;
            case 3: 
                    numberDays = 31;
                break;
            case 4: 
                    numberDays = 30;
                break;
            case 5: 
                    numberDays = 31;
                break;
            case 6: 
                    numberDays = 30;
                break;
            case 7: 
                    numberDays = 31;
                break;
            case 8:
                    numberDays = 31;
                break;
            case 9: 
                    numberDays = 30;
                break;
            case 10: 
                    numberDays = 31;
                break;
            case 11: 
                    numberDays = 30;
                break;
            case 12: 
                    numberDays = 31;
                break;
            
        }
        return numberDays;
        
    }
    private boolean runFortran(String Location, String FolderName, String Method, String cropType, String RunType, boolean resetCounter, boolean Start){
        String location = Location+ "/Interface/";
        String folderPath =  Location + "/Interface/results/";
        boolean programFinished = false;

        int r = 10;
        //String f = location ;
        File f = new File(getAppPath());
        
        String line;
        
        OutputStream stdIn = null;
        InputStream stdErr = null;
        InputStream stdOut = null;
        
//        try {
            // process tells it to execute the fortran program
            // bcv added
            // JOptionPane is used to display a message box to show the path
            location = location + "PhenologyMMS.exe";
            //JOptionPane.showMessageDialog(null,fl1,"\"\"",JOptionPane.WARNING_MESSAGE);
            String fl1 = "\""+ getAppPath() + "\\PhenologyMMS.exe";
//               System.out.println("path: " + fl1);
             //JOptionPane.showMessageDialog(null,fl1,"\"\"",JOptionPane.WARNING_MESSAGE);

             Process p = null;

             try {
                p = Runtime.getRuntime().exec(fl1, null, f);
                Thread err = new Thread(new StreamGobbler(p.getErrorStream()));
                Thread out = new Thread(new StreamGobbler(p.getInputStream()));
                err.start();
                out.start();
            } catch (IOException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }

             // Ian added to drain the buffer.  This was causing the program
             // to not be able to return from the execution of the Fortran
             // program.               
                try {
                    r = p.waitFor();
                } catch (InterruptedException ex) {
                    Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (r == 0) 
                    programFinished = true;
                else
                    programFinished = false;
                
             

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
//        }

             System.out.println("r = " + r); //if r = 0 it completed successfully
//        }catch(InterruptedException ie){
//            // this is left blank - no exception to be caught
//            JOptionPane.showMessageDialog(null,"interrupted exception","",JOptionPane.WARNING_MESSAGE);  
//        }
//        catch (IOException i){
//            i.printStackTrace();
//            JOptionPane.showMessageDialog(null,"IOexception","",JOptionPane.WARNING_MESSAGE); 
//        }
                
        if (r == 0){
            if (RunType.equalsIgnoreCase("B")){
                copyPhenolOutFile(folderPath, FolderName, cropType, Method);
                copyTinputsFile(folderPath, FolderName, cropType, Method);
                createResultsFile(folderPath, FolderName, cropType, Method, resetCounter, Start);
            }else{
                try {
                    combineInOutFile(folderPath, FolderName);
                } catch (Exception ex) {
                    Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return programFinished;
    }
    public void copyPhenolOutFile(String folderPath, String FolderName,String cropType, String Method){
        Path dest = null;
        Path source = Paths.get(folderPath + "phenol.out");
        
        File newDir = new File(folderPath + FolderName + "/" + "outputs");
                
//      Path p1 = Paths.get(location + "results/temp");
        if (!newDir.exists()){
            try{
                newDir.mkdirs();
            }catch(SecurityException ex){
                System.out.println(ex.toString());
            }
        }else{
//             JOptionPane.showMessageDialog(null,"Error: ###.#, This directory already exists","Directory Error",JOptionPane.WARNING_MESSAGE);  
        }



        for (int x = 0; true; x++){

            dest = Paths.get(newDir.getAbsolutePath()+ "/" + cropType + "_" + Method + "_" + x + ".out");
            try {
                if (!Files.exists(dest)){
//                    System.out.println("Source: " + source);
//                    System.out.println("Destination: " + dest);
                    Files.copy(source, dest);
                    break;
                }
            } catch (IOException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void copyTinputsFile(String folderPath, String FolderName,String cropType, String Method){
        Path dest = null;
        Path source = Paths.get(folderPath).getParent();
        source = Paths.get(source.toString() + "\\" + "tinputs.dat");
        
        File newDir = new File(folderPath + FolderName + "/" + "inputs");
                

        if (!newDir.exists()){
            try{
                newDir.mkdirs();
            }catch(SecurityException ex){
                System.out.println(ex.toString());
            }
        }else{
//             JOptionPane.showMessageDialog(null,"Error: ###.#, This directory already exists","Directory Error",JOptionPane.WARNING_MESSAGE);  
        }
        for (int x = 0; true; x++){

            dest = Paths.get(newDir.getAbsolutePath() + "/" + cropType + "_" + Method + "_" + x + ".in");

            try {
                if (!Files.exists(dest)){
//                    System.out.println("Source: " + source);
//                    System.out.println("Destination: " + dest);
                        
                    Files.copy(source, dest);
                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void createResultsFile(String folderPath, String FolderName, String cropType, String Method, boolean ResetRowCounter, boolean justStarted){

        //Path Variables
        Path inDest = Paths.get(folderPath + "\\" + FolderName);
        Path outDest = Paths.get(folderPath + "\\" + FolderName);
        Path outSource = Paths.get(folderPath + "\\phenol.out");
        Path inSource = Paths.get(folderPath).getParent();
        inSource = Paths.get(inSource.toString() + "\\" + "tinputs.dat");

        //File Writer Variables
        PrintWriter out = null;
        BufferedWriter bw = null;
        FileWriter fw = null;
        
        boolean inputMade = false;
        
        //Used for creating Directories
        File newDir = new File(inDest.toString());
        if (!newDir.exists()){
            try{
                newDir.mkdirs();
            }catch(SecurityException ex){
                System.out.println(ex.toString());
            }
        }
        
        //Creating File handlers
        File Input = new File(inDest.toString() + "\\input.tab");
        //Creating Temp Files to make 1 result file
        if (!Input.exists() || justStarted){
            try {
                Files.deleteIfExists(Input.toPath());
                Input.createNewFile();
              } catch (IOException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Writing to file
        if(Input.exists() ){
            try {
                
                fw = new FileWriter(Input.toString(), true);
                bw = new BufferedWriter(fw);
                out = new PrintWriter(bw);
                BufferedReader br = null;
                boolean start = false;
                String table [][] = null;
                
                
                
                String row[] ={};
                



                //Getting the number of Columns and number of Rows
                br = new BufferedReader(new FileReader(outSource.toString()));
                countTextRows = 0;
                for(String line; (line = br.readLine()) != null; ) {
                    if (line.contains("Phenological Event") == true){
                        header = line.trim().split("\\s\\s+");
                        start = true;
                    }else if (start){
                        if (!line.isEmpty()){
                            countTextRows++;
                        }
                    }
                }
//                totalRows = countTextRows;
                //Creating the appropriate size table for action performed
                if (ResetRowCounter)
                    countRows = 0;
                if (justStarted){
                    countRows = 0;
                    table = new String[2][header.length * countTextRows];
                    //Adding t-inputs headers to table
                    table[0][0] = "Run #";
                    table[1][0] = "0";
                    table[0][1] = "Crop Type";
                    table[1][1] = "";
                    table[0][2] = "Crop Variety";
                    table[1][2] = "";
                    table[0][3] = "Run Type";
                    table[1][3] = "";
                    table[0][4] = "Location";
                    table[1][4] = ""; 
                    table[0][5] = "Year";
                    table[1][5] = ""; 
                    
                }else{
                    table = new String[1][header.length * countTextRows];
                    table[0][0] = Integer.toString(countRows);
                }

                try{
                    //Adding Phenol.out to headers
                    br = new BufferedReader(new FileReader(outSource.toString()));
                    start = false;
                    int abbrevRow = 0;
                    int countColumns = 5;
                    for(String line; (line = br.readLine()) != null; ) {
                        if (line.contains("Phenological Event") == true){
                            start = true;
                        }else if (start){
                            
                            row = line.trim().split("\\s\\s+");     //Removes all the un-necessary white space
                            
                            //taking the name and making abbreviations
                            if (row[0].contains(" ")){
                                String abbreviation = "";
                                String firstAbbrev = Character.toString(row[0].split(" ")[0].charAt(0));
                                String secondAbbrev = Character.toString(row[0].split(" ")[1].charAt(0));
                                if (secondAbbrev.contains("("))
                                    abbreviation = firstAbbrev;
                                else
                                    abbreviation = firstAbbrev + secondAbbrev;
                                
                                Abbrev.add(abbreviation);
                                Events.add(row[0]);
                            }else if (row[0].contains("Canopy Height")){
                                String abbreviation = "Canopy Ht";
                                Abbrev.add(abbreviation);
                                Events.add(row[0]);
                             
                            }else{
                                String abbreviation = Character.toString(row[0].charAt(0));
                                Abbrev.add(abbreviation);
                                Events.add(row[0]);
                            }
                            //Adding Headers & data
                            for (int i = 1; i < row.length; i++){       //Loops through the columns for 1 row.
                                 if (IsValidNumber(row[i]) != 0 && i != 2 ){
                                    if (IsValidNumber(row[i]) == 1){
                                        if (Integer.parseInt(row[i]) > 0 || row[i].length() > 3){
                                            countColumns++;     //increment column count
                                            
                                            if (justStarted){
                                                //Add To Header to table
                                                table[0][countColumns] = Abbrev.get(abbrevRow) + "-" + header[i];

                                                //Add Data to table
                                                table[table.length-1][countColumns] = row[i];
                                            }else
                                                table[0][countColumns] = row[i];
                                        }
                                    }else if (IsValidNumber(row[i]) == 2){
                                        if (Double.parseDouble(row[i]) > 0 || row[i].length() > 3){
                                            countColumns++;     //increment column count
                                            
                                            if (justStarted){
                                                //Add Header to table
                                                table[0][countColumns] = Abbrev.get(abbrevRow)+ "-" + header[i];

                                                //Add Data to table
                                                table[table.length-1][countColumns] = row[i];
                                            }else{
                                                table[0][countColumns] = row[i];
                                            }
                                        }
                                    }
                                }else if (row[i].length() > 3 && row[i].contains("/")){
                                    countColumns++;     //increment column count

                                    if (justStarted){
                                        //Add To Header to table
                                        table[0][countColumns] = Abbrev.get(abbrevRow) +  "-" + header[i];

                                        //Add Data to table
                                        table[table.length-1][countColumns] = row[i];
                                    }else{
                                        table[0][countColumns] = row[i];
                                    }
                                }
                            }
                           abbrevRow++; 
                        }
                    }
                    //Getting Input Data
                    br = new BufferedReader(new FileReader(inSource.toString()));
                     int cCount = 1;
                    for (String line; (line = br.readLine()) != null;){
                        if (cCount == 1){
                            if (justStarted)
                                table[1][1] = line;                             //Crop Type
                            else
                                table[0][1] = line;
                        }else if (cCount ==2){
                            if (justStarted)
                                table[1][2] = line;                            //Crop Variety
                            else 
                                table[0][2] = line;
                        }else if (cCount ==3){
                            if (justStarted)
                                table[1][4] = line;                            //Crop Location
                            else
                                table[0][4] = line;
                        }else if (cCount ==6){
                            if (justStarted)
                                table[1][5] = line;                            //Crop Year
                            else
                                table[0][5] = line;
                            break;
                        }
                        cCount ++;
                    }
                    if (justStarted)
                        table[1][3] = Method;                            //Crop Location
                    else
                        table[0][3] = Method;
                    
                    
                    //Writing Data to file
                    for (int i = 0; i < table.length; i++){
                        for (int j = 0; j < table[i].length; j++){
                            if (j == 0)
                                fw.write(String.format("%-7s\t",table[i][j]));
                            else if (j == 1 || j == 2)
                                fw.write(String.format("%-30s\t",table[i][j]));
                            else if (j == 3)
                                fw.write(String.format("%-20s\t",table[i][j]));
                            else if (j == 4)
                                fw.write(String.format("%-50s\t",table[i][j]));
                             else if (j == 5)
                                fw.write(String.format("%-20s\t",table[i][j]));
                            else
                                fw.write(String.format("%-15s\t",table[i][j]));
                        }
                        fw.write(System.lineSeparator());
                    }
                    
                    start = false;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }

            out.close();
            countRows ++;
        }
    }
    private void FinishFile(String folderPath, String folderName ){
        //File Writer Variables
        PrintWriter out = null;
        BufferedWriter bw = null;
        FileWriter fw = null;
        
        //Paths
        Path inDest = Paths.get(folderPath + "Interface\\results\\" + folderName);
        File Input = new File(inDest.toString() + "\\input.tab");
        
        try {
            fw = new FileWriter(Input.toString(), true);
        } catch (IOException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);
        out.write("\n\n\n");
        for (int x = 0; x < countTextRows; x++){
            out.write(String.format("%-7s\t",Events.get(x) + " = " + Abbrev.get(x) + "\n"));
        }
        Events.clear();
        Abbrev.clear();
        out.close();
    }
    private int IsValidNumber(String var){
        int validNumber = 1;
             
        try{
            Integer.parseInt(var);
        }catch(NumberFormatException e){
            validNumber = 0;
        }
        try{
            Double.parseDouble(var);
            validNumber = 2;
        }catch(NumberFormatException e){
            validNumber = 0;
        }
        
        return validNumber;
    }
    public void combineInOutFile(String folderPath, String FolderName) throws IOException{
        //Path Variables
        Path inDest = Paths.get(folderPath + "\\" + FolderName);
        Path outDest = Paths.get(folderPath + "\\" + FolderName);
        Path outSource = Paths.get(folderPath + "phenol.out");
        Path inSource = Paths.get(folderPath).getParent();
        inSource = Paths.get(inSource.toString() + "\\" + "tinputs.dat");
        
        //File Writer Variables
        PrintWriter out = null;
        BufferedWriter bw = null;
        FileWriter fw = null;
        
        boolean inputMade = false;
        boolean outputMade = false;
        
        //Used for creating Directories
        File newDir = new File(folderPath + FolderName);
        
        //Creating File handlers
        File Input = new File(inDest.toString() + "\\results.info");

        //Creating Directory if it Doesnt exist
        if (!newDir.exists()){
            try{
                newDir.mkdirs();
            }catch(SecurityException ex){
                System.out.println(ex.toString());
            }
        }
        
        //Creating Results File if it doesn't exist
        if (!Input.exists()){
            try {
                inputMade = Input.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            if (Input.exists()){
                Input.delete();
                inputMade = Input.createNewFile();
            }
        }
        
        //Add T-Input.dat data to results file
        if (inputMade){
            try {
                fw = new FileWriter(Input.toString(), true);
            } catch (IOException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
                        out.println("These are the inputs provided to execute PhenologyMMS:\n");
            out.println(String.format("%-40s: %s","Crop", Crop.getCropType()));
            out.println(String.format("%-40s: %s","Variety", Crop.getCropVariety()));
            out.println(String.format("%-40s: %s","Location/ WeatherFiles",Crop. getLocationOfWeatherFile()));
            out.println(String.format("%-40s: %s","Planting Date Month (mm)", Crop.getDate().split("/")[0]));
            out.println(String.format("%-40s: %s","Planting Date Day (dd)", Crop.getDate().split("/")[1]));
            out.println(String.format("%-40s: %s","Planting Date Year (yyyy)",Crop.getDate().split("/")[2]));
            
            //Creating Day of Year

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(Crop.getDate().split("/")[1]));

            cal.set(Calendar.YEAR, Integer.parseInt(Crop.getDate().split("/")[2]));
            int daysInYear = 0;
            for (int x = 0; x < 12; x++){
                if (x < Integer.parseInt(Crop.getDate().split("/")[0])-1){
                    cal.set(Calendar.MONTH, x);
                    int ActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    daysInYear = daysInYear + ActualMax;
                }else if (x == Integer.parseInt(Crop.getDate().split("/")[0])-1){
                    daysInYear = daysInYear + Integer.parseInt(Crop.getDate().split("/")[1]);
                }
            }
            out.println(String.format("%-40s: %s","Day of Year",Integer.toString(daysInYear)));
            out.println(String.format("%-40s: %s","Planting Depth (cm)", Crop.getDepth()));
            out.println(String.format("%-40s: %s","Planting Rate (plants/m^2)", Crop.getRate()));
            out.println(String.format("%-40s: %s","Initial Soil Moisture Conditions", Crop.getSoilMoisture()));
            out.println(String.format("%-40s: %s","Latitude (degrees)", Crop.getLatitude()));
            out.println(String.format("%-40s: %s","GDD Method", Crop.getGddMethod()));
            out.println(String.format("%-40s: %s","Base Temperature (C)", Crop.getBaseTemp()));
            out.println(String.format("%-40s: %s","Lower Optimum Temperature (C)",  Crop.getLowerOptimumTemp()));
            out.println(String.format("%-40s: %s","Upper Optimum Temperature (C)",  Crop.getUpperOptimumTemp()));
            out.println(String.format("%-40s: %s","Upper/Maximum Temperature (C)", Crop.getMaxTemp()));
            out.println(String.format("%-40s: %s","Maximum Canopy Height (cm)", Crop.getCanopyHt()));
            out.println(String.format("%-40s: %s","Phyllochron Value", Crop.getPhyllochron()));
            out.println("-------------------------------------------------------------------");

            int z = Crop.getGrowthStagesData().size() / 4;
            int count = 0;
            for (int i = 0; i < z; i++){
                    String header = Crop.getGrowthStagesHeaders().get(i).toString().split("_")[2];
                count++;
                if (i == 0){

                    if (Crop.getGrowthStageSelection().get(0))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(16))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(32))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(48))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 1){

                    if (Crop.getGrowthStageSelection().get(1))
                       out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(17))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(33))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(49))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 2){

                    if (Crop.getGrowthStageSelection().get(2))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(18))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(34))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(50))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 3){

                    if (Crop.getGrowthStageSelection().get(3))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(19))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(35))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(51))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 4){

                    if (Crop.getGrowthStageSelection().get(4))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(20))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(36))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(52))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 5){

                    if (Crop.getGrowthStageSelection().get(5))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(21))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(37))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(53))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 6){

                    if (Crop.getGrowthStageSelection().get(6))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(22))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(38))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(54))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 7){

                    if (Crop.getGrowthStageSelection().get(7))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(23))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(39))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(55))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 8){

                    if (Crop.getGrowthStageSelection().get(8))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(24))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(40))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(56))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }

                else if (i == 9){

                    if (Crop.getGrowthStageSelection().get(9))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(25))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(41))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(57))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }            
                else if (i == 10){

                    if (Crop.getGrowthStageSelection().get(10))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(26))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(42))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(58))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }            
                else if (i == 11){

                    if (Crop.getGrowthStageSelection().get(11))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(27))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(43))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(59))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }

                else if (i == 12){

                    if (Crop.getGrowthStageSelection().get(12))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(28))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(44))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(60))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 13){

                    if (Crop.getGrowthStageSelection().get(13))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(29))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(45))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(61))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 14){

                    if (Crop.getGrowthStageSelection().get(14))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(30))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(46))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(62))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 15){

                    if (Crop.getGrowthStageSelection().get(15))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(31))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(47))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(63))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 16){

                    if (Crop.getGrowthStageSelection().get(16))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(32))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(48))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(64))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 17){

                    if (Crop.getGrowthStageSelection().get(17))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(33))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(49))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(65))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 18){

                    if (Crop.getGrowthStageSelection().get(18))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(34))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(50))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(66))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
                else if (i == 19){

                    if (Crop.getGrowthStageSelection().get(19))
                        out.println(String.format("%-40s= %s", header, "GN " + Crop.getGrowthStagesData().get(i)));
                    else if (Crop.getGrowthStageSelection().get(35))
                        out.println(String.format("%-40s= %s", header, "GS " + Crop.getGrowthStagesData().get(i+z)));
                    else if (Crop.getGrowthStageSelection().get(51))
                        out.println(String.format("%-40s= %s", header, "LN " + Crop.getGrowthStagesData().get(i+(z*2))));
                    else if (Crop.getGrowthStageSelection().get(68))
                        out.println(String.format("%-40s= %s", header, "LS " + Crop.getGrowthStagesData().get(i+(z*3))));
                }
            }
            out.println("-------------------------------------------------------------------");
            out.println("-------------------------------------------------------------------");

            //Copy the output results below this.
            try{
                BufferedReader br = new BufferedReader(new FileReader(outSource.toString()));
                for(String line; (line = br.readLine()) != null; ) {
                    out.println(line);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.close();
        }        
    }
    
    public String getAppPath(){
       String tmp, c1;
       tmp = "";
       int indx, mm;
                try {                        
                    URL url = new URL("jar", "", -1, "file:" + new File("").getAbsolutePath());
                    tmp = url.toString();
                    c1 = url.getPath();     
                    indx = c1.indexOf(":");
                    mm = c1.length();            
                    tmp = c1.substring(indx + 1, mm);  
                   // System.out.println("tmp = " +tmp);                   
                 }
                 catch (Exception e) {
                   System.out.println("IOException: URL");
                 }
       return tmp;       
    }
    static class StreamGobbler implements Runnable {
        BufferedReader reader;
        public StreamGobbler(InputStream in) throws IOException {
            this.reader = new BufferedReader(new InputStreamReader(in));
        }
        
        public void run() {
            try {
                String line = null;
                while ( (line = reader.readLine()) != null) {
                    ;//do nothing
                }
            } catch (IOException ioe) {
                System.out.println("error gobbling stream");
                ioe.printStackTrace();
            }
        }
        
    }
    public void singleBatchRun(cropObject crop, String path) throws FileNotFoundException, UnsupportedEncodingException{
        String date[];
        
        String location = path + "/Interface/tinputs.dat";
        
        File file = new File(location);
        
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        String info = "";
        if (crop.getCropType().contains("Types"))
            info = crop.getCropType().substring(0, crop.getCropType().indexOf("Types"));
        else
            info = crop.getCropType();
                
        writer.println(info);
        writer.println(crop.getCropVariety());
//        writer.println(crop. getLocationOfWeatherFile());
        writer.println("\"" +crop. getLocationOfWeatherFile() + "\"");
        date = crop.getDate().split("/");
        writer.println(date[0]);
        writer.println(date[1]);
        writer.println(date[2]);
        
        //Creating Day of Year

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[1]));
        
        cal.set(Calendar.YEAR, Integer.parseInt(date[2]));
        int daysInYear = 0;
        for (int x = 0; x < 12; x++){
            if (x < Integer.parseInt(date[0])-1){
                cal.set(Calendar.MONTH, x);
                int ActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                daysInYear = daysInYear + ActualMax;
            }else if (x == Integer.parseInt(date[0])-1){
                daysInYear = daysInYear + Integer.parseInt(date[1]);
            }
        }
        writer.println(Integer.toString(daysInYear));
        writer.println(crop.getDepth());
        writer.println(crop.getRate());
        writer.println(crop.getSoilMoisture());
        
        //Read Database and get SoilMoisture numbers EMERGENCE DATA
        
        for (int i = 0; i < 4; i ++){
            if (i == 0){
                writer.println("Optimum");
            }else if (i == 1){
                writer.println("Medium");
            }else if (i == 2){
                writer.println("Dry");
            }else if (i == 3){
                writer.println("Planted in dust");
            }
        
            if (i == 0){
                for (int x = 0; x < crop.getOptimum().size(); x++)
                    writer.println(crop.getOptimum().get(x));

            }else if (i ==1){
                for (int x = 0; x < crop.getMedium().size(); x++)
                    writer.println(crop.getMedium().get(x));

            }else if (i == 2){
                for (int x = 0; x < crop.getDry().size(); x++)
                    writer.println(crop.getDry().get(x));

            }else if (i == 3){
                for (int x = 0; x < crop.getPID().size(); x++)
                    writer.println(crop.getPID().get(x));
            }
        }
        
        writer.println(crop.getLatitude());
        writer.println(crop.getGddMethod());
        writer.println(crop.getBaseTemp());
        writer.println(crop.getLowerOptimumTemp());
        writer.println(crop.getUpperOptimumTemp());
        writer.println(crop.getMaxTemp());
        writer.println(crop.getGDD_PER_LEAF());
        writer.println(crop.getMaxCanopyHt());
        
        //Writer Vernal Data for printout
        for (int i = 0; i < crop.getVernalTypes().size(); i++){
            writer.println(crop.getVernalTypes().get(i));
        }
        writer.println(crop.getCanopyHt());  
        
        int z = crop.getGrowthStagesData().size() / 4;
        int count = 0;
        for (int i = 0; i < z; i++){
            count++;
            if (i == 0){
                if (crop.getGrowthStageSelection().get(0))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(16))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 1){
                if (crop.getGrowthStageSelection().get(1))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(17))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 2){
                if (crop.getGrowthStageSelection().get(2))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(18))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 3){
                if (crop.getGrowthStageSelection().get(3))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(19))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 4){
                if (crop.getGrowthStageSelection().get(4))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(20))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(36))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(52))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 5){
                if (crop.getGrowthStageSelection().get(5))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(21))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(37))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(53))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 6){
                if (crop.getGrowthStageSelection().get(6))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(22))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(38))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(54))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 7){
                if (crop.getGrowthStageSelection().get(7))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(23))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(39))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(55))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 8){
                if (crop.getGrowthStageSelection().get(8))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(24))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(40))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(56))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

            else if (i == 9){
                if (crop.getGrowthStageSelection().get(9))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(25))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(41))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(57))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 10){
                if (crop.getGrowthStageSelection().get(10))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(26))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(42))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(58))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 11){
                if (crop.getGrowthStageSelection().get(11))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(27))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(43))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(59))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            
            else if (i == 12){
                if (crop.getGrowthStageSelection().get(12))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(28))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(44))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(60))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 13){
                if (crop.getGrowthStageSelection().get(13))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(29))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(45))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(61))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 14){
                if (crop.getGrowthStageSelection().get(14))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(30))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(46))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(62))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 15){
                if (crop.getGrowthStageSelection().get(15))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(31))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(47))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(63))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 16){
                if (crop.getGrowthStageSelection().get(16))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(64))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 17){
                if (crop.getGrowthStageSelection().get(17))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(65))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 18){
                if (crop.getGrowthStageSelection().get(18))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(66))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 19){
                if (crop.getGrowthStageSelection().get(19))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(68))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

        }
        for (int i = 0; i < (16-count-1); i++)
            writer.println("GN 0");
       

        writer.close();
    }

    public void simulationRate(cropObject crop, String path, String value) {
        String date[];
        
        String location = path + "/Interface/tinputs.dat";
        
        File file = new File(location);
        
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter(file, "UTF-8");
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String info = "";
        if (crop.getCropType().contains("Types"))
            info = crop.getCropType().substring(0, crop.getCropType().indexOf("Types"));
        else
            info = crop.getCropType();
                
        writer.println(info);
        writer.println(crop.getCropVariety());
        writer.println("\"" + crop. getLocationOfWeatherFile() + "\"");
        date = crop.getDate().split("/");
        writer.println(date[0]);
        writer.println(date[1]);
        writer.println(date[2]);
        
        //Creating Day of Year

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[1]));
        
        cal.set(Calendar.YEAR, Integer.parseInt(date[2]));
        int daysInYear = 0;
        for (int x = 0; x < 12; x++){
            if (x < Integer.parseInt(date[0])-1){
                cal.set(Calendar.MONTH, x);
                int ActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                daysInYear = daysInYear + ActualMax;
            }else if (x == Integer.parseInt(date[0])-1){
                daysInYear = daysInYear + Integer.parseInt(date[1]);
            }
        }
        writer.println(Integer.toString(daysInYear));
        writer.println(crop.getDepth());
        writer.println(value);
        writer.println(crop.getSoilMoisture());
        
        //Read Database and get SoilMoisture numbers EMERGENCE DATA
        
        for (int i = 0; i < 4; i ++){
             if (i == 0){
                writer.println("Optimum");
            }else if (i == 1){
                writer.println("Medium");
            }else if (i == 2){
                writer.println("Dry");
            }else if (i == 3){
                writer.println("Planted in dust");
            }
        
            if (i == 0){
                for (int x = 0; x < crop.getOptimum().size(); x++)
                    writer.println(crop.getOptimum().get(x));

            }else if (i ==1){
                for (int x = 0; x < crop.getMedium().size(); x++)
                    writer.println(crop.getMedium().get(x));

            }else if (i == 2){
                for (int x = 0; x < crop.getDry().size(); x++)
                    writer.println(crop.getDry().get(x));

            }else if (i == 3){
                for (int x = 0; x < crop.getPID().size(); x++)
                    writer.println(crop.getPID().get(x));
            }
        }
        
        writer.println(crop.getLatitude());
        writer.println(crop.getGddMethod());
        writer.println(crop.getBaseTemp());
        writer.println(crop.getLowerOptimumTemp());
        writer.println(crop.getUpperOptimumTemp());
        writer.println(crop.getMaxTemp());
        writer.println(crop.getGDD_PER_LEAF());
        writer.println(crop.getMaxCanopyHt());
        
        //Writer Vernal Data for printout
        for (int i = 0; i < crop.getVernalTypes().size(); i++){
            writer.println(crop.getVernalTypes().get(i));
        }
        writer.println(crop.getCanopyHt());  //Hardcoded the Canopy Ht. for right now
        
        int z = crop.getGrowthStagesData().size() / 4;
        int count = 0;
        for (int i = 0; i < z; i++){
            count++;
            if (i == 0){
                if (crop.getGrowthStageSelection().get(0))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(16))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 1){
                if (crop.getGrowthStageSelection().get(1))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(17))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 2){
                if (crop.getGrowthStageSelection().get(2))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(18))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 3){
                if (crop.getGrowthStageSelection().get(3))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(19))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 4){
                if (crop.getGrowthStageSelection().get(4))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(20))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(36))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(52))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 5){
                if (crop.getGrowthStageSelection().get(5))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(21))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(37))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(53))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 6){
                if (crop.getGrowthStageSelection().get(6))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(22))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(38))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(54))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 7){
                if (crop.getGrowthStageSelection().get(7))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(23))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(39))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(55))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 8){
                if (crop.getGrowthStageSelection().get(8))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(24))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(40))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(56))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

            else if (i == 9){
                if (crop.getGrowthStageSelection().get(9))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(25))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(41))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(57))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 10){
                if (crop.getGrowthStageSelection().get(10))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(26))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(42))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(58))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 11){
                if (crop.getGrowthStageSelection().get(11))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(27))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(43))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(59))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            
            else if (i == 12){
                if (crop.getGrowthStageSelection().get(12))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(28))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(44))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(60))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 13){
                if (crop.getGrowthStageSelection().get(13))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(29))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(45))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(61))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 14){
                if (crop.getGrowthStageSelection().get(14))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(30))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(46))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(62))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 15){
                if (crop.getGrowthStageSelection().get(15))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(31))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(47))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(63))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 16){
                if (crop.getGrowthStageSelection().get(16))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(64))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 17){
                if (crop.getGrowthStageSelection().get(17))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(65))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 18){
                if (crop.getGrowthStageSelection().get(18))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(66))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 19){
                if (crop.getGrowthStageSelection().get(19))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(68))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

        }
        for (int i = 0; i < (16-count-1); i++)
            writer.println("GN 0");
            

        writer.close();
    }
 
    public void simulationDepth(cropObject crop, String path, String value){
               String date[];
        
        String location = path + "/Interface/tinputs.dat";
        
        File file = new File(location);
        
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter(file, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String info = "";
        if (crop.getCropType().contains("Types"))
            info = crop.getCropType().substring(0, crop.getCropType().indexOf("Types"));
        else
            info = crop.getCropType();
                
        writer.println(info);
        writer.println(crop.getCropVariety());
        writer.println("\"" + crop. getLocationOfWeatherFile() + "\"");
        date = crop.getDate().split("/");
        writer.println(date[0]);
        writer.println(date[1]);
        writer.println(date[2]);
        
        //Creating Day of Year

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[1]));
        
        cal.set(Calendar.YEAR, Integer.parseInt(date[2]));
        int daysInYear = 0;
        for (int x = 0; x < 12; x++){
            if (x < Integer.parseInt(date[0])-1){
                cal.set(Calendar.MONTH, x);
                int ActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                daysInYear = daysInYear + ActualMax;
            }else if (x == Integer.parseInt(date[0])-1){
                daysInYear = daysInYear + Integer.parseInt(date[1]);
            }
        }
        writer.println(Integer.toString(daysInYear));
        writer.println(value);
        writer.println(crop.getRate());
        writer.println(crop.getSoilMoisture());
        
        //Read Database and get SoilMoisture numbers EMERGENCE DATA
        
       for (int i = 0; i < 4; i ++){
             if (i == 0){
                writer.println("Optimum");
            }else if (i == 1){
                writer.println("Medium");
            }else if (i == 2){
                writer.println("Dry");
            }else if (i == 3){
                writer.println("Planted in dust");
            }
        
            if (i == 0){
                for (int x = 0; x < crop.getOptimum().size(); x++)
                    writer.println(crop.getOptimum().get(x));

            }else if (i ==1){
                for (int x = 0; x < crop.getMedium().size(); x++)
                    writer.println(crop.getMedium().get(x));

            }else if (i == 2){
                for (int x = 0; x < crop.getDry().size(); x++)
                    writer.println(crop.getDry().get(x));

            }else if (i == 3){
                for (int x = 0; x < crop.getPID().size(); x++)
                    writer.println(crop.getPID().get(x));
            }
        }
        
        writer.println(crop.getLatitude());
        writer.println(crop.getGddMethod());
        writer.println(crop.getBaseTemp());
        writer.println(crop.getLowerOptimumTemp());
        writer.println(crop.getUpperOptimumTemp());
        writer.println(crop.getMaxTemp());
        writer.println(crop.getGDD_PER_LEAF());
        writer.println(crop.getMaxCanopyHt());
        
        //Writer Vernal Data for printout
        for (int i = 0; i < crop.getVernalTypes().size(); i++){
            writer.println(crop.getVernalTypes().get(i));
        }
        writer.println(crop.getCanopyHt());  //Hardcoded the Canopy Ht. for right now
        
        int z = crop.getGrowthStagesData().size() / 4;
        int count = 0;
        for (int i = 0; i < z; i++){
            count++;
            if (i == 0){
                if (crop.getGrowthStageSelection().get(0))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(16))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 1){
                if (crop.getGrowthStageSelection().get(1))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(17))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 2){
                if (crop.getGrowthStageSelection().get(2))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(18))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 3){
                if (crop.getGrowthStageSelection().get(3))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(19))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 4){
                if (crop.getGrowthStageSelection().get(4))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(20))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(36))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(52))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 5){
                if (crop.getGrowthStageSelection().get(5))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(21))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(37))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(53))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 6){
                if (crop.getGrowthStageSelection().get(6))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(22))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(38))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(54))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 7){
                if (crop.getGrowthStageSelection().get(7))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(23))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(39))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(55))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 8){
                if (crop.getGrowthStageSelection().get(8))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(24))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(40))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(56))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

            else if (i == 9){
                if (crop.getGrowthStageSelection().get(9))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(25))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(41))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(57))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 10){
                if (crop.getGrowthStageSelection().get(10))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(26))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(42))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(58))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 11){
                if (crop.getGrowthStageSelection().get(11))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(27))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(43))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(59))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            
            else if (i == 12){
                if (crop.getGrowthStageSelection().get(12))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(28))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(44))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(60))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 13){
                if (crop.getGrowthStageSelection().get(13))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(29))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(45))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(61))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 14){
                if (crop.getGrowthStageSelection().get(14))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(30))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(46))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(62))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 15){
                if (crop.getGrowthStageSelection().get(15))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(31))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(47))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(63))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 16){
                if (crop.getGrowthStageSelection().get(16))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(64))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 17){
                if (crop.getGrowthStageSelection().get(17))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(65))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 18){
                if (crop.getGrowthStageSelection().get(18))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(66))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 19){
                if (crop.getGrowthStageSelection().get(19))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(68))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

        }
        for (int i = 0; i < (16-count-1); i++)
            writer.println("GN 0");
        

        writer.close(); 
    }
    public void simulationMaxCanopyHt(cropObject crop, String path, String value){
                String date[];
        
        String location = path + "/Interface/tinputs.dat";
        
        File file = new File(location);
        
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter(file, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String info = "";
        if (crop.getCropType().contains("Types"))
            info = crop.getCropType().substring(0, crop.getCropType().indexOf("Types"));
        else
            info = crop.getCropType();
                
        writer.println(info);
        writer.println(crop.getCropVariety());
        writer.println("\"" + crop. getLocationOfWeatherFile() + "\"");
        date = crop.getDate().split("/");
        writer.println(date[0]);
        writer.println(date[1]);
        writer.println(date[2]);
        
        //Creating Day of Year

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[1]));
        
        cal.set(Calendar.YEAR, Integer.parseInt(date[2]));
        int daysInYear = 0;
        for (int x = 0; x < 12; x++){
            if (x < Integer.parseInt(date[0])-1){
                cal.set(Calendar.MONTH, x);
                int ActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                daysInYear = daysInYear + ActualMax;
            }else if (x == Integer.parseInt(date[0])-1){
                daysInYear = daysInYear + Integer.parseInt(date[1]);
            }
        }
        writer.println(Integer.toString(daysInYear));
        writer.println(crop.getDepth());
        writer.println(crop.getRate());
        writer.println(crop.getSoilMoisture());
        
        //Read Database and get SoilMoisture numbers EMERGENCE DATA
        
        for (int i = 0; i < 4; i ++){
             if (i == 0){
                writer.println("Optimum");
            }else if (i == 1){
                writer.println("Medium");
            }else if (i == 2){
                writer.println("Dry");
            }else if (i == 3){
                writer.println("Planted in dust");
            }
        
            if (i == 0){
                for (int x = 0; x < crop.getOptimum().size(); x++)
                    writer.println(crop.getOptimum().get(x));

            }else if (i ==1){
                for (int x = 0; x < crop.getMedium().size(); x++)
                    writer.println(crop.getMedium().get(x));

            }else if (i == 2){
                for (int x = 0; x < crop.getDry().size(); x++)
                    writer.println(crop.getDry().get(x));

            }else if (i == 3){
                for (int x = 0; x < crop.getPID().size(); x++)
                    writer.println(crop.getPID().get(x));
            }
        }
        
        writer.println(crop.getLatitude());
        writer.println(crop.getGddMethod());
        writer.println(crop.getBaseTemp());
        writer.println(crop.getLowerOptimumTemp());
        writer.println(crop.getUpperOptimumTemp());
        writer.println(crop.getMaxTemp());
        writer.println(crop.getGDD_PER_LEAF());
        writer.println(value);
        
        //Writer Vernal Data for printout
        for (int i = 0; i < crop.getVernalTypes().size(); i++){
            writer.println(crop.getVernalTypes().get(i));
        }
        writer.println(crop.getCanopyHt());  //Hardcoded the Canopy Ht. for right now
        
        int z = crop.getGrowthStagesData().size() / 4;
        int count = 0;
        for (int i = 0; i < z; i++){
            count++;
            if (i == 0){
                if (crop.getGrowthStageSelection().get(0))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(16))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 1){
                if (crop.getGrowthStageSelection().get(1))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(17))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 2){
                if (crop.getGrowthStageSelection().get(2))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(18))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 3){
                if (crop.getGrowthStageSelection().get(3))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(19))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 4){
                if (crop.getGrowthStageSelection().get(4))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(20))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(36))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(52))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 5){
                if (crop.getGrowthStageSelection().get(5))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(21))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(37))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(53))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 6){
                if (crop.getGrowthStageSelection().get(6))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(22))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(38))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(54))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 7){
                if (crop.getGrowthStageSelection().get(7))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(23))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(39))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(55))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 8){
                if (crop.getGrowthStageSelection().get(8))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(24))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(40))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(56))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

            else if (i == 9){
                if (crop.getGrowthStageSelection().get(9))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(25))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(41))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(57))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 10){
                if (crop.getGrowthStageSelection().get(10))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(26))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(42))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(58))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 11){
                if (crop.getGrowthStageSelection().get(11))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(27))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(43))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(59))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            
            else if (i == 12){
                if (crop.getGrowthStageSelection().get(12))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(28))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(44))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(60))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 13){
                if (crop.getGrowthStageSelection().get(13))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(29))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(45))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(61))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 14){
                if (crop.getGrowthStageSelection().get(14))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(30))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(46))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(62))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 15){
                if (crop.getGrowthStageSelection().get(15))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(31))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(47))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(63))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 16){
                if (crop.getGrowthStageSelection().get(16))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(64))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 17){
                if (crop.getGrowthStageSelection().get(17))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(65))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 18){
                if (crop.getGrowthStageSelection().get(18))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(66))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 19){
                if (crop.getGrowthStageSelection().get(19))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(68))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

        }
        for (int i = 0; i < (16-count-1); i++)
            writer.println("GN 0");
            

        writer.close();
    }
    public void simulationSoilMoisture(cropObject crop, String path, int value){
        String date[];
        String SoilMoistureMethod = "";
        
         if (value == 0)
            SoilMoistureMethod = "Optimum";
        else if (value == 1)
            SoilMoistureMethod = "Medium";
        else if (value == 2)
            SoilMoistureMethod = "Dry";
        else if (value == 3)
            SoilMoistureMethod = "Planted in Dust";
        
        String location = path + "/Interface/tinputs.dat";
        
        File file = new File(location);
        
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        String info = "";
        if (crop.getCropType().contains("Types"))
            info = crop.getCropType().substring(0, crop.getCropType().indexOf("Types"));
        else
            info = crop.getCropType();
                
        writer.println(info);
        writer.println(crop.getCropVariety());
        writer.println("\"" + crop. getLocationOfWeatherFile() + "\"");
//        writer.println( "/"+crop.getCountry() + "/" + crop.getRegion() + "/" + crop. getLocationOfWeatherFile());
        date = crop.getDate().split("/");
        writer.println(date[0]);
        writer.println(date[1]);
        writer.println(date[2]);
        
        //Creating Day of Year

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[1]));
        
        cal.set(Calendar.YEAR, Integer.parseInt(date[2]));
        int daysInYear = 0;
        for (int x = 0; x < 12; x++){
            if (x < Integer.parseInt(date[0])-1){
                cal.set(Calendar.MONTH, x);
                int ActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                daysInYear = daysInYear + ActualMax;
            }else if (x == Integer.parseInt(date[0])-1){
                daysInYear = daysInYear + Integer.parseInt(date[1]);
                break;
            }
        }
        writer.println(Integer.toString(daysInYear));
        writer.println(crop.getDepth());
        writer.println(crop.getRate());
        writer.println(SoilMoistureMethod);
        
        //Read Database and get SoilMoisture numbers EMERGENCE DATA
        
        for (int i = 0; i < 4; i ++){
             if (i == 0){
                writer.println("Optimum");
            }else if (i == 1){
                writer.println("Medium");
            }else if (i == 2){
                writer.println("Dry");
            }else if (i == 3){
                writer.println("Planted in dust");
            }
        
            if (i == 0){
                for (int x = 0; x < crop.getOptimum().size(); x++)
                    writer.println(crop.getOptimum().get(x));

            }else if (i ==1){
                for (int x = 0; x < crop.getMedium().size(); x++)
                    writer.println(crop.getMedium().get(x));

            }else if (i == 2){
                for (int x = 0; x < crop.getDry().size(); x++)
                    writer.println(crop.getDry().get(x));

            }else if (i == 3){
                for (int x = 0; x < crop.getPID().size(); x++)
                    writer.println(crop.getPID().get(x));
            }
        }
        
        writer.println(crop.getLatitude());
        writer.println(crop.getGddMethod());
        writer.println(crop.getBaseTemp());
        writer.println(crop.getLowerOptimumTemp());
        writer.println(crop.getUpperOptimumTemp());
        writer.println(crop.getMaxTemp());
        writer.println(crop.getGDD_PER_LEAF());
        writer.println(crop.getMaxCanopyHt());
        
        //Writer Vernal Data for printout
        for (int i = 0; i < crop.getVernalTypes().size(); i++){
            writer.println(crop.getVernalTypes().get(i));
        }
        writer.println(crop.getCanopyHt());  
        
        int z = crop.getGrowthStagesData().size() / 4;
        int count = 0;
        for (int i = 0; i < z; i++){
            count++;
            if (i == 0){
                if (crop.getGrowthStageSelection().get(0))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(16))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 1){
                if (crop.getGrowthStageSelection().get(1))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(17))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 2){
                if (crop.getGrowthStageSelection().get(2))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(18))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 3){
                if (crop.getGrowthStageSelection().get(3))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(19))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 4){
                if (crop.getGrowthStageSelection().get(4))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(20))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(36))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(52))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 5){
                if (crop.getGrowthStageSelection().get(5))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(21))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(37))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(53))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 6){
                if (crop.getGrowthStageSelection().get(6))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(22))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(38))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(54))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 7){
                if (crop.getGrowthStageSelection().get(7))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(23))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(39))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(55))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 8){
                if (crop.getGrowthStageSelection().get(8))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(24))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(40))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(56))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

            else if (i == 9){
                if (crop.getGrowthStageSelection().get(9))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(25))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(41))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(57))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 10){
                if (crop.getGrowthStageSelection().get(10))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(26))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(42))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(58))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 11){
                if (crop.getGrowthStageSelection().get(11))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(27))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(43))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(59))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            
            else if (i == 12){
                if (crop.getGrowthStageSelection().get(12))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(28))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(44))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(60))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 13){
                if (crop.getGrowthStageSelection().get(13))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(29))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(45))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(61))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 14){
                if (crop.getGrowthStageSelection().get(14))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(30))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(46))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(62))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 15){
                if (crop.getGrowthStageSelection().get(15))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(31))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(47))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(63))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 16){
                if (crop.getGrowthStageSelection().get(16))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(64))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 17){
                if (crop.getGrowthStageSelection().get(17))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(65))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 18){
                if (crop.getGrowthStageSelection().get(18))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(66))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 19){
                if (crop.getGrowthStageSelection().get(19))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(68))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

        }
        for (int i = 0; i < (16-count-1); i++)
            writer.println("GN 0");
       

        writer.close();
    }
    public void simulationDate(cropObject crop, String path, String WeatherFile,String date[]){
        
        
        String location = path + "/interface/tinputs.dat";
        
        File file = new File(location);
        
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter(file, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        String info = "";
        if (crop.getCropType().contains("Types"))
            info = crop.getCropType().substring(0, crop.getCropType().indexOf("Types"));
        else
            info = crop.getCropType();
                
        writer.println(info);
        writer.println(crop.getCropVariety());
        writer.println("\"" + WeatherFile + "\"");
        
        
        writer.println(date[1]);
        writer.println(date[0]);
        writer.println(date[2]);
        
        //Creating Day of Year
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[1]));
        
        cal.set(Calendar.YEAR, Integer.parseInt(date[2]));
        int daysInYear = 0;
        for (int x = 0; x < 12; x++){
            if (x < Integer.parseInt(date[1])-1){
                cal.set(Calendar.MONTH, x);
                int ActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                daysInYear = daysInYear + ActualMax;
            }else if (x == Integer.parseInt(date[1])-1){
                daysInYear = daysInYear + Integer.parseInt(date[0]);
                break;
            }
        }

        writer.println(Integer.toString(daysInYear));
        writer.println(crop.getDepth());
        writer.println(crop.getRate());
        writer.println(crop.getSoilMoisture());
        
        //Read Database and get SoilMoisture numbers EMERGENCE DATA
        
        for (int i = 0; i < 4; i ++){
             if (i == 0){
                writer.println("Optimum");
            }else if (i == 1){
                writer.println("Medium");
            }else if (i == 2){
                writer.println("Dry");
            }else if (i == 3){
                writer.println("Planted in dust");
            }
        
            if (i == 0){
                for (int x = 0; x < crop.getOptimum().size(); x++)
                    writer.println(crop.getOptimum().get(x));

            }else if (i ==1){
                for (int x = 0; x < crop.getMedium().size(); x++)
                    writer.println(crop.getMedium().get(x));

            }else if (i == 2){
                for (int x = 0; x < crop.getDry().size(); x++)
                    writer.println(crop.getDry().get(x));

            }else if (i == 3){
                for (int x = 0; x < crop.getPID().size(); x++)
                    writer.println(crop.getPID().get(x));
            }
        }
        
        writer.println(crop.getLatitude());
        writer.println(crop.getGddMethod());
        writer.println(crop.getBaseTemp());
        writer.println(crop.getLowerOptimumTemp());
        writer.println(crop.getUpperOptimumTemp());
        writer.println(crop.getMaxTemp());
        writer.println(crop.getGDD_PER_LEAF());
        writer.println(crop.getMaxCanopyHt());
        
        //Writer Vernal Data for printout
        for (int i = 0; i < crop.getVernalTypes().size(); i++){
            writer.println(crop.getVernalTypes().get(i));
        }
        writer.println(crop.getCanopyHt());  //Hardcoded the Canopy Ht. for right now
        
        int z = crop.getGrowthStagesData().size() / 4;
        int count = 0;
        for (int i = 0; i < z; i++){
            count++;
            if (i == 0){
                if (crop.getGrowthStageSelection().get(0))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(16))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 1){
                if (crop.getGrowthStageSelection().get(1))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(17))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 2){
                if (crop.getGrowthStageSelection().get(2))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(18))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 3){
                if (crop.getGrowthStageSelection().get(3))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(19))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 4){
                if (crop.getGrowthStageSelection().get(4))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(20))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(36))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(52))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 5){
                if (crop.getGrowthStageSelection().get(5))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(21))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(37))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(53))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 6){
                if (crop.getGrowthStageSelection().get(6))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(22))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(38))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(54))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 7){
                if (crop.getGrowthStageSelection().get(7))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(23))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(39))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(55))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 8){
                if (crop.getGrowthStageSelection().get(8))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(24))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(40))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(56))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

            else if (i == 9){
                if (crop.getGrowthStageSelection().get(9))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(25))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(41))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(57))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 10){
                if (crop.getGrowthStageSelection().get(10))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(26))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(42))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(58))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 11){
                if (crop.getGrowthStageSelection().get(11))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(27))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(43))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(59))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            
            else if (i == 12){
                if (crop.getGrowthStageSelection().get(12))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(28))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(44))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(60))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 13){
                if (crop.getGrowthStageSelection().get(13))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(29))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(45))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(61))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 14){
                if (crop.getGrowthStageSelection().get(14))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(30))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(46))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(62))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 15){
                if (crop.getGrowthStageSelection().get(15))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(31))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(47))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(63))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 16){
                if (crop.getGrowthStageSelection().get(16))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(64))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 17){
                if (crop.getGrowthStageSelection().get(17))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(65))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 18){
                if (crop.getGrowthStageSelection().get(18))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(66))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 19){
                if (crop.getGrowthStageSelection().get(19))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(68))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

        }
        for (int i = 0; i < (16-count-1); i++)
            writer.println("GN 0");
            


        writer.close();
    }
    public void simulatePeriodMoisture(cropObject crop, String path, int value, String WeatherFile, java.util.Calendar date){
        String dateParsed[];
        String SoilMoistureMethod = "";
        
         if (value == 0)
            SoilMoistureMethod = "Optimum";
        else if (value == 1)
            SoilMoistureMethod = "Medium";
        else if (value == 2)
            SoilMoistureMethod = "Dry";
        else if (value == 3)
            SoilMoistureMethod = "Planted in Dust";
        
        String location = path + "/Interface/tinputs.dat";
        
        File file = new File(location);
        
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        String info = "";
        if (crop.getCropType().contains("Types"))
            info = crop.getCropType().substring(0, crop.getCropType().indexOf("Types"));
        else
            info = crop.getCropType();
                
        writer.println(info);
        writer.println(crop.getCropVariety());
        writer.println("\"" + WeatherFile + "\"" );
//        writer.println( "/"+crop.getCountry() + "/" + crop.getRegion() + "/" + crop. getLocationOfWeatherFile());
        writer.println(date.get(Calendar.MONTH) + 1);
        writer.println(date.get(Calendar.DAY_OF_MONTH));
        writer.println(date.get(Calendar.YEAR));
        
        //Creating Day of Year

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
        
        cal.set(Calendar.YEAR,date.get(Calendar.YEAR));
        int daysInYear = 0;
        for (int x = 0; x < 12; x++){
            if (x < date.get(Calendar.MONTH)){
                cal.set(Calendar.MONTH, x);
                int ActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                daysInYear = daysInYear + ActualMax;
            }else if (x == date.get(Calendar.MONTH)){
                daysInYear = daysInYear + date.get(Calendar.DAY_OF_MONTH);
            }
        }
        writer.println(Integer.toString(daysInYear));
        writer.println(crop.getDepth());
        writer.println(crop.getRate());
        writer.println(SoilMoistureMethod);
        
        //Read Database and get SoilMoisture numbers EMERGENCE DATA
        
        for (int i = 0; i < 4; i ++){
             if (i == 0){
                writer.println("Optimum");
            }else if (i == 1){
                writer.println("Medium");
            }else if (i == 2){
                writer.println("Dry");
            }else if (i == 3){
                writer.println("Planted in dust");
            }
        
            if (i == 0){
                for (int x = 0; x < crop.getOptimum().size(); x++)
                    writer.println(crop.getOptimum().get(x));

            }else if (i ==1){
                for (int x = 0; x < crop.getMedium().size(); x++)
                    writer.println(crop.getMedium().get(x));

            }else if (i == 2){
                for (int x = 0; x < crop.getDry().size(); x++)
                    writer.println(crop.getDry().get(x));

            }else if (i == 3){
                for (int x = 0; x < crop.getPID().size(); x++)
                    writer.println(crop.getPID().get(x));
            }
        }
        
        writer.println(crop.getLatitude());
        writer.println(crop.getGddMethod());
        writer.println(crop.getBaseTemp());
        writer.println(crop.getLowerOptimumTemp());
        writer.println(crop.getUpperOptimumTemp());
        writer.println(crop.getMaxTemp());
        writer.println(crop.getGDD_PER_LEAF());
        writer.println(crop.getMaxCanopyHt());
        
        //Writer Vernal Data for printout
        for (int i = 0; i < crop.getVernalTypes().size(); i++){
            writer.println(crop.getVernalTypes().get(i));
        }
        writer.println(crop.getCanopyHt());  
        
        int z = crop.getGrowthStagesData().size() / 4;
        int count = 0;
        for (int i = 0; i < z; i++){
            count++;
            if (i == 0){
                if (crop.getGrowthStageSelection().get(0))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(16))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 1){
                if (crop.getGrowthStageSelection().get(1))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(17))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 2){
                if (crop.getGrowthStageSelection().get(2))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(18))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 3){
                if (crop.getGrowthStageSelection().get(3))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(19))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 4){
                if (crop.getGrowthStageSelection().get(4))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(20))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(36))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(52))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 5){
                if (crop.getGrowthStageSelection().get(5))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(21))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(37))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(53))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 6){
                if (crop.getGrowthStageSelection().get(6))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(22))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(38))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(54))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 7){
                if (crop.getGrowthStageSelection().get(7))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(23))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(39))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(55))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 8){
                if (crop.getGrowthStageSelection().get(8))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(24))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(40))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(56))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

            else if (i == 9){
                if (crop.getGrowthStageSelection().get(9))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(25))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(41))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(57))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 10){
                if (crop.getGrowthStageSelection().get(10))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(26))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(42))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(58))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }            
            else if (i == 11){
                if (crop.getGrowthStageSelection().get(11))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(27))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(43))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(59))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            
            else if (i == 12){
                if (crop.getGrowthStageSelection().get(12))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(28))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(44))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(60))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 13){
                if (crop.getGrowthStageSelection().get(13))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(29))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(45))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(61))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 14){
                if (crop.getGrowthStageSelection().get(14))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(30))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(46))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(62))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 15){
                if (crop.getGrowthStageSelection().get(15))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(31))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(47))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(63))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 16){
                if (crop.getGrowthStageSelection().get(16))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(32))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(48))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(64))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 17){
                if (crop.getGrowthStageSelection().get(17))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(33))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(49))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(65))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 18){
                if (crop.getGrowthStageSelection().get(18))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(34))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(50))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(66))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }
            else if (i == 19){
                if (crop.getGrowthStageSelection().get(19))
                    writer.println("GN " + crop.getGrowthStagesData().get(i));
                else if (crop.getGrowthStageSelection().get(35))
                    writer.println("GS " + crop.getGrowthStagesData().get(i+z));
                else if (crop.getGrowthStageSelection().get(51))
                    writer.println("LN " + crop.getGrowthStagesData().get(i+(z*2)));
                else if (crop.getGrowthStageSelection().get(68))
                    writer.println("LS " + crop.getGrowthStagesData().get(i+(z*3)));
            }

        }
        for (int i = 0; i < (16-count-1); i++)
            writer.println("GN 0");
       

        writer.close();
    }
    
    
      public void WriteSettingsFile(String filePath, String Country, String Region){
        //Creating file
        Path p1 = Paths.get(filePath + "/settings.stt");
        File Input = new File(p1.toString());


        //File Writer Variables
        PrintWriter out = null;
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(Input.toString(), false);
        } catch (IOException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);
        out.write("\n");

        out.write("Location Settings");
        out.write(System.getProperty("line.separator"));

        if (Country.length() > 0){
            out.write(String.format("%-10s: %s","Country", Country));
            out.write(System.getProperty("line.separator"));
        }
        if (Region.length() > 0){
            out.write(String.format("%-10s: %s","Region", Region));
            out.write(System.getProperty("line.separator"));
        }

        out.close();        
        
    }
    public List <String> ReadSettingsFile(String filePath){
        //Creating file
        Path p1 = Paths.get(filePath + "/settings.stt");
        File Input = new File(p1.toString());
        List <String> data = new ArrayList();
        
        try {
                String splitData[];
                BufferedReader in = new BufferedReader(new FileReader(p1.toString()));
                while(in.ready()){
                    String s = in.readLine();
                    splitData = s.split(":");

                    if (splitData[0].trim().equalsIgnoreCase("Country")){
                        boolean add = data.add(splitData[1].trim());
                    }
                    if (splitData[0].trim().equalsIgnoreCase("Region")){
                        boolean add = data.add(splitData[1].trim());
                    }
                }
            
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        if (data.size() == 0){
            data.add("No Country");
            data.add("No Region");
        }
        return data;
    }
	
    public List <String> readWeatherFile(String filePath) throws IOException{
        String split[] = {""};
        String temp = "";
        List <String> fileYears = new ArrayList();
        boolean beginningFound = false;
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            while(in.ready()){
                String s = in.readLine();
                split = s.split("\\s+");
                
                if (split.length >=8)
                    if (!split[8].equalsIgnoreCase("") && split[8] != null && !beginningFound){
                        if (!split[8].contains(".") && Integer.parseInt(split[8]) > 1890){ //this was 1900. DE changed the value
                            fileYears.add(split[8]);
                            beginningFound = true;
                        }
                    }else{
                        temp = split[8];
                    }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        fileYears.add(temp);
        return fileYears;
    }
    public void saveFile(cropObject crop, String path, String runType){
        Path p1 = Paths.get(path + "\\" + "Interface\\results\\" + crop.getFileName());
        File f = new File(p1.toString());
        if (f.exists()){
            //Rename Directory
            f.renameTo(p1.toFile());

        }else{
            f = new File(p1.toString());
            
            if (!f.exists()){
                //Create Directory
                try {
                    Files.createDirectory(p1);
                } catch (IOException ex) {
                    Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
//        if (runType.equalsIgnoreCase("S")){
            Path p2 = Paths.get(p1.toString()+"\\input.sav");
            p1 = Paths.get(p1.getParent().getParent().toString() + "\\" + "tinputs.dat" );
            
            //Writing the T-inputs.dat file to a save file
            try {
                writeTINPUTS_DAT(crop, path, "S", false);
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                f = new File (p2.toString());
                if (f.exists())
                    f.delete();
                Files.copy(p1, p2, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
            
             //File Writer Variables
            PrintWriter out = null;
            BufferedWriter bw = null;
            FileWriter fw = null;

            //Paths
            Path inDest = Paths.get(p1.getParent().toString()+ "\\results\\" + crop.getFileName() +"\\input.sav");
            File Input = new File(inDest.toString());

            try {
                fw = new FileWriter(Input.toString(), true);
            } catch (IOException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            out.write("\n\n\n");
//
//           
//            out.write(String.format("%-40s","Name of File: " + crop.getFileName() + "\n\n"));
//            
//            out.write("Growth Stage Data" + "\n");
//            for (int i = 0; i < crop.getGrowthStagesData().size(); i++){
//                if (i == crop.getGrowthStagesData().size()-1)
//                    out.write(crop.getGrowthStagesData().get(i) + "\n\n");
//                else
//                    out.write(crop.getGrowthStagesData().get(i) + ",");
//            }
//            out.write("Growth Stage Headers" + "\n");
//            for (int i = 0; i < crop.getGrowthStagesHeaders().size(); i++){
//                if (i == crop.getGrowthStagesHeaders().size()-1)
//                    out.write(crop.getGrowthStagesHeaders().get(i) + "\n\n");
//                else
//                    out.write(crop.getGrowthStagesHeaders().get(i) + ",");
//            }
//
//            
//            out.close();
//            
//        }else if (runType.equalsIgnoreCase("B")){
//            Path p2 = Paths.get(p1.toString()+"\\input.sav");
//            p1 = Paths.get(p1.getParent().getParent().toString() + "\\" + "tinputs.dat" );        
//            try {
//                writeTINPUTS_DAT(crop, path, runType, false);
//            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
//                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
//            }
//             try {
//                Files.copy(p1, p2);
//            } catch (IOException ex) {
//                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//            //File Writer Variables
//            PrintWriter out = null;
//            BufferedWriter bw = null;
//            FileWriter fw = null;
//
//            //Paths
//            Path inDest = Paths.get(p1.getParent().toString()+ "\\results\\" + crop.getFileName() +"\\input.sav");
//            File Input = new File(inDest.toString());
//
//            try {
//                fw = new FileWriter(Input.toString(), true);
//            } catch (IOException ex) {
//                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            bw = new BufferedWriter(fw);
//            out = new PrintWriter(bw);
//            out.write("\n\n\n");

           
            out.write(String.format("%-40s","Name of File: " + crop.getFileName() + "\n\n"));
            
            out.write("Growth Stage Data" + "\n");
            for (int i = 0; i < crop.getGrowthStagesData().size(); i++){
                if (i == crop.getGrowthStagesData().size()-1)
                    out.write(crop.getGrowthStagesData().get(i) + "\n\n");
                else
                    out.write(crop.getGrowthStagesData().get(i) + ",");
            }
            out.write("Growth Stage Headers" + "\n");
            for (int i = 0; i < crop.getGrowthStagesHeaders().size(); i++){
                if (i == crop.getGrowthStagesHeaders().size()-1)
                    out.write(crop.getGrowthStagesHeaders().get(i) + "\n\n");
                else
                    out.write(crop.getGrowthStagesHeaders().get(i) + ",");
            }

            out.write("Growth Stage Selections" + "\n");
            for (int i = 0; i < crop.getGrowthStageSelection().size(); i++){
                if (i == crop.getGrowthStageSelection().size()-1)
                    out.write(crop.getGrowthStageSelection().get(i) + "\n\n");
                else
                    out.write(crop.getGrowthStageSelection().get(i) + ",");
            }
            
            //Also writing a range file
            if (crop.getRateRange().size() > 0){
                out.write(String.format("%-20s","Rate Ranges"));
                out.write(System.getProperty("line.separator"));
                for (int x = 0; x < crop.getRateRange().size(); x++){
                    out.write(crop.getRateRange().get(x) + "\n");
                }
            }
            if (crop.getDepthRange().size() > 0){
                out.write(String.format("%-20s","Depth Ranges"));
                out.write(System.getProperty("line.separator"));
                for (int x = 0; x < crop.getDepthRange().size(); x++){
                    out.write(crop.getDepthRange().get(x) + "\n");
                }
            }
            if (crop.getMaxCanopyHtRange().size() > 0){
                out.write(String.format("%-20s","MaxCanopyHt Ranges"));
                out.write(System.getProperty("line.separator"));
                for (int x = 0; x < crop.getMaxCanopyHtRange().size(); x++){
                    out.write(crop.getMaxCanopyHtRange().get(x) + "\n");
                }
            }
            if (crop.getSoilMoistureRange().size() > 0){
                out.write(String.format("%-20s","Soil Moisture Ranges\n"));
                for (int x = 0; x < crop.getSoilMoistureRange().size(); x++){
                    out.write(crop.getSoilMoistureRange().get(x) + "\n");
                }
            }
            if (crop.getWeatherFileRanges().size() > 0){
                out.write(String.format("%-20s","WeatherFile Ranges\n"));
                for (int x = 0; x < crop.getWeatherFileRanges().size(); x++){
                    out.write(crop.getWeatherFileRanges().get(x) + "\n");
                }
            }
            
            out.write("\n EOF");
            out.close();
//        }
        
    }
    public cropObject openFile(String filePath){
        cropObject crop = new cropObject();
        String splitData[];
        int lineCounter = 1;
        int internalCounter = 0;
        
        
        //boolean variables so that we can grab data
        boolean rateRanges = false;
        boolean depthRanges = false;
        boolean maxCanopyRanges = false;
        boolean soilRanges = false;
        boolean dateRanges = false;
        boolean GrowthStageData = false;
        boolean GrowthStageHeaders = false;
        boolean GrowthStageSelection = false;
        
         //Creating file
        Path p1 = Paths.get(filePath);
        File Input = new File(p1.toString());
        List <String> data = new ArrayList();
        List <Boolean> gsSelect = new ArrayList();
        boolean gsSelections[] = new boolean[64];
        Arrays.fill(gsSelections, Boolean.FALSE);
        
        try {
             
                BufferedReader in = new BufferedReader(new FileReader(p1.toString()));
                while(in.ready()){
                    String s = in.readLine();
                    s.trim();
                    if (lineCounter == 1)
                        crop.setCropType(s);
                    else if (lineCounter == 2)
                        crop.setCropVariety(s);
                    else if (lineCounter == 3){
                        splitData = s.split("/");
                        crop.setCountry(splitData[0].substring(1, splitData[0].length()));
                        
                        if (splitData.length == 2)
                            crop.setWeatherFileName(splitData[1].substring(0, splitData[1].length()-1));
                        else{
                            crop.setRegion(splitData[1]);
                            crop.setWeatherFileName(splitData[2].substring(0, splitData[2].length()-1));
                        }
                    }else if (lineCounter >= 4 && lineCounter <= 6){
                        data.add(s);
                        if (lineCounter == 6){
                            crop.setDate(data.get(0) + "/" + data.get(1) + "/" + data.get(2));
                            data.clear();
                        }
                    }else if (lineCounter == 8)
                        crop.setDepth(s);
                    else if (lineCounter == 9)
                        crop.setRate(s);
                    else if (lineCounter == 10)
                        crop.setSoilMoisture(s);
                    else if (lineCounter >= 12 && lineCounter <= 15){
                        data.add(s);
                        if (lineCounter == 15){
                            crop.setOptimum(data);
                            data.clear();
                        }
                    }else if (lineCounter >= 17 && lineCounter <= 20){
                        data.add(s);
                        if (lineCounter == 20){
                            crop.setMedium(data);
                            data.clear();
                        }
                    }else if (lineCounter >= 22 && lineCounter <= 25){
                        data.add(s);
                        if (lineCounter == 25){
                            crop.setDry(data);
                            data.clear();
                        }
                    }else if (lineCounter >= 27 && lineCounter <= 30){
                        data.add(s);
                        if (lineCounter == 30){
                            crop.setPID(data);
                            data.clear();
                        }
                    }else if (lineCounter == 31)
                        crop.setLatitude(s);
                    else if (lineCounter == 32)
                        crop.setGddMethod(s);
                    else if (lineCounter == 33)
                        crop.setBaseTemp(s);
                    else if (lineCounter == 34)
                        crop.setLOTemp(s);
                    else if (lineCounter == 35)
                        crop.setUOTemp(s);
                    else if (lineCounter == 36)
                        crop.setMaxTemp(s);
                    else if (lineCounter == 37)
                        crop.setGDDLeaf(s);
                    else if (lineCounter == 38)
                        crop.setMaxCanopyHt(s);
                    else if (lineCounter >= 39 && lineCounter <= 44){
                        data.add(s);
                        
                        if (lineCounter == 44){
                            crop.setVernalization(data);
                            data.clear();
                        }
                    }else if (lineCounter == 45)
                        crop.setCanopyHt(s);
                    else if (lineCounter >= 46 && (s.split(" ")[0].equalsIgnoreCase("GN") || s.split(" ")[0].equalsIgnoreCase("GS") || s.split(" ")[0].equalsIgnoreCase("LN")) || s.split(" ")[0].equalsIgnoreCase("LS")){
                        String st[] = s.trim().split(" ");
                        
//                        //Setting the internal Counter
//                        if (lineCounter == 46)
//                            internalCounter = 0;
//                        
//                        if (st[0].equalsIgnoreCase("GN"))
//                            gsSelections[internalCounter] = true;
//                        else
//                            gsSelections[internalCounter] = false;
//                        
//                        if (st[0].equalsIgnoreCase("GS"))
//                            gsSelections[internalCounter+15] = true;
//                        else
//                            gsSelections[internalCounter+15] = false;
//                        if (st[0].equalsIgnoreCase("LN"))
//                            gsSelections[internalCounter+31] = true;
//                        else
//                            gsSelections[internalCounter+31] = false;
//                        if (st[0].equalsIgnoreCase("LS"))
//                            gsSelections[internalCounter+47] = true;
//                        else
//                            gsSelections[internalCounter+47] = false;
//                        
//                        
//                        internalCounter++;

                    }else if (s.trim().contains("Name of File:")){ 
                        crop.setFileName(s.split(":")[1].trim());
                        
                    }else if (s.trim().equalsIgnoreCase("Growth Stage Data") || GrowthStageData){
                        
                        if (GrowthStageData){
                            String GSD[] = s.split(",");
                            for (int x = 0; x < GSD.length; x++)
                                data.add(GSD[x].trim());
                            crop.setGrowthStagesData(data);
                            GrowthStageData = false;
                        }else{
                            GrowthStageData = true;
                        }
                        if (!GrowthStageData){
                            data.clear();
                        }
                    }else if (s.trim().equalsIgnoreCase("Growth Stage Headers") || GrowthStageHeaders){

                        if (GrowthStageHeaders){
                            String GSH[] = s.split(",");
                            for (int x = 0; x < GSH.length; x++)
                                data.add(GSH[x].trim());
                            crop.setGrowthStagesHeaders(data);
                            GrowthStageHeaders = false;
                        }else{
                            GrowthStageHeaders = true;
                        }
                        if (!GrowthStageHeaders)
                            data.clear();
                        
                    }else if (s.trim().equalsIgnoreCase("Growth Stage Selections") || GrowthStageSelection){
                        
                        if (GrowthStageSelection){
                            String GSD[] = s.split(",");
                            for (int x = 0; x < GSD.length; x++)
                                gsSelect.add(Boolean.parseBoolean(GSD[x].trim()));
                            crop.setGrowthStagesSelection(gsSelect);
                            GrowthStageSelection = false;
                        }else{
                            GrowthStageSelection = true;
                        }
                        if (!GrowthStageSelection){
                            data.clear();
                        }
                    }else if (s.trim().contains("Rate Ranges") || rateRanges == true){
                        
                        if (rateRanges == true){
                            data.add(s);
                            if (internalCounter == 3 ){
                                crop.setRateRange(data);
                                rateRanges = false;
                                data.clear();
                            }
                        }else{
                            internalCounter = 0;
                            rateRanges = true;
                        }
                        internalCounter++;
                    }else if (s.trim().contains("Depth Ranges") || depthRanges == true){
                        if (depthRanges == true){
                            data.add(s);
                            if (internalCounter == 3 ){
                                crop.setDepthRange(data);
                                depthRanges = false;
                                data.clear();
                            }
                        }else{
                            internalCounter = 0;
                            depthRanges = true;
                        }
                        internalCounter++;
                        
                    }else if (s.trim().contains("MaxCanopyHt Ranges") ||maxCanopyRanges == true){
                        if (maxCanopyRanges == true){
                            data.add(s);
                            if (internalCounter == 3 ){
                                crop.setMaxCanopyHtRange(data);
                                maxCanopyRanges = false;
                                data.clear();
                            }
                        }else{
                            internalCounter = 0;
                            maxCanopyRanges = true;
                        }
                        internalCounter++;
                    
                    }else if (s.trim().contains("Soil Moisture Ranges") || soilRanges == true){
                        if (soilRanges == true){
                            data.add(s);
                            if (internalCounter == 3 ){
                                crop.setSoilMoistureRange(data);
                                soilRanges = false;
                                data.clear();
                            }
                        }else{
                            internalCounter = 0;
                            soilRanges = true;
                        }
                        internalCounter++;
                        
                    }else if (s.trim().contains("WeatherFile Ranges") || dateRanges == true){
                        if (dateRanges == true){
                            if (!s.equalsIgnoreCase("EOF") && !s.equalsIgnoreCase("")){
                                String st[] = s.trim().substring(1, s.length()-2).split(",");
                                List <String> loadingWeather = new ArrayList();
                                for (int x = 0; x < st.length; x++){
                                        loadingWeather.add(st[x].trim());
                                        if (x == st.length-1)
                                            crop.setWeatherFileRanges(loadingWeather);
                                    }
                                }else{
                                    dateRanges = false;
                                    data.clear();
                                }
                        }else{
                           internalCounter = 0;
                           dateRanges = true;
                        }
                        internalCounter++;
                    }
                   lineCounter++;     
                }
                //Adding  Growth Statge Selections to crop.
//                for (int i = 0; i < gsSelections.length; i++){
//                    gsSelect.add(gsSelections[i]);
//                }
                crop.setGrowthStagesSelection(gsSelect);
            
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }

        return crop;
    }
    public void deleteWeatherFiles(String location){
        
    }
	
	public void deleteDirectory(Path dir) throws IOException {
		if(Files.exists(dir) && Files.isDirectory(dir)) {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if (exc == null) {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					} else {
						// directory iteration failed; propagate exception
						throw exc;
					}
				}
			});
		}
	}
 }
