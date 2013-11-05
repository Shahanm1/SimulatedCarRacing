/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.opponents;

import static de.janquadflieg.mrracer.data.CarConstants.*;
import de.janquadflieg.mrracer.track.TrackSegment;

import java.awt.geom.*;
import java.util.*;

/**
 *
 * @author Verlage
 *
 * New in 2013: Saves distance on neighbour sensors, too First tests with
 * sensors +-1
 *
 */
public class Opponent2013 {

    private int id = 0;
    private boolean active = false;
    /**
     * Do i need to block this opponent?
     */
    private boolean needToBlock = false;
    /**
     * Ahead of me?
     */
    private boolean ahead = false;
    /**
     * This car is responsable for behaviour
     */
    private boolean responsable = false;
    /**
     * Angle of the sensor belonging to this Opponent in radiants.
     */
    private double angleR = 0.0;
    /**
     * criticle Oppo?
     */
    private boolean critical = false;
    /**
     * List of last positions.
     */
    private ArrayList<Point2D> positions = new ArrayList<>();
    /**
     * List of last Speedvectors
     */
    private ArrayList<Point2D> lastSpedVecs = new ArrayList<>();
    /**
     * Number of positions used to calculate the relative speed vector.
     */
    private static final int NUM_POSITIONS = 4;
    /**
     * Time in seconds between the positions.
     */
    private static final double DELTA_TIME = 0.02;
    /**
     * Speed difference vector in m/s.
     */
    private Point2D speedDiffVec = new Point2D.Double();
    /**
     * Position.
     */
    private Point2D position = new Point2D.Double();
    /**
     * Time in seconds until we hit this car, thats the important information!
     */
    private double timeToCrash = 0.0;
    /**
     * Time in seconds until we reach the minimum save distance, thats the
     * important information!
     */
    private double timeToMinDistance = 0.0;
    /**
     * Rectangle of this car.
     */
    private Rectangle2D rectangle = new Rectangle2D.Double();
    private int nummer = 0;
    private boolean TEXTDEBUG = false;
    //Time on which Opponent was last seen
    private int lastseen;
    /**
     * Saves last 10 distances and its angle X = Distance Y = sensorid
     */
    private ArrayList<Point2D> allDistances = new ArrayList<>();
    /*
     * Distance to Trackend or next Oppo
     * if distance is distance to trackend then oppo=null
     * else oppo= opponent, which has that distance
     */
    private double leftGap = 0.0;
    private double rightGap = 0.0;
    private Opponent2013 leftOppo;
    private Opponent2013 rightOppo;
    /**
     * Observer.
     */
    private OpponentObserver observer;

    public Opponent2013(int id, OpponentObserver o, int nummer, int lastseen) {
        this.id = id;
        this.observer = o;
        this.angleR = Math.toRadians(id * 10.0);
        this.nummer = nummer;
        this.lastseen = lastseen;

        //System.out.println("Neuer Gegner "+nummer+"\n ID: "+id+" ;");
    }

    public double getLeftGap() {
        return leftGap;
    }

    public void setLeftGap(double leftgap) {
        this.leftGap = leftgap;
    }

    public double getRightGap() {
        return rightGap;
    }

    public void setRightGap(double rightgap) {
        this.rightGap = rightgap;
    }

    public int getLastSeen() {
        return lastseen;
    }

    public Opponent2013 getLeftOppo() {
        return leftOppo;
    }

    public void setLeftOppo(Opponent2013 leftOppo) {
        this.leftOppo = leftOppo;
    }

    public Opponent2013 getRightOppo() {
        return rightOppo;
    }

    public void setRightOppo(Opponent2013 rightOppo) {
        this.rightOppo = rightOppo;
    }

    public void resetLeftGap() {
        leftOppo = null;
    }

    public void resetRightGap() {
        rightOppo = null;
    }

    public boolean isResponsable() {
        return responsable;
    }

    public void setResponsable(boolean responsable) {
        this.responsable = responsable;
    }

    /*
     * returns last angle Id on which car was seen
     */
    public int getID() {
        return id;
    }

    public Point2D getPosition() {
        return position;
    }

    public Rectangle2D getRectangle() {
        return rectangle;
    }

    public Point2D getSpeedDiffVec() {
        return speedDiffVec;
    }

    public double getTimeToCrash() {
        return timeToCrash;
    }

    public double getTimeToMinDistance() {
        return timeToMinDistance;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAhead() {
        return ahead;
    }

    public boolean shouldBeBlocked() {
        return needToBlock;
    }

    public void reset(double d, int sensorid, int lastseen) {
        active = false;
        needToBlock = false;
        ahead = false;
        positions.clear();
        lastSpedVecs.clear();
        speedDiffVec.setLocation(0.0, 0.0);
        position.setLocation(0.0, 0.0);
        timeToCrash = 0.0;
        timeToMinDistance = 0.0;
        allDistances.clear();
        lastseen = 0;
        
        setSensorValue(d,sensorid,lastseen);

    }

    public void setSensorValue(double d, int sensorid, int lastseen) {

        int x = positions.size();
        int lastsensorid = id;
        id = sensorid;
        int verschiebung = 0;
        responsable = false;

        //smoother oppotracking if opponent switch between two sensors
        if (lastsensorid > id) {
            verschiebung = 5;
        } else if (id > lastsensorid) {
            verschiebung = -5;
        }
        if (id == 0 && lastsensorid == 35) {
            verschiebung = -5;
        }
        if (id == 35 && lastsensorid == 0) {
            verschiebung = 5;
        }

        this.angleR = Math.toRadians((id * 10.0) + 5.0 + verschiebung);

        this.lastseen = lastseen;
        Double hilfe = new Double(sensorid);
        allDistances.add(new Point2D.Double(d, hilfe));

        Point2D pos = new Point2D.Double(-Math.sin(angleR) * d,
                -Math.cos(angleR) * d);

        positions.add(pos);


        if (positions.size() > NUM_POSITIONS) {
            positions.remove(0);
            allDistances.remove(0);
        }

        // if (TEXTDEBUG) System.out.println("Anzahl positions/allDistances ("+positions.size()+"/"+allDistances.size()+")");
        if (positions.size() < NUM_POSITIONS) {
            // System.out.println("Gegnernummer: "+nummer+" ;Sensor: "+sensorid+" ; Distance: "+d+" ;Pos: "+pos);
            position = pos;
            return;
        }

        active = true;

        Point2D avgLastPosition = new Point2D.Double();
        Point2D avgPosition = new Point2D.Double();
        /*for (int i = 0; i < NUM_POSITIONS / 2; ++i) {
         avgLastPosition.setLocation(
         avgLastPosition.getX() + positions.get(i).getX(),
         avgLastPosition.getY() + positions.get(i).getY());
         }
         avgLastPosition.setLocation(
         avgLastPosition.getX() / (NUM_POSITIONS / 2),
         avgLastPosition.getY() / (NUM_POSITIONS / 2));

         for (int i = NUM_POSITIONS / 2; i < positions.size(); ++i) {
         avgPosition.setLocation(
         avgPosition.getX() + positions.get(i).getX(),
         avgPosition.getY() + positions.get(i).getY());
         }
         avgPosition.setLocation(
         avgPosition.getX() / (NUM_POSITIONS / 2),
         avgPosition.getY() / (NUM_POSITIONS / 2));

         speedDiffVec.setLocation(
         (avgPosition.getX()-avgLastPosition.getX())/(DELTA_TIME*(NUM_POSITIONS / 2)),
         (avgPosition.getY()-avgLastPosition.getY())/(DELTA_TIME*(NUM_POSITIONS / 2)));*/
        //System.out.println("---------------------------------------------------------");
        //System.out.println("Gegner "+nummer+":");
        for (int i = 0; i < NUM_POSITIONS - 1; ++i) {
            avgLastPosition.setLocation(
                    avgLastPosition.getX() + positions.get(i).getX(),
                    avgLastPosition.getY() + positions.get(i).getY());
            //System.out.println("AVGLAST YPos["+i+"] : "+positions.get(i).getY());
        }
        avgLastPosition.setLocation(
                avgLastPosition.getX() / (NUM_POSITIONS - 1),
                avgLastPosition.getY() / (NUM_POSITIONS - 1));

        for (int i = 1; i < positions.size(); ++i) {
            avgPosition.setLocation(
                    avgPosition.getX() + positions.get(i).getX(),
                    avgPosition.getY() + positions.get(i).getY());
          //  System.out.println("AVG YPos["+i+"] : "+positions.get(i).getY());
        }
        avgPosition.setLocation(
                avgPosition.getX() / (NUM_POSITIONS - 1),
                avgPosition.getY() / (NUM_POSITIONS - 1));

        speedDiffVec.setLocation(
                (avgPosition.getX() - avgLastPosition.getX()) / (DELTA_TIME),
                (avgPosition.getY() - avgLastPosition.getY()) / (DELTA_TIME));
        //System.out.println("speedDiffVec: "+ (avgPosition.getY() - avgLastPosition.getY()) / (DELTA_TIME));
       // System.out.println("-----------------------------E N D E ----------------------------------");
        position.setLocation(avgPosition);

        critical = false;
        leftGap = 0.0;
        rightGap = 0.0;
        rightOppo = null;
        leftOppo = null;
        responsable = false;
        needToBlock = false;

         /**
         * Look at last Speedvecs Only switch from positive to negativ DiffVec
         * if the two last values are negativ, too
         */
        
        int j = lastSpedVecs.size();
        if (j > NUM_POSITIONS) {
            lastSpedVecs.remove(0);
        }
        j = lastSpedVecs.size();
        if (lastSpedVecs.size() > 1) {
            if (speedDiffVec.getY() < 0) {
                if (lastSpedVecs.get(j - 1).getY() < 0 && lastSpedVecs.get(j - 2).getY() < 0) {
                    lastSpedVecs.add(speedDiffVec);
                } else {
                    Point2D.Double speedVecErsatz = new Point2D.Double(speedDiffVec.getX(), 1.0);
                    speedDiffVec = speedVecErsatz;
                }

            } else {
                lastSpedVecs.add(speedDiffVec);
            }
        } else {
            lastSpedVecs.add(speedDiffVec);
        }
        
        
        
        // wo sind die alle?        
        // hinter mir und langsamer -> nicht wichtig!
        if (position.getY() < 0 && speedDiffVec.getY() < 0) {
            active = false;
            ahead = false;
        }
        // vor mir und schneller
        if (position.getY() > 0 && speedDiffVec.getY() > 0) {
            active = false;
            ahead = true;
        }
        // vor mir und langsamer -> overtake!
        if (position.getY() > 0 && speedDiffVec.getY() < 0) {
            ahead = true;
            active = true;
        }
        // hinter mir und schneller koennte man ja blocken
        if (position.getY() < 0 && speedDiffVec.getY() > 0) {
            needToBlock = true;
            active = true;
            ahead = false;
        }

        // Triangle in Front of car for active Cars
        if (position.getY() > 0 && active) {

            if (position.getX() >= 0) {
                if (position.getX() * 1.8 <= position.getY()) {
                } else {
                    active = false;
                }

            } else {
                if (Math.abs(position.getX() * 1.8) <= position.getY()) {
                } else {
                    active = false;
                }
            }
        }
       
        


        //Workaround for Speedbug
        if ((Math.abs(speedDiffVec.getX()) > 100.0) || (Math.abs(speedDiffVec.getY()) > 340)) {
            reset(d, sensorid,lastseen);
        }




        //Do not block Oppos which are eventualy in Corner
        TrackSegment mySegment = observer.getTrackModel().getSegment(observer.getData().getDistanceFromStartLine());
        TrackSegment hisSegment = observer.getTrackModel().getSegment(observer.getData().getDistanceFromStartLine() - d);
        if (!hisSegment.equals(mySegment)) {
            needToBlock = false;
        }



        rectangle.setRect(position.getX() - (CAR_WIDTH * 0.5),
                position.getY() + (CAR_LENGTH * 0.5),
                CAR_WIDTH, CAR_LENGTH);

        timeToCrash = position.getY() / speedDiffVec.getY() * -1.0;
        timeToMinDistance = (position.getY() - (observer.getMinDistance().getY() + CAR_LENGTH)) / speedDiffVec.getY() * -1.0;
    }

    /*
     * return true if given distance and angle is part of sensorDisId
     */
    public boolean findValue(Double d, int sensorid) {

        //if((d<=200.0)&&(TEXTDEBUG)) System.out.println("Suche Distance "+d+" ;ID: +"+sensorid+ "Alldistance.size()= "+allDistances.size());
        for (int i = 0; i < allDistances.size(); i++) {
            Double dis = new Double(allDistances.get(i).getX());
            Double senID = new Double(sensorid);
            Double winkel = new Double(allDistances.get(i).getY());
            //  if (TEXTDEBUG)System.out.println("findValue "+d+" / "+sensorid+"   [Vorhanden: "+dis+"/ID: "+winkel+"]");
            if ((dis.equals(d))
                    && (winkel.equals(senID))) {
                //  if (TEXTDEBUG)System.out.println("Finde Distance "+d+" ;ID: +"+ sensorid +" : gefunden?"+true);
                return true;
            }
        }
        return false;
    }

    public int getNumber() {
        return nummer;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean crit) {
        this.critical = crit;
    }
}
