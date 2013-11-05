/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.verlage.mrracer.comparator;

import de.janquadflieg.mrracer.opponents.Opponent2013;
import java.util.Comparator;

/**
 *
 * @author quad
 */
public class TimeToCrashComparator2013
implements Comparator<Opponent2013>{

    @Override
    public int compare(Opponent2013 o1, Opponent2013 o2){
        double f1 = o1.getTimeToCrash();
        double f2 = o2.getTimeToCrash();

        if(f1 < f2){
            return -1;

        } else if(f1 > f2){
            return 1;

        } else {
            return 0;
        }
    }
}
