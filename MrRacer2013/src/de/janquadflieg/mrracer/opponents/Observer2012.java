/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.opponents;

import de.verlage.mrracer.comparator.RectangleXPositionComparator;
import de.verlage.mrracer.comparator.TimeToMinDistanceComparator;
import scr.Controller.Stage;


import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.classification.Situation;
import static de.janquadflieg.mrracer.data.CarConstants.*;
import de.janquadflieg.mrracer.gui.GraphicDebugable;
import de.janquadflieg.mrracer.plan.Plan2011;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.JPanel;

/**
 *
 * @author quad
 */
public class Observer2012
        implements OpponentObserver, GraphicDebugable{       

    /** Maximum distance of sensors accepted by the noise handler. */
    public static final double MAX_DISTANCE_NOISY = 175.0;
    /** The amount of straight needed to attempt an overtaking. */
    private static final double MIN_REMAINING_STRAIGHT = 50.0;
    // 156   160     3    16 -22514.8      470       13     0.16353  8.94641 6.56333 2.80090 10.01875 5.00000
    // 146   149     3    15 -22260.8      444       13     0.14398  7.06556 6.11940 3.62545  7.45413 4.61839
    /** Minimal distance to switch the position on the track by 1 meter. */
    private double minSwitchDistance = 5.0;
    /** Factor to increase the minimum switch distance according to the current speed. */
    private double switchIncreaseMaxFactor = 7.0;
    /** Minimum absolute distance in meter to other cars. */
    private Point2D minDistance = new Point2D.Double(1.0, 10.0);
    /** Increase width for the critical zone for corners. */
    private double criticalWidthIncrease = 4.0;
    /** Minimum lookahead for the critical zone. */
    private static final double MIN_CRITICAL_LOOKAHEAD = 10.0 + CAR_LENGTH;
    /** Maximum lookahead for the critical zone. */
    private static final double CRITICAL_LOOKAHEAD_INCREASE = MAX_DISTANCE_NOISY - MIN_CRITICAL_LOOKAHEAD;        
    /** Graphical debugging? */
    private boolean GRAPHICAL_DEBUG = false;
    /** Output debug text messages? */
    private static final boolean TEXT_DEBUG = false;
    /** Debug painter. */
    private DebugPainter debugPainter;
    /** TrackModel. */
    private TrackModel model;
    /** Current stage. */
    private Stage stage = Stage.RACE;
    /** Recommended point. */
    private Point2D point = OpponentObserver.NO_RECOMMENDED_POINT;
    /** Recommmended speed. */
    private double speed = OpponentObserver.NO_RECOMMENDED_SPEED;
    /** Number of active cars. */
    private int lastNumCars = -1;
    /** Opponents. */
    private Opponent[] opponents = new Opponent[36];
    /** String Identifier. */
    public static final String MIN_SWITCH_DISTANCE = "-OBSERVER.MIN_SWITCH_DISTANCE-";
    /** String Identifier. */
    public static final String SWITCH_INCREASE_MAX_FACTOR = "-OBSERVER.SWITCH_INCREASE_MAX_FACTOR-";
    /** String identifier. */
    public static final String MIN_DISTANCE_X = "-OBSERVER.MIN_DISTANCE_X-";
    /** String identifier. */
    public static final String MIN_DISTANCE_Y = "-OBSERVER.MIN_DISTANCE_Y-";
    /** String identifier. */
    public static final String CRITICAL_WIDTH_INCREASE = "-OBSERVER.CRITICAL_WIDTH_INCREASE-";

    public Observer2012() {
        if (GRAPHICAL_DEBUG) {
            debugPainter = new DebugPainter();
            debugPainter.setName("Observer2012");
        }
        for (int i = 0; i < opponents.length; ++i) {
            opponents[i] = new Opponent(i, this);
        }
        /*if (System.getProperties().containsKey("ObserverDebug")) {
            System.out.println("ObserverDebug");
            TEXT_DEBUG = true;
        }*/
    }    

    @Override
    public void setParameters(Properties params, String prefix){
        minSwitchDistance = Double.parseDouble(params.getProperty(prefix+MIN_SWITCH_DISTANCE, String.valueOf(minSwitchDistance)));
        switchIncreaseMaxFactor = Double.parseDouble(params.getProperty(prefix+SWITCH_INCREASE_MAX_FACTOR, String.valueOf(switchIncreaseMaxFactor)));
        minDistance.setLocation(Double.parseDouble(params.getProperty(prefix+MIN_DISTANCE_X, String.valueOf(minDistance.getX()))),
                Double.parseDouble(params.getProperty(prefix+MIN_DISTANCE_Y, String.valueOf(minDistance.getY()))));
        criticalWidthIncrease = Double.parseDouble(params.getProperty(prefix+CRITICAL_WIDTH_INCREASE, String.valueOf(criticalWidthIncrease)));
    }

    @Override
    public void getParameters(Properties params, String prefix){
        params.setProperty(prefix+MIN_SWITCH_DISTANCE, String.valueOf(minSwitchDistance));
        params.setProperty(prefix+SWITCH_INCREASE_MAX_FACTOR, String.valueOf(switchIncreaseMaxFactor));
        params.setProperty(prefix+MIN_DISTANCE_X, String.valueOf(minDistance.getX()));
        params.setProperty(prefix+MIN_DISTANCE_Y, String.valueOf(minDistance.getY()));
        params.setProperty(prefix+CRITICAL_WIDTH_INCREASE, String.valueOf(criticalWidthIncrease));
    }

    public void paint(String baseFileName, java.awt.Dimension d) {
        
    }
    
    @Override
    public TrackModel getTrackModel(){
        return null;
    }
     @Override
    public SensorData getData(){
        return null;
    }
    /**
     * Calculates the distance needed to switch the position on the track by delta meter.
     * @param delta
     * @return
     */
    public double calcSwitchDistance(SensorData data, double delta){
        return delta * (minSwitchDistance+(Math.max(0, Math.min(1, data.getSpeed()/300.0)) * switchIncreaseMaxFactor));
    }

    /**
     * Calculates the possible absolute change in trackposition.
     * @param data
     * @param length
     * @return
     */
    public double calcPossibleSwitchDelta(SensorData data, double length){
        return length / (minSwitchDistance+(Math.max(0, Math.min(1, data.getSpeed()/300.0)) * switchIncreaseMaxFactor));
    }

    public Point2D getMinDistance(){
        return minDistance;
    }

    public boolean canSteerLeft(){
        return false;
    }

    public boolean canSteerRight(){
        return true;
    }

    @Override
    public javax.swing.JComponent[] getComponent() {
        if (GRAPHICAL_DEBUG) {
            return new javax.swing.JComponent[]{debugPainter};

        } else {
            return new javax.swing.JComponent[0];
        }
    }

    @Override
    public void setStage(Stage s) {
        this.stage = s;
    }

    @Override
    public void update(SensorData data, Situation s) {
        if (stage != Stage.RACE) {
            reset();
            return;
        }

        if (!data.onTrack()) {
            reset();
            return;
        }

        if (!model.complete()) {
            reset();
            return;
        }

        doAvoid(data, s);

        if (GRAPHICAL_DEBUG) {
            debugPainter.repaint();
        }
    }

    public String getInfo() {
        return "";
    }

    private void doAvoid(SensorData data, Situation situation) {
        int currentIndex = model.getIndex(data.getDistanceFromStartLine());
        TrackSegment current = model.getSegment(currentIndex);
        TrackSegment nextCorner = null;

        double remainingStraight = 0.0;
        if (current.isStraight()) {
            remainingStraight += current.getEnd() - data.getDistanceFromStartLine();

            int nextIndex = model.incrementIndex(currentIndex);
            TrackSegment next = model.getSegment(nextIndex);

            while (nextIndex != currentIndex && next.isStraight()) {
                remainingStraight += next.getLength();

                nextIndex = model.incrementIndex(nextIndex);
                next = model.getSegment(nextIndex);
            }

            if (next.isCorner()) {
                nextCorner = next;
            }
        }

        double CRITICAL_WIDTH = minDistance.getX()+CAR_WIDTH;

        if (remainingStraight > 0 && remainingStraight < MIN_REMAINING_STRAIGHT) {
            double v = Math.abs(nextCorner.getApexes()[0].value);
            CRITICAL_WIDTH += (remainingStraight / MIN_REMAINING_STRAIGHT) * Math.max(0, Math.min(1, v / 100.0)) * criticalWidthIncrease;

        } else if (current.isCorner()) {
            int apexIndex = -1;
            TrackSegment.Apex[] apexes = current.getApexes();

            for (int i = 0; i < apexes.length && apexIndex == -1; ++i) {
                if (data.getDistanceFromStartLine() < apexes[i].position) {
                    apexIndex = i;
                }
            }

            if (apexIndex >= 0 && apexIndex < apexes.length) {
                double v = Math.abs(apexes[apexIndex].value);
                CRITICAL_WIDTH += Math.max(0, Math.min(1, v / 100.0)) * criticalWidthIncrease;
            }
        }

        Rectangle2D criticalZone = new Rectangle2D.Double(
                -(CRITICAL_WIDTH * 0.5), 0.0, CRITICAL_WIDTH,
                MIN_CRITICAL_LOOKAHEAD + (Math.max(0, Math.min(1, data.getSpeed() / Plan2011.MAX_SPEED)) * CRITICAL_LOOKAHEAD_INCREASE));

        double[] sensors = data.getOpponentSensors();
        double[] track = data.getTrackEdgeSensors();        

        ArrayList<Opponent> active = new ArrayList<>();
        ArrayList<Opponent> ahead = new ArrayList<>();
        ArrayList<Opponent> critical = new ArrayList<>();

        for (int i = 0; i < sensors.length; ++i) {
            if(i >= 9 && i <= 27){
                if(track[i-9] < sensors[i]){
                    sensors[i] = 200.0;
                }
            }

            opponents[i].setSensorValue(sensors[i]);

            if (opponents[i].isActive()) {
                active.add(opponents[i]);
                /*if(TEXT_DEBUG){
                System.out.println(criticalZone.toString());
                System.out.println(opponents[i].getRectangle());
                }*/
                if (criticalZone.intersects(opponents[i].getRectangle())) {
                    critical.add(opponents[i]);
                }
            }
            if(opponents[i].isAhead()){
                ahead.add(opponents[i]);
            }
        }

        if (active.isEmpty()) {
            point = NO_RECOMMENDED_POINT;
            speed = NO_RECOMMENDED_SPEED;
            lastNumCars = -1;
            return;
        }

        lastNumCars = active.size();

        Collections.sort(active, new TimeToMinDistanceComparator());
        Collections.sort(critical, new TimeToMinDistanceComparator());

        if (TEXT_DEBUG && !active.isEmpty()) {
            System.out.println("---------[" + active.size() + "/" + critical.size() + "]----------------");
            for (int i = 0; i < active.size(); ++i) {
                Point2D p = active.get(i).getPosition();
                Point2D s = active.get(i).getSpeedDiffVec();
                double time = active.get(i).getTimeToCrash();
                System.out.println("Active[" + i + "] "
                        + Utils.dTS(s.getY() * 3.6) + "km/h, "
                        + Utils.dTS(p.getY()) + "m, "
                        + Utils.dTS(time) + "s, "
                        + Utils.dTS(active.get(i).getTimeToMinDistance()) + "s");
            }
            System.out.println("");
        }

        if (TEXT_DEBUG && !critical.isEmpty()) {
            System.out.println("---------[" + active.size() + "/" + critical.size() + "]----------------");
            for (int i = 0; i < critical.size(); ++i) {
                Point2D p = critical.get(i).getPosition();
                Point2D s = critical.get(i).getSpeedDiffVec();
                double time = critical.get(i).getTimeToCrash();
                System.out.println("Critical[" + i + "] "
                        + Utils.dTS(s.getY() * 3.6) + "km/h, "
                        + Utils.dTS(p.getY()) + "m, "
                        + Utils.dTS(time) + "s, "
                        + Utils.dTS(critical.get(i).getTimeToMinDistance()) + "s");
            }
        }

        if (TEXT_DEBUG && !ahead.isEmpty()) {
            System.out.println("---------[Ahead: " + ahead.size() + "]----------------");
            for (int i = 0; i < ahead.size(); ++i) {
                Point2D p = ahead.get(i).getPosition();
                Point2D s = ahead.get(i).getSpeedDiffVec();
                double time = ahead.get(i).getTimeToCrash();
                System.out.println("Ahead[" + i + "] "
                        + Utils.dTS(s.getY() * 3.6) + "km/h, "
                        + Utils.dTS(p.getY()) + "m, "
                        + Utils.dTS(time) + "s, "
                        + Utils.dTS(ahead.get(i).getTimeToMinDistance()) + "s");
            }
        }

        if (!critical.isEmpty() && critical.get(0).getTimeToMinDistance() <= 20.0) {
            double speedOther = data.getSpeed() + (critical.get(0).getSpeedDiffVec().getY() * 3.6);
            double allowedSpeed;

            if (critical.get(0).getTimeToMinDistance() >= 0) {
                allowedSpeed = speedOther + ((critical.get(0).getTimeToMinDistance() / 20.0) * 20.0);

            } else {
                //allowedSpeed = speedOther + ((critical.get(0).getTimeToMinDistance() / 20.0) * 20.0);
                allowedSpeed = data.getSpeed() - 5.0;
            }

            speed = Math.min(data.getSpeed(), allowedSpeed);

            if (TEXT_DEBUG) {
                System.out.println(data.getSpeedS() + "km/h, other: " + Utils.dTS(speedOther) + ", allowed: " + Utils.dTS(allowedSpeed));
                System.out.println("Recommended Speed: " + Utils.dTS(speed) + "km/h");
                System.out.println("");
            }

        } else {
            speed = NO_RECOMMENDED_SPEED;
        }

        if (remainingStraight > MIN_REMAINING_STRAIGHT && !ahead.isEmpty()) {
            point = NO_RECOMMENDED_POINT;
            Collections.sort(ahead, new RectangleXPositionComparator());
            final double myPosition = data.calcAbsoluteTrackPosition(model.getWidth());

            if (TEXT_DEBUG) {
                System.out.println("Me at "+Utils.dTS(myPosition) + " Calculate overtaking point:");
                for (int i = 0; i < ahead.size(); ++i) {
                    System.out.println("Ahead[" + i + "] "
                            + Utils.dTS(ahead.get(i).getRectangle().getMinX()));
                }
            }

            

            // luecken zum ueberholen
            ArrayList<Point2D> gaps = new ArrayList<>();

            // zwischen rand und linkem auto
            if ((ahead.get(0).getRectangle().getMinX() + myPosition) > CAR_WIDTH + minDistance.getX()) {
                gaps.add(new Point2D.Double(0.0, (ahead.get(0).getRectangle().getMinX() + myPosition)));
            }
            // zwischen den autos
            for (int i = 1; i < active.size(); ++i) {
                double width = (active.get(i).getRectangle().getMinX() + myPosition) -
                        (active.get(i-1).getRectangle().getMinX() + myPosition + CAR_WIDTH);
                if (width > CAR_WIDTH + minDistance.getX()) {
                    gaps.add(new Point2D.Double((active.get(i-1).getRectangle().getMinX() + myPosition + CAR_WIDTH),
                            width));
                }
            }
            // zwischen rechtem rand und rechtem auto
            double width = model.getWidth()-
                    (active.get(active.size()-1).getRectangle().getMinX() + myPosition + CAR_WIDTH);
            if (width > CAR_WIDTH + minDistance.getX()) {
                gaps.add(new Point2D.Double((active.get(active.size()-1).getRectangle().getMinX() + myPosition + CAR_WIDTH),
                            width));
            }

            if (TEXT_DEBUG && !gaps.isEmpty()) {
                for (int i = 0; i < gaps.size(); ++i) {
                    System.out.println("Gap[" + i + "] "+
                            Utils.dTS(gaps.get(i).getX())+", width="+
                            Utils.dTS(gaps.get(i).getY()));
                }
            }

            if(!gaps.isEmpty()){                
                double bestAbsDist = Double.POSITIVE_INFINITY;
                double bestPosition = 0.0;
                int bestIndex = -1;

                for(int i=0; i < gaps.size(); ++i){
                    // linker rand der luecke
                    double left = (myPosition-(gaps.get(i).getX()+(CAR_WIDTH*0.5)+minDistance.getX()));
                    // rechter rand der luecke
                    double right = (myPosition-(gaps.get(i).getX()+gaps.get(i).getY()-(CAR_WIDTH*0.5)-minDistance.getX()));
                    
                    double absMin = Math.abs(left);
                    double position = gaps.get(i).getX()+(CAR_WIDTH*0.5)+minDistance.getX();
                    if(Math.abs(right) < absMin){
                        absMin = Math.abs(right);
                        position = gaps.get(i).getX()+gaps.get(i).getY()-(CAR_WIDTH*0.5)-minDistance.getX();
                    }

                    if(TEXT_DEBUG){
                        System.out.println("["+i+"] "+Utils.dTS(left)+" "+Utils.dTS(right)+" @ "+Utils.dTS(position));
                    }

                    if(absMin < bestAbsDist){
                        bestAbsDist = absMin;
                        bestPosition = position;
                        bestIndex = i;
                    }
                }
                
                if(TEXT_DEBUG){
                    System.out.print("Choosing gap "+bestIndex+" / "+Utils.dTS(bestPosition));
                }               
                
                double xPos = SensorData.calcRelativeTrackPosition(bestPosition, model.getWidth());
                double yPos = data.getDistanceRaced()+calcSwitchDistance(data, bestAbsDist);                

                point = new Point2D.Double(xPos, yPos);
                if(TEXT_DEBUG){
                    System.out.println(" "+Utils.dTS(xPos)+"("+Utils.dTS(bestPosition)+"), "+Utils.dTS(yPos)+"/"+Utils.dTS(yPos-data.getDistanceRaced()));
                    System.out.println("");
                }
            }

        } else {
            point = NO_RECOMMENDED_POINT;
        }
    }

    /**
     * Returns the recommended position on the track to avoid other cars. The
     * x-coordinate corresponds to the position on the track and the y-coordinate
     * to the race distance, at which the given x coordinate should be reached.
     *
     * Might return NO_RECOMMENDED_POINT if there is no recommendation.
     *
     * @return
     */
    @Override
    public java.awt.geom.Point2D getRecommendedPosition() {
        return point;
    }

    @Override
    public PositionType getPositionType(){
        return PositionType.OVERTAKING;
    }

    /**
     * Returns the recommended speed to avoid crashing into other cars.
     * Might return NO_RECOMMENDE_SPEED if there is no need to slow down.
     *
     * @return
     */
    @Override
    public double getRecommendedSpeed() {
        return speed;
    }

    @Override
    public boolean otherCars() {
        return lastNumCars != -1;

    }

    @Override
    public void setTrackModel(TrackModel trackModel) {
        this.model = trackModel;
    }

    @Override
    public void reset() {
        for (Opponent o : opponents) {
            o.reset();
        }

        point = OpponentObserver.NO_RECOMMENDED_POINT;
        speed = OpponentObserver.NO_RECOMMENDED_SPEED;
        lastNumCars = -1;        
    }

    private class DebugPainter
            extends JPanel {

        @Override
        public void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics;
            try {
                paintComponent(g);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }

        public void paintComponent(Graphics2D g) {
            Dimension size = getSize();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.BLACK);

            //g.drawString(info, 10, 10);
        }
    }
}
