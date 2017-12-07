/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MMS.helperFunctions;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author mike.herder
 */
public class cropObject {
    private String fileName = "";
    private String tableName = "";
    private String cropType = "";
    private String cropVariety = "";
    private String country = "";
    private String region = "";
    private String weatherFile = "";
    private String WeatherFileName = "";
    private String piDate = "";
    private String piSoilMoisture = "";
    private String piDepth = "";
    private String piRate = "";
    private String piLatitude = "";
    private String piMaxCanopyHt = "";
    private String baseTemp = "";
    private String LowerOptimumTemp = "";
    private String UpperOptimumTemp = "";
    private String MaximumTemp = "";
    private String GddPerLeaf = "";
    
    //Crop Variety Specific Data
    private List gsHeaders = new ArrayList();
    private List <String> GrowthStagesData = new ArrayList();
    private List <Boolean> gsSelections = new ArrayList();
    private List <String> VernalTypes = new ArrayList();
    private String gddMethod = "";
    private String P1D = "";
    private String P1DT = "";
    private String CanopyHt = "";
    private String Phyllochron = "";
    
    //Emerge Data
    private List <String> Optimum = new ArrayList();
    private List <String> Medium = new ArrayList();
    private List <String> Dry = new ArrayList();
    private List <String> PID = new ArrayList();
    
    private List <List> WeatherFileRanges =  new ArrayList();
    private List <String> rateRange = new ArrayList();
    private List <String> soilMoistureRange = new ArrayList();
    private List <String> depthRange = new ArrayList();
    private List <String> maxCanopyHtRange = new ArrayList();
    private boolean simulatePeriodMoisture = false;
    private boolean cropLoaded = false;
    
    public cropObject(){
        
    }
    
    //Constructor
    public cropObject(List<String> CropType, List<String> data){
        List <String> temp = new ArrayList(data);

        
        tableName = CropType.get(1);
        cropType = CropType.get(0);
        cropVariety = temp.get(0);
            temp.remove(0);
        
        //Verifying the date is properly formatted.
//        SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
//        piDate = date.format(CropType.get(2));
        piDate = CropType.get(2);
        piSoilMoisture = CropType.get(3);
        piDepth = CropType.get(4);
        piRate = CropType.get(5);
        piMaxCanopyHt = CropType.get(6);;

        //These Items below are items that should never be changed. So there will be no Setter methods, Just Getter methods.
        baseTemp = CropType.get(7);
        LowerOptimumTemp = CropType.get(8);
        UpperOptimumTemp = CropType.get(9);
        MaximumTemp = CropType.get(10);
        
        GddPerLeaf = CropType.get(12);
        
        Optimum.add(CropType.get(13));      //Adding Optimum WFPSL
        Optimum.add(CropType.get(14));      //Adding Optimum WFPSUP
        Optimum.add(CropType.get(15));      //Adding Optimum GERMGDD
        Optimum.add(CropType.get(16));      //Adding Optimum ERGDD
        
        Medium.add(CropType.get(17));      //Adding Medium WFPSL
        Medium.add(CropType.get(18));      //Adding Medium WFPSUP
        Medium.add(CropType.get(19));      //Adding Medium GERMGDD
        Medium.add(CropType.get(20));      //Adding Medium ERGDD
        
        Dry.add(CropType.get(21));      //Adding Dry WFPSL
        Dry.add(CropType.get(22));      //Adding Dry WFPSUP
        Dry.add(CropType.get(23));      //Adding Dry GERMGDD
        Dry.add(CropType.get(24));      //Adding Dry ERGDD
        
        PID.add(CropType.get(25));      //Adding Planted In Dust WFPSL
        PID.add(CropType.get(26));      //Adding Planted In Dust WFPSUP
        PID.add(CropType.get(27));      //Adding Planted In Dust GERMGDD
        PID.add(CropType.get(28));      //Adding Planted In Dust ERGDD
        


        if (temp.get(temp.size()-1) != null)
            Phyllochron = temp.get(temp.size()-1);
        else
            Phyllochron = "1";
        temp.remove(temp.size()-1);
        
        CanopyHt = temp.get(temp.size()-1);
           temp.remove(temp.size()-1);
        P1DT = temp.get(temp.size()-1);
           temp.remove(temp.size()-1);
        P1D = temp.get(temp.size()-1);
            temp.remove(temp.size()-1);
        gddMethod = temp.get(temp.size()-1);
           temp.remove(temp.size()-1);
        
        VernalTypes.add(0, temp.get(temp.size()-1));
           temp.remove(temp.size()-1);
        VernalTypes.add(0, temp.get(temp.size()-1));
           temp.remove(temp.size()-1);
        VernalTypes.add(0, temp.get(temp.size()-1));
           temp.remove(temp.size()-1);
        VernalTypes.add(0, temp.get(temp.size()-1));
           temp.remove(temp.size()-1);
        VernalTypes.add(0, temp.get(temp.size()-1));
           temp.remove(temp.size()-1);
        VernalTypes.add(0, temp.get(temp.size()-1));
           temp.remove(temp.size()-1);
    
        GrowthStagesData.addAll(temp);
        
    }
    
    /*
    * Setters for all private variables
    */
    public void setFileName(String FILENAME){
        String temp = FILENAME;
        fileName = temp;
    }
    public void setCropType(String CROPTYPE){
        cropType = CROPTYPE;
    }
    public void setCropVariety(String CROPVARIETY){
        cropVariety = CROPVARIETY;
    }
    public void setCountry(String COUNTRY){
        country = COUNTRY;
    }
    public void setRegion (String REGION){
        region = REGION;
    }
    public void setWeatherFileName (String WEATHERFILE){
        weatherFile = WEATHERFILE;
    }
    public void setNameOfWeatherFile (String name){
        WeatherFileName = name;
    }
    public void setDate (String DATE){
        piDate = DATE;
    }
    public void setSoilMoisture (String SOILMOISTURE){
        piSoilMoisture = SOILMOISTURE;
    }
    public void setDepth (String DEPTH){
        piDepth = DEPTH;
    }
    public void setRate (String RATE){
        piRate = RATE;
    }
    public void setLatitude (String LATITUDE){
        piLatitude = LATITUDE;
    }
    public void setMaxCanopyHt(String MAXCANOPYHt){
        piMaxCanopyHt = MAXCANOPYHt;
    }
    public void setGrowthStagesHeaders (List <String> GROWTHSTAGEHEADERS){
        List <String> newList = new ArrayList(GROWTHSTAGEHEADERS);
        gsHeaders.clear();
        gsHeaders.addAll(newList);
    }
    public void setGrowthStagesData (List GROWTHSTAGES){
        List <String> newList = new ArrayList(GROWTHSTAGES);
        GrowthStagesData.clear();
        GrowthStagesData.addAll(newList);
    }
    public void setGrowthStagesSelection (List <Boolean> gsSELECTIONS){
        List <Boolean> newList = new ArrayList(gsSELECTIONS);        
        gsSelections.clear();
        gsSelections.addAll(newList);
    }
    public void setGddMethod(String GDD){
        gddMethod = GDD;
    }
    public void setPhotoPeriod1(String PP1){
        P1D = PP1;
    }
    public void setPhotoPeriod2(String PP2){
        P1DT = PP2;
    }
    public void setCanopyHt(String CH){
        CanopyHt = CH;
    }
    public void setRateRange(List <String> range){
        List <String> newList = new ArrayList(range);
        rateRange.clear();
        rateRange.addAll(newList);
    }

    public void setSoilMoistureRange(List <String> range){
        List <String> newList = new ArrayList(range);
        soilMoistureRange.clear();
        soilMoistureRange.addAll(newList);
    }

    public void setDepthRange(List <String> range){
        List <String> newList = new ArrayList(range);
        depthRange.clear();
        depthRange.addAll(newList);
    }
    public void setMaxCanopyHtRange(List <String> range){
        List <String> newList = new ArrayList(range);
        maxCanopyHtRange.clear();
        maxCanopyHtRange.addAll(newList);
    }
    public void setSimulatePeriodMoisture(boolean sim){
        simulatePeriodMoisture = sim;
    }
    public void setWeatherFileRanges(List <String> range){
        List <String> newList = new ArrayList(range);
        WeatherFileRanges.add(newList);
    }
    public void setPhyllochron(String phyllochron){
        Phyllochron = phyllochron;
    }
    public void setOptimum(List <String> list){
        List <String> newList = new ArrayList(list);
        Optimum.clear();
        Optimum.addAll(newList);
    }
    public void setMedium(List <String> list){
        List <String> newList = new ArrayList(list);
        Medium.clear();
        Medium.addAll(newList);
    }
    public void setDry(List <String> list){
        List <String> newList = new ArrayList(list);
        Dry.clear();
        Dry.addAll(newList);
    }
    public void setPID(List <String> list){
        List <String> newList = new ArrayList(list);
        PID.clear();
        PID.addAll(newList);
    }
    public void setBaseTemp(String temp){
        baseTemp = temp;
    }
    public void setLOTemp(String temp){
        LowerOptimumTemp = temp;
        
       
    }
    public void setUOTemp(String temp){
        UpperOptimumTemp = temp;
    }
    public void setMaxTemp(String temp){
         MaximumTemp = temp;
    }
    public void setGDDLeaf(String gddLeaf){
        GddPerLeaf = gddLeaf;
    }
    public void setVernalization(List <String> vern){
        List <String> newList = new ArrayList(vern);
        VernalTypes.clear();
        VernalTypes.addAll(newList);
    }
    public void setTableName(String name){
        tableName = name;
    }
    public void setCropLoaded(boolean load){
        cropLoaded = load;
    }
    
    
    /*
    *Getters for all private variables
    */
    public String getFileName(){
        return fileName;
    }
    public String getCropType(){
        return cropType;
    }
    public String getCropVariety(){
        return cropVariety;
    }
    public String getCountry(){
        return country;
    }
    public String getRegion (){
        return region;
    }
    public String getWeatherFileName (){
        return weatherFile;
    }
    public String getLocationOfWeatherFile(){
        return WeatherFileName;
    }
    public String getDate (){
        return piDate;
    }
    public String getSoilMoisture (){
        return piSoilMoisture;
    }
    public String getDepth (){
        return piDepth;
    }
    public String getRate (){
        return piRate;
    }
    public String getLatitude (){
        return piLatitude;
    }
    public String getMaxCanopyHt(){
        return piMaxCanopyHt;
    }
    public List getGrowthStagesHeaders(){
        return gsHeaders;
    }
    public List getGrowthStagesData(){
        return GrowthStagesData;
    }
    public List <Boolean> getGrowthStageSelection(){
        return gsSelections;
    }
    public String getBaseTemp(){
        return baseTemp;
    }
    public String getLowerOptimumTemp(){
        return LowerOptimumTemp;
    }
    public String getUpperOptimumTemp(){
        return UpperOptimumTemp;
    }
    public String getMaxTemp(){
        return MaximumTemp;
    }
    public String getGddMethod(){
        return gddMethod;
    }
    public String getGDD_PER_LEAF(){
        return GddPerLeaf;
    }
    public List <String> getVernalTypes(){
        return VernalTypes;
    }
    public String getPhotoPeriod1(){
        return P1D;
    }
    public String getPhotoPeriod2(){
        return P1DT;
    }
    public String getCanopyHt(){
        return CanopyHt;
    }
    public List <String> getOptimum(){
        return Optimum;
    }
    public List <String> getMedium(){
        return Medium;
    }
    public List <String> getDry(){
        return Dry;
    }
    public List <String> getPID(){
        return PID;
    }
    public List <String> getRateRange(){
        return rateRange;
    }
   
    public List <String> getSoilMoistureRange(){
        return soilMoistureRange;
    }

    public List <String> getDepthRange(){
        return depthRange;
    }

    public List <String> getMaxCanopyHtRange(){
        return maxCanopyHtRange;
    }
    public boolean getSimulatePeriodMoisture(){
        return simulatePeriodMoisture;
    }
 
    public List <List> getWeatherFileRanges(){
        return WeatherFileRanges;
    }
    public String getPhyllochron(){
        return Phyllochron;
    }
    public String getTableName(){
        return tableName;
    }
    public boolean getCropLoaded(){
        return cropLoaded;
    }
}
