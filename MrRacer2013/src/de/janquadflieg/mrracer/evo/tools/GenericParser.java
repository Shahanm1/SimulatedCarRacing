/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.evo.tools;

import java.util.*;
import java.text.*;

import de.janquadflieg.mrracer.PropertiesCreator;
import de.janquadflieg.mrracer.behaviour.*;
import de.janquadflieg.mrracer.controller.*;
import de.janquadflieg.mrracer.evo.Individual2011;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction;
import de.janquadflieg.mrracer.plan.Plan2011;

/**
 *
 * @author quad
 */
public class GenericParser
        implements IndividualParser {

    private ArrayList<String> params;
    private HashMap<String, String> map;

    public GenericParser(ArrayList<String> p, HashMap<String, String> map) {
        this.params = p;
        this.map = map;
    }

    public Individual2011 parse(String line, String track, java.util.HashMap<String, Integer> header) {
        Individual2011 result = new Individual2011();
        result.properties = new Properties();

        DecimalFormat nf = new DecimalFormat();
        DecimalFormatSymbols symbols = nf.getDecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        nf.setDecimalFormatSymbols(symbols);

        StringTokenizer tokenizer = new StringTokenizer(line, " \t");        

        ArrayList<String> data = new ArrayList<String>();

        while (tokenizer.hasMoreTokens()) {
            data.add(tokenizer.nextToken());
            //System.out.println(data.get(data.size() - 1));
        }

        // number        
        result.indNR = Integer.parseInt(data.get(header.get(map.get(PropertiesCreator.IND_NUMBER))));
        result.indID = result.indNR + 1;

        // fitness value        
        try {
            result.fitness.put(track, nf.parse(data.get(header.get(map.get(PropertiesCreator.IND_FITNESS)))).doubleValue());

        } catch (ParseException e) {
            e.printStackTrace(System.out);
        }

        for (int i = 0; i < params.size(); ++i) {
            String param = params.get(i);
            String identifier = map.get(param);
            int index = header.get(identifier);


            try {
                if (param.endsWith(GeneralisedLogisticFunction.GROWTH_RATE_B)) {
                    result.properties.put(param, String.valueOf(Math.pow(10, nf.parse(data.get(index)).doubleValue())));

                } else if(param.endsWith(OffTrackRecoveryBehaviour.F_ANGLE)){
                    result.properties.put(param, String.valueOf(nf.parse(data.get(index)).doubleValue() * 90.0));

                } else if(param.endsWith(OffTrackRecoveryBehaviour.B_ANGLE)){
                    result.properties.put(param, String.valueOf(nf.parse(data.get(index)).doubleValue() * -90.0));

                } else if(param.endsWith(Clutch.MS)){
                    result.properties.put(param, String.valueOf(nf.parse(data.get(index)).doubleValue() * 200.0));

                } else {
                    // simple double
                    result.properties.put(param, String.valueOf(nf.parse(data.get(index)).doubleValue()));
                }


            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }

        return result;
    }
}
