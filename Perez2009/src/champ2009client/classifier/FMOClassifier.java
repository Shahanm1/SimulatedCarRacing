/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.classifier;

import champ2009client.DiegoFMOClient;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;

/**
 * This class is used to communicate controller data with WEKA files (used to generate ARFF files to create classifier.)
 * @author Diego
 */
public class FMOClassifier {

    private static ClassifierData _data[];
    private static String filenameIN;
    private static String filenameOUT;
    private static int trackMeters;
    
    private static String trackName;
    private static String controllerName;
    
    
    public static void readCSV()
    {
        try{
            BufferedReader br = new BufferedReader(new FileReader(filenameIN));   
         
            String line = br.readLine();
            while(line != null)
            {
                String[] line_data =  line.split(";");     
                int min = Integer.parseInt(line_data[0]);  
                int max = Integer.parseInt(line_data[1]);
                //char dataclass = line_data[2].charAt(0);
                String dataclass = line_data[2];
                for(int i = min; i < max; i++)
                {
                    _data[i] = new ClassifierData();
                    _data[i].mClass = dataclass;                    
                }   
                line = br.readLine();
            }    
            br.close();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static String toARFFformat(double value)
    {
        DecimalFormat myF = new DecimalFormat("0.00");
        String valSt = myF.format(value);
        return valSt.replace(',', '.');
    }
    
    public static void printARFFRaw()
    {
        try{
            
            PrintWriter bw = new PrintWriter(new FileOutputStream(filenameOUT));
            StringBuffer line;
            
            //Header
            DateFormat df = DateFormat.getInstance();
            java.util.Date now = new java.util.Date();
            String dateSt = df.format(now);
            bw.println("% DATE: " + dateSt);
            bw.println("% TRACK: " + trackName);
            bw.println("% CONTROLLER: " + controllerName);
            bw.println("%");
            
            //Relation and 
            String relationName = trackName + dateSt.replace(' ', '_').replace('/','_');
            bw.println("@RELATION " + relationName);
            bw.println("@ATTRIBUTE angleAxis NUMERIC");
            bw.println("@ATTRIBUTE trackPos  NUMERIC");
            for(int i = 0; i < 19; i++)
            {
                bw.println("@ATTRIBUTE track_" + i + " NUMERIC");
            }
            //bw.println("@ATTRIBUTE class  {R,P,C}");
            bw.println("@ATTRIBUTE class  {R,PI,PD,CI,CD}");
            
            //Data
            //DecimalFormat myF = new DecimalFormat("0.00");
            bw.println("@DATA");
            for(int m = 0; m < trackMeters; m++)
            {
                line = new StringBuffer();
                if(_data[m].valid)
                {
                    /*line.append( myF.format(_data[m].angleAxis) + ",");
                    line.append( myF.format(_data[m].trackPosition) + ",");
                    for(int i = 0; i < 19; i++) 
                    {
                        line.append( myF.format(_data[m].trackSensors[i]) + ",");
                    } */   
                    line.append( toARFFformat(_data[m].angleAxis)  + ",");
                    line.append( toARFFformat(_data[m].trackPosition) + ",");
                    for(int i = 0; i < 19; i++) 
                    {
                        line.append( toARFFformat(_data[m].trackSensors[i]) + ",");
                    } 
                    
                    line.append( _data[m].mClass );

                    bw.println(line.toString());
                }
            }
            
            bw.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    
    public static void printARFFRawTrack()
    {
        try{
            
            PrintWriter bw = new PrintWriter(new FileOutputStream(filenameOUT));
            StringBuffer line;
            
            //Header
            DateFormat df = DateFormat.getInstance();
            java.util.Date now = new java.util.Date();
            String dateSt = df.format(now);
            bw.println("% DATE: " + dateSt);
            bw.println("% TRACK: " + trackName);
            bw.println("% CONTROLLER: " + controllerName);
            bw.println("%");
            
            //Relation and 
            String relationName = trackName + dateSt.replace(' ', '_').replace('/','_');
            bw.println("@RELATION " + relationName);
            bw.println("@ATTRIBUTE angleAxis NUMERIC");
            bw.println("@ATTRIBUTE trackPos  NUMERIC");
            for(int i = 0; i < 19; i++)
            {
                bw.println("@ATTRIBUTE track_" + i + " NUMERIC");
            }
            bw.println("@ATTRIBUTE class  {R,P,C}");
            
            //Data
            //DecimalFormat myF = new DecimalFormat("0.00");
            bw.println("@DATA");
            for(int m = 0; m < trackMeters; m++)
            {
                line = new StringBuffer();
                if(_data[m].valid)
                {
                    line.append( toARFFformat(_data[m].angleAxis)  + ",");
                    line.append( toARFFformat(_data[m].trackPosition) + ",");
                    for(int i = 0; i < 19; i++) 
                    {
                        line.append( toARFFformat(_data[m].trackSensors[i]) + ",");
                    } 
                    
                    line.append( _data[m].mClass );

                    bw.println(line.toString());
                }
            }
            
            bw.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public static void printARFFTreatedDiff()
    {
        try{
            
            PrintWriter bw = new PrintWriter(new FileOutputStream(filenameOUT));
            StringBuffer line;
            
            //Header
            DateFormat df = DateFormat.getInstance();
            java.util.Date now = new java.util.Date();
            String dateSt = df.format(now);
            bw.println("% DATE: " + dateSt);
            bw.println("% TRACK: " + trackName);
            bw.println("% CONTROLLER: " + controllerName);
            bw.println("%");
            
            //Relation and 
            String relationName = trackName + dateSt.replace(' ', '_').replace('/','_');
            bw.println("@RELATION " + relationName);
            bw.println("@ATTRIBUTE angleAxis NUMERIC");
            bw.println("@ATTRIBUTE trackPos  NUMERIC");
            for(int i = 0; i < 10; i++)
            {
                int left = i;
                int right = 18 - i;
                if(left == right)
                {
                    bw.println("@ATTRIBUTE track_9 NUMERIC");    
                }else
                {
                    bw.println("@ATTRIBUTE track_" + left + "_" + right + " NUMERIC");    
                }
                
            }
            bw.println("@ATTRIBUTE class  {R,P,C}");
            
            //Data
            //DecimalFormat myF = new DecimalFormat("0.00");
            bw.println("@DATA");
            for(int m = 0; m < trackMeters; m++)
            {
                line = new StringBuffer();
                if(_data[m].valid)
                {
                    line.append( toARFFformat(_data[m].angleAxis)  + ",");
                    line.append( toARFFformat(_data[m].trackPosition) + ",");
                    
                    for(int i = 0; i < 10; i++) 
                    {
                        int left = i;
                        int right = 18 - i;
                        if(left == right)
                        {   
                            line.append( toARFFformat(_data[m].trackSensors[i]) + ",");
                        }
                        else
                        {
                            line.append( toARFFformat(Math.abs(_data[m].trackSensors[left] - _data[m].trackSensors[right])) + ",");   
                        }
                    }

                    line.append( _data[m].mClass );
                    
                    bw.println(line.toString());
                }
            }
            
            bw.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public static void printARFFTreatedDiff2()
    {
        try{
            
            PrintWriter bw = new PrintWriter(new FileOutputStream(filenameOUT));
            StringBuffer line;
            
            //Header
            DateFormat df = DateFormat.getInstance();
            java.util.Date now = new java.util.Date();
            String dateSt = df.format(now);
            bw.println("% DATE: " + dateSt);
            bw.println("% TRACK: " + trackName);
            bw.println("% CONTROLLER: " + controllerName);
            bw.println("%");
            
            //Relation and 
            String relationName = trackName + dateSt.replace(' ', '_').replace('/','_');
            bw.println("@RELATION " + relationName);
            
            //bw.println("@ATTRIBUTE angleAxis NUMERIC");
            //bw.println("@ATTRIBUTE trackPos  NUMERIC");
            bw.println("@ATTRIBUTE track_0 NUMERIC");    
            bw.println("@ATTRIBUTE track_5 NUMERIC");    
            bw.println("@ATTRIBUTE track_8 NUMERIC");    
            bw.println("@ATTRIBUTE track_9 NUMERIC");    
            bw.println("@ATTRIBUTE track_10 NUMERIC");    
            bw.println("@ATTRIBUTE track_13 NUMERIC");    
            bw.println("@ATTRIBUTE track_18 NUMERIC");    
            
            bw.println("@ATTRIBUTE class  {R,P,C}");
            
            //Data
            //DecimalFormat myF = new DecimalFormat("0.00");
            bw.println("@DATA");
            for(int m = 0; m < trackMeters; m++)
            {
                line = new StringBuffer();
                if(_data[m].valid)
                {
              //      line.append( toARFFformat(_data[m].angleAxis)  + ",");
//                    line.append( toARFFformat(_data[m].trackPosition) + ",");
                    line.append( toARFFformat(_data[m].trackSensors[0]) + ",");
                    line.append( toARFFformat(_data[m].trackSensors[5]) + ",");
                    line.append( toARFFformat(_data[m].trackSensors[8]) + ",");
                    line.append( toARFFformat(_data[m].trackSensors[9]) + ",");
                    line.append( toARFFformat(_data[m].trackSensors[10]) + ",");
                    line.append( toARFFformat(_data[m].trackSensors[13]) + ",");
                    line.append( toARFFformat(_data[m].trackSensors[18]) + ",");
                    line.append( _data[m].mClass );
                    
                    bw.println(line.toString());
                }
            }
            
            bw.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    
    public static void setData(int meters, double[] tSensors, double angle, double trackPos)
    {
        if( (_data[meters].trackPosition == -100.0f) && tSensors[0] != -1.0f)
        {
            _data[meters].angleAxis = angle;
            _data[meters].trackPosition = trackPos;
            _data[meters].trackSensors = tSensors; 
            _data[meters].valid = true;                       
            System.out.println(_data[meters].mClass);
        }
    }
    
    public static void main(String args[])
    {
        if(args.length != 2)
        {
            System.out.println("FORMAT: java FMOClassifier trackNameFile meters");
        }else
        {
           // FMOClassifier cl = new FMOClassifier(args[0], Integer.parseInt(args[1]));   
           // cl.readCSV();
            trackMeters = Integer.parseInt(args[1]);
            FMOClassifier._data = new ClassifierData[trackMeters];
            for(int i = 0; i < trackMeters; i++)
            {
                FMOClassifier._data[i] = new ClassifierData();
            }
            
            System.out.println(args[0]);
            filenameIN = args[0] + ".csv";
            filenameOUT = args[0] + ".arff";
            trackName = args[0];
            
            FMOClassifier.readCSV();
            
            String controllerArgs[] = new String[1];
            controllerName = controllerArgs[0] = "champ2009client.DiegoController";
            
            DiegoFMOClient dfmo = new DiegoFMOClient();
            //dfmo.go(controllerArgs);
            dfmo.go();
            
        }
    }
    
}
