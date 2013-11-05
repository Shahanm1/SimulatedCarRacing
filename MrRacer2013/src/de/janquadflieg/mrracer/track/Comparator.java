/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.track;

import java.io.File;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.classification.Situations;

/**
 *
 * @author quad
 */
public class Comparator {

    public static void test(String[] files)
            throws Exception{

        TrackModel[] models = new TrackModel[files.length];

        for(int i=0; i < files.length; ++i){
            System.out.println("Loading model["+i+"]: "+"."+File.separator+files[i]);
            models[i] = TrackModel.load("."+File.separator+files[i]);
            System.out.println(models[i].getName()+" "+Utils.dTS(models[i].getLength())+"m "+models[i].size());
            System.out.println("");
        }

        for(int i=0; i < models.length; ++i){
            for(int j=0; j < models.length; ++j){
                System.out.print(Utils.dTS(distance(models[i], models[j]))+" ");
            }
            System.out.println("");
        }
    }

    public static double distance(TrackModel a, TrackModel b){
        double result = 0.0;

        for(double d = 0.0; d < a.getLength(); ++d){
            int t1 = a.getSegment(d).getSubSegment(d).getType();
            int t2 = b.getSegment(d).getSubSegment(d).getType();

            result += Situations.fiveTypesDistance(t1, t2);
        }

        result /= a.getLength()/1000.0;

        return result;
    }

    public static void main(String[] args){
        String[] noisy = {"mueda.saved_model", "mueda2.saved_model", "mueda3.saved_model"};
        String[] normal = {"mueda_nn.saved_model", "mueda2_nn.saved_model", "mueda3_nn.saved_model"};
        try{
            test(normal);
            
        } catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
}
