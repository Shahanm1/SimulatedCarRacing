/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

/**
 * 
 * @author Jan Quadflieg
 */
public class Utils {
    /** NumberFormat to convert double values to strings. */
    private final static DecimalFormat FORMAT = new DecimalFormat("#,##0.00;#,##0.00");

    /** A constant indicating that there is no valid data. */
    public static final double NO_DATA_D = Double.NEGATIVE_INFINITY;

    /** A constant indicating that there is no valid data. */
    public static final int NO_DATA_I = Integer.MIN_VALUE;

    /** A constant indicating that there is no valid data. */
    public static final long NO_DATA_L = Long.MIN_VALUE;

    public static String timeToString(double d){
        if(d == NO_DATA_D){
            return "No Data";

        } else {
            int minutes = ((int)d) / 60;
            int seconds = ((int)d) - (minutes*60);

            String result = "";
            if(minutes < 10){
                result += "0";
            }
            result += String.valueOf(minutes)+":";
            if(seconds < 10){
                result += "0";
            }
            result += String.valueOf(seconds);

            return result;
        }
    }

    public static String timeToExactString(double d){
        if(d == NO_DATA_D){
            return "No Data";

        } else {
            int minutes = ((int)d) / 60;
            int seconds = ((int)d) - (minutes*60);
            int hundreds = (int) ((d - ((int)d)) * 100.0);
            String result = String.valueOf(minutes)+":";
            if(seconds < 10){
                result += "0";
            }
            result += String.valueOf(seconds)+".";
            if(hundreds < 10){
                result += "0";
            }
            result += String.valueOf(hundreds);

            return result;
            //return FORMAT.format(d);
        }
    }

    public static String dTS(double d){
        if(d == NO_DATA_D){
            return "No Data";

        } else {
            return FORMAT.format(d);
        }
    }

    public static String iTS(int i){
        if(i == NO_DATA_I){
            return "No Data";

        } else {
            return String.valueOf(i);
        }
    }

    public static boolean createPath(String path)
    throws Exception{
        File f = new File(path);

        return f.mkdirs();
    }

    /**
     * Method to check if a given number is even.
     *
     * @ return true if i is an even number.
     */
    public static boolean even(int i){
        return (i & 1) == 0;
    }

    /**
     * Method to check if a given number is odd.
     *
     * @ return true if i is an odd number.
     */
    public static boolean odd(int i){
        return (i & 1) != 0;
    }

    public static String list(Properties p, String delim){
        StringBuilder result = new StringBuilder();
        Set<String> set = p.stringPropertyNames();
        ArrayList<String> entries = new ArrayList<>(set);
        Collections.sort(entries);
        for(int i=0; i < entries.size(); ++i){
            result.append(entries.get(i)).append("=").append(p.getProperty(entries.get(i))).append(delim);
        }
        return result.toString();
    }   

    //assure that value lies in [min,max], cut higher/lower values
    public static double truncate(final double value, final double min, final double max){
        return Math.min(Math.max(value, min), max);
    }

    //assure that value lies in [min,max], cut higher/lower values
    public static int truncate(final int value, final int min, final int max){
        return Math.min(Math.max(value, min), max);
    }

    public static String toSHA1(String input){
        try{
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA1");
            md.update(input.getBytes("UTF-8"));
            byte[] hash = md.digest();

            StringBuilder result = new StringBuilder();
            for(int i=0; i < hash.length; ++i){
                result.append(Integer.toString((hash[i] & 0xff ) + 0x100, 16).substring( 1 ));
            }

            return result.toString();

        } catch(Exception e){
            e.printStackTrace(System.out);
            return "";
        }
    }

    public static void main(String[] args){
        //System.out.println(Utils.timeToExactString(127.20));
        //java.util.Random r = new java.util.Random(System.currentTimeMillis());
        //for(int i=0; i < 20; ++i){
        //    int num = r.nextInt(67);
        //    System.out.println((1+i)+" - "+num);
        //}

        /*String salt = "7*X@L\"N:JI";
        String name = "jan".trim().toLowerCase();
        String data = "1:56.79-9Punkte";
        String hash = toSHA1(data+name+salt);

        System.out.println(hash);*/
    }
}
