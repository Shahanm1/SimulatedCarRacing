/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.opponents;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import static de.janquadflieg.mrracer.data.CarConstants.*;

/**
 *
 * @author Verlage
 */
public class OppoTracking {

    private ArrayList<Double[]> sensorlist = new ArrayList<Double[]>();
    //Saves how many cars are in range;
    private int oppoid = 0;
    private OpponentObserver observer;
    private boolean TEXTDEBUG = false;
    // private gameticks - not equal to real gameticks
    private int gameticks = 0;
    //List of all Cars in Range
    private ArrayList<Opponent2013> gegner = new ArrayList<Opponent2013>();
    private double rightend;
    private double leftend;
    
    //List of all activ Cars in Range
    private ArrayList<Opponent2013> gegnerActive = new ArrayList<Opponent2013>();
    //List of all activ Cars ahead
    private ArrayList<Opponent2013> gegnerActiveAhead = new ArrayList<Opponent2013>();
     //List of all blockableCars in Range
    private ArrayList<Opponent2013> gegnerBlockable = new ArrayList<Opponent2013>();
    //List of all Cars ahead(includes non active ones!
     private ArrayList<Opponent2013> gegnerAhead = new ArrayList<Opponent2013>();

    public OppoTracking(OpponentObserver o) {
        observer = o;
    }

    public void addSensorValues(Double[] values, double left, double right) {
        if (sensorlist.size() >= 10) {
            sensorlist.remove(0);
        }
        sensorlist.add(values);
        gameticks++;
        findOppos();
        rightend = right;
        leftend = left;
    }

    /* Looks for Opponents in new sensor data and compares it to old sensorvalues
     * status: Just tracks if car switch from sensor x to x+1/x-1 without invisibility
     * 
     * 
     * Info: Gegner verschwinden manchmal im "toten Winkel" -> nicht betrachtet
     */
    private void findOppos() {
        int vor = 0;
        int nach = 0;
        int gegnerID = -1;
        boolean found = false;

        if (sensorlist.size() < 3) {
            return;
        }

        for (int i = 0; i <= 35; i++) {
            if (i == 0) {
                vor = 35;
                nach = 1;
            } else if (i == 35) {
                vor = 34;
                nach = 0;
            } else {
                vor = i - 1;
                nach = i + 1;
            }
            Double aktuell = sensorlist.get(sensorlist.size() - 1)[i];
            Double aktuellvor = sensorlist.get(sensorlist.size() - 1)[vor];
            Double aktuellnach = sensorlist.get(sensorlist.size() - 1)[nach];

            if (aktuell <= 190.0) {

                // if (TEXTDEBUG) System.out.println("Aktuell= "+aktuell+" Sensor: "+i);
                Double old = sensorlist.get(sensorlist.size() - 2)[i];
                Double oldvor = sensorlist.get(sensorlist.size() - 2)[vor];
                Double oldnach = sensorlist.get(sensorlist.size() - 2)[nach];

                found = false;
                if ((old * 1.30 >= aktuell) && (old * 0.70 <= aktuell)) {
                    gegnerID = findOppowithDistance(old, i);
                    //System.out.println("Fund Selber Sensor "+gegnerID);
                    found = true;
                } else if ((oldvor * 1.30 >= aktuell) && (oldvor * 0.70 <= aktuell)) {
                    if (aktuellvor > 190){
                    gegnerID = findOppowithDistance(oldvor, vor);
                    //System.out.println("Fund VOR Sensor "+gegnerID);
                    found = true;
                    }
                } else if ((oldnach * 1.30 >= aktuell) && (oldnach * 0.70 <= aktuell)) {
                    if (aktuellnach > 190){
                    gegnerID = findOppowithDistance(oldnach, nach);
                    // System.out.println("Fund NACH Sensor "+gegnerID);
                    found = true;
                    }
                }

                if (found) {
                    if (gegnerID == -1) {
                        gegner.add(new Opponent2013(i, observer, oppoid, gameticks));
                        gegner.get(gegner.size() - 1).setSensorValue(aktuell, i, gameticks);
                        oppoid++;
                        found = false;
                    } else {
                        gegner.get(gegnerID).setSensorValue(aktuell, i, gameticks);
                    }
                    found = false;
                } else {
                    if (TEXTDEBUG) {
                        System.out.println("Erstelle neuen Gegner :)");
                    }
                    gegner.add(new Opponent2013(i, observer, oppoid, gameticks));
                    gegner.get(gegner.size() - 1).setSensorValue(aktuell, i, gameticks);
                    oppoid++;
                }
            } else {
                //
            }
        }
        gegnerActive.clear();
        gegnerBlockable.clear();
        gegnerActiveAhead.clear();
        gegnerAhead.clear();
        
        for (int i = 0; i < gegner.size(); i++) {
            int lasttime = gegner.get(i).getLastSeen();
            
            if ((gameticks - lasttime) > 2) {
                gegner.remove(i);
                
               }
               
        }
        for (int i = 0; i < gegner.size(); i++) {
           if (gegner.get(i).isActive()){
                    gegnerActive.add(gegner.get(i));
                   // System.out.println("      Aktiv: "+i+" : Nummer: "+gegner.get(i).getNumber()+"]" );
                    
                    if (gegner.get(i).isAhead()) gegnerActiveAhead.add(gegner.get(i));
                }
                if (gegner.get(i).shouldBeBlocked()){
                    gegnerBlockable.add(gegner.get(i));
                    //System.out.println("      Block: "+i+" : Nummer: "+gegner.get(i).getNumber()+"]" );
                }
                if (gegner.get(i).isAhead()) gegnerAhead.add(gegner.get(i)); 
            
        }

        //Calculate Gaps for all activ opponents 
        for (int i = 0; i < gegnerActive.size(); i++) {
            double yCar = gegnerActive.get(i).getPosition().getY();
            if (gegnerActive.get(i).isAhead() && yCar > 1.0) {
                //First set left and right gap to max value(which is distance to track end(x cordinates)
                if (gegnerActive.get(i).getPosition().getX() < 0) {
                    gegnerActive.get(i).setLeftGap(leftend - Math.abs(gegnerActive.get(i).getPosition().getX()));
                    gegnerActive.get(i).setRightGap(rightend + Math.abs(gegnerActive.get(i).getPosition().getX()));
                } else {
                    gegnerActive.get(i).setLeftGap(leftend + gegnerActive.get(i).getPosition().getX());
                    gegnerActive.get(i).setRightGap(rightend - gegnerActive.get(i).getPosition().getX());
                }
                //look for all others if they have similar(+/- (Car_Length/2)+Epsilon) y value 
                 boolean foundOppoOnYLeft = false;
                 boolean foundOppoOnYRight = false;
                for (int j = 0; j < gegnerActive.size(); j++) {
                    double yCarOther = gegnerActive.get(j).getPosition().getY();
                   
                    if (/**
                             * gegner.get(j).isActive()&&
                             */
                            yCarOther > 1.0 && i != j) {
                        if (yCar + CAR_LENGTH / 2 + 0.5 >= yCarOther && yCar - CAR_LENGTH / 2 + 0.5 <= yCarOther) {
                            //System.out.println("Nachbar!!!! :) ");
                            double xCar = gegnerActive.get(i).getPosition().getX();
                            double xCarOther = gegnerActive.get(j).getPosition().getX();
                            double dist = 100;
                            if (xCar > xCarOther) {
                                //look at LEFTgap
                                dist = xCar - xCarOther;
                                if (dist < gegnerActive.get(i).getLeftGap()) {
                                    gegnerActive.get(i).setLeftGap(dist-CAR_WIDTH/2);
                                    gegnerActive.get(i).setLeftOppo(gegnerActive.get(j));
                                    foundOppoOnYLeft = true;
                                }

                            } else {
                                //look at RIGHTGAP
                                dist = xCarOther - xCar;
                                if (dist < gegnerActive.get(i).getRightGap()) {
                                    gegnerActive.get(i).setRightGap(dist-CAR_WIDTH/2);
                                    gegnerActive.get(i).setRightOppo(gegnerActive.get(j));
                                    foundOppoOnYRight = true;
                                }
                            }
                        }
                    }
                    if (!foundOppoOnYLeft) gegnerActive.get(i).resetLeftGap();
                    if (!foundOppoOnYRight)gegnerActive.get(i).resetRightGap();
                    
                }

            }
        }

    }

    public ArrayList<Opponent2013> getGegnerAhead() {
        return gegnerAhead;
    }

    /*
     * return number of opponent which was seen with given distance and sensor angle
     */
    private int findOppowithDistance(Double dis, int sensorid) {
        // if (TEXTDEBUG) System.out.println("Suche Oppo");
        for (int i = 0; i < gegner.size(); i++) {
            if (gegner.get(i).findValue(dis, sensorid)) {
                //  if (TEXTDEBUG)System.out.println("Matching Oppo");
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Opponent2013> getOpponents() {
        return gegner;
    }

    public Opponent2013[] getOpponentsAsArray() {
        int size = gegner.size();

        Opponent2013[] help = new Opponent2013[size];
        for (int i = 0; i < size; i++) {
            help[i] = gegner.get(i);
        }
        return help;
    }

    public String toString() {
        String hilf = new String();
        for (int x = 0; x < sensorlist.size(); x++) {
            hilf = hilf.concat("[");
            for (int y = 0; y < sensorlist.get(x).length; y++) {
                hilf = hilf.concat(sensorlist.get(x)[y] + "/");
            }

            hilf = hilf.concat("]\n");
        }
        return hilf;
    }

    public int getOppoId() {
        return oppoid;
    }

    public void reset() {
        sensorlist.clear();
        oppoid = 0;
        gegner.clear();
    }

    public int getGameticks() {
        return gameticks;
    }

    public ArrayList<Opponent2013> getBlockableOppos() {
        return gegnerBlockable;

    }

    public ArrayList<Opponent2013> getActiveOppos() {
      return gegnerActive;
    }

    public ArrayList<Opponent2013> getGegnerActiveAhead() {
        return gegnerActiveAhead;
    }
    
    
}
