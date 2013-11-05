/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.opponents;

import de.verlage.mrracer.comparator.TimeToMinDistanceComparator2013;
import de.verlage.mrracer.comparator.RectangleXPositionComparator2013;
import de.verlage.mrracer.comparator.YPosComparator2013;
import scr.Controller.Stage;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.classification.Situation;
import static de.janquadflieg.mrracer.data.CarConstants.*;
import de.janquadflieg.mrracer.gui.GraphicDebugable;
import static de.janquadflieg.mrracer.opponents.OpponentObserver.NO_RECOMMENDED_POINT;
import static de.janquadflieg.mrracer.opponents.OpponentObserver.NO_RECOMMENDED_SPEED;
import de.janquadflieg.mrracer.plan.Plan2013;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.*;
import java.text.NumberFormat;

import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.JPanel;

/**
 *
 * @author Verlage
 */
public class Observer2013
        implements OpponentObserver, GraphicDebugable {

    private SensorData graphdata;
    /**
     * Maximum distance of sensors accepted by the noise handler.
     */
    public static final double MAX_DISTANCE_NOISY = 175.0;
    /**
     * Minimum lookahead for the critical zone.
     */
    private static final double MIN_CRITICAL_LOOKAHEAD = 10.0 + CAR_LENGTH;
    /**
     * Maximum lookahead for the critical zone.
     */
    private static final double CRITICAL_LOOKAHEAD_INCREASE = MAX_DISTANCE_NOISY - MIN_CRITICAL_LOOKAHEAD;
    /**
     * Minimum width for the critical zone.
     */
    private static final double MIN_CRITICAL_WIDTH = CAR_WIDTH + 2.0;
    /**
     * Increase width for the critical zone.
     */
    private static final double CRITICAL_WIDTH_INCREASE = 4;
    /**
     * Minimal distance to switch the position on the track by 1 meter.
     */
    private static final double MIN_SWITCH_DISTANCE = 10.0;
    /**
     * Factor to increase the minimum switch distance according to the current
     * speed.
     */
    private static final double SWITCH_INCREASE_MAX_FACTOR = 7.0;
    /**
     * MIN DISTANCE to other slow Cars(active after some damage)
     */
    private double MIN_DISTANCE_TO_SLOW = 6.5;
    // Distance to other car, before using higher one(without damage)        
    private double MIN_DISTANCE_TO_SLOW_WOD = 5.3;
    /**
     * Minimum absolute distance in meter to other cars.
     */
    public static final Point2D MIN_DISTANCE = new Point2D.Double(3.0, 10.0);
    /**
     * Epsilon multiplicator on which opposensors are still acepted(track <
     * oppo)
     */
    private static final double epsilon_over_track = 1.2;
    /**
     * Graphical debugging?
     */
    private boolean GRAPHICAL_DEBUG = true;
    /**
     * Output debug text messages?
     */
    private static final boolean TEXT_DEBUG = false;
    /**
     * NEW Opponent behaviour?
     */
    private static final boolean new_behave = true;
    /**
     * Debug painter.
     */
    private DebugPainter debugPainter;
    /**
     * TrackModel.
     */
    private TrackModel model;
    /**
     * Current stage.
     */
    private Stage stage = Stage.RACE;
    /**
     * Recommended point.
     */
    private Point2D point = OpponentObserver.NO_RECOMMENDED_POINT;
    /**
     * Recommmended speed.
     */
    private double speed = OpponentObserver.NO_RECOMMENDED_SPEED;
    /**
     * Number of active cars.
     */
    private int lastNumCars = -1;
    /**
     * criticalzone
     */
    Rectangle2D criticalZone;
    /**
     * GAB LENGTH which is accepted in beginning of race
     */
    /**
     * Polygon for RightCorners - New Criticalzone
     */
    ArrayList<Point2D> CornerPoly = new ArrayList<>();
    Polygon GraphdebugPoly;
    Polygon critRCornPoly;
    Polygon critLCornPoly;
    private double gapWidth = 2.8;
    /**
     * Pixel per Meter for Graphicdebug
     */
    private int PPM = 10;
    private Opponent2013[] opponents;
    private OppoTracking tracker;
    private PositionType artOfPosition;
    private int zoneFactor = 3;
    boolean safemode = false;
    //critical brake zone. if a oppo has this distance and is slower take a brake :(
    double lookahead = 0.0;
    boolean iWantToBlock = false;
    double cornerSafe = 0.0;

    public Observer2013() {
        if (GRAPHICAL_DEBUG) {
            debugPainter = new DebugPainter();
            debugPainter.setName("Observer2013");
            GraphdebugPoly = new Polygon();
        }

        /*if (System.getProperties().containsKey("ObserverDebug")) {
         System.out.println("ObserverDebug");
         TEXT_DEBUG = true;
         }*/
        tracker = new OppoTracking(this);
        criticalZone = new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);
        critRCornPoly = new Polygon();
        critLCornPoly = new Polygon();
        artOfPosition = PositionType.BLOCKING;

    }

    @Override
    public void setParameters(Properties params, String prefix) {
    }

    @Override
    public void getParameters(Properties params, String prefix) {
    }

    public void paint(String baseFileName, java.awt.Dimension d) {
    }    

    public Point2D getMinDistance() {
        return MIN_DISTANCE;
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
        graphdata = data;
        doAvoid(data, s);
        if (GRAPHICAL_DEBUG) {
            debugPainter.repaint();
        }
    }

    @Override
    public SensorData getData() {
        return graphdata;
    }

    public String getInfo() {
        return "";
    }

    private void doAvoid(SensorData data, Situation situation) {
        doAvoidNew(data, situation);
    }

    private void doAvoidNew(SensorData data, Situation situation) {
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

        double[] sensors = data.getOpponentSensors();
        double[] track = data.getTrackEdgeSensors();



        Double[] sensorsD = new Double[36];

        for (int i = 0; i < sensors.length; ++i) {
            if (i >= 9 && i <= 27) {
                if (track[i - 9] * epsilon_over_track < sensors[i]) {
                    sensors[i] = 200.0;
                }
            }
            if (i == 8 && sensors[i] * 1.2 > track[10]) {
                sensors[i] = 200.0;
            }
            if (i == 7 && sensors[i] * 1.3 > track[11]) {
                sensors[i] = 200.0;
            }
            if (i == 6 && sensors[i] * 1.4 > track[12]) {
                sensors[i] = 200.0;
            }
            if (i == 5 && sensors[i] * 1.5 > track[13]) {
                sensors[i] = 200.0;
            }
            if (i == 4 && sensors[i] * 1.5 > track[14]) {
                sensors[i] = 200.0;
            }
            if (i == 28 && sensors[i] * 1.2 > track[17]) {
                sensors[i] = 200.0;
            }
            if (i == 29 && sensors[i] * 1.3 > track[16]) {
                sensors[i] = 200.0;
            }
            if (i == 30 && sensors[i] * 1.4 > track[15]) {
                sensors[i] = 200.0;
            }
            if (i == 31 && sensors[i] * 1.5 > track[14]) {
                sensors[i] = 200.0;
            }
            if (i == 32 && sensors[i] * 1.5 > track[13]) {
                sensors[i] = 200.0;
            }


            sensorsD[i] = (Double) sensors[i];
        }

        tracker.addSensorValues(sensorsD, track[0], track[17]);

        ArrayList<Opponent2013> allactive = tracker.getActiveOppos();
        ArrayList<Opponent2013> active = tracker.getGegnerActiveAhead();
        ArrayList<Opponent2013> blockable = tracker.getBlockableOppos();
        ArrayList<Opponent2013> ahead = tracker.getGegnerAhead();


        double myPosition = data.calcAbsoluteTrackPosition(model.getWidth());
        //speed = 270.0;
        Point2D.Double blockpoint = new Point2D.Double();
        lastNumCars = allactive.size();
        if (lastNumCars == 0) {
            lastNumCars = -1;
            point = NO_RECOMMENDED_POINT;
            speed = NO_RECOMMENDED_SPEED;
            return;
        }

        Collections.sort(blockable, new YPosComparator2013());
        Collections.sort(active, new YPosComparator2013());


        int posInRace = data.getRacePosition();

        //Adaptive scale for acepted Gaps- scales with Damage
        if (data.getDamage() > 1000 && data.getDamage() <= 2000) {
            gapWidth = 2.8;
            zoneFactor = 3;
            safemode = false;
        } else if (data.getDamage() > 2000 && data.getDamage() <= 3000) {
            gapWidth = 3.0;
            zoneFactor = 3;
            safemode = false;
        } else if (data.getDamage() > 3000 && data.getDamage() <= 5000) {
            gapWidth = 3.3;
            zoneFactor = 3;
            safemode = false;
        } else if (data.getDamage() > 5000) {
            gapWidth = 3.8;
            zoneFactor = 2;
            safemode = true;
        } else {
            gapWidth = 2.8;
            zoneFactor = 3;
            safemode = false;
        }

        if (posInRace == 1) {
            gapWidth = 3.8;
            zoneFactor = 2;
            safemode = true;
        }



        if (current.isStraight()) {

            CornerPoly.clear();
            critRCornPoly.reset();
            critLCornPoly.reset();
            iWantToBlock = false;

            //Blocking
            if (data.getDamage() < 5000) {
                if (active.size() == 0) {

                    if (blockable != null && blockable.size() > 0) {

                        artOfPosition = PositionType.BLOCKING;
                        speed = NO_RECOMMENDED_SPEED;
                        double x = blockable.get(blockable.size() - 1).getPosition().getX();
                        blockable.get(blockable.size() - 1).setResponsable(true);
                        if (blockable.get(blockable.size() - 1).getTimeToCrash() > 4.0 || blockable.get(blockable.size() - 1).getPosition().getY() > 50) {
                            point = NO_RECOMMENDED_POINT;
                            iWantToBlock = false;
                        } else {

                            //only block if oppo is more then 1 meter away(x-coord)
                            if (Math.abs(x) > 1.0) {
                                //check if x coord ist not on Track and chance it to track end
                                if (x > 0) {
                                    if (x > track[17] || (x > track[17] - 1.4)) {
                                        x = track[17] - 1.4;
                                    }
                                } else {
                                    if (Math.abs(x) > track[0] || (Math.abs(x) > track[0] - 1.4)) {
                                        x = -track[0] + 1.4;
                                    }
                                }

                                double xPos = SensorData.calcRelativeTrackPosition(x + myPosition, model.getWidth());
                                double deltaPosition = Math.abs(myPosition - (x));
                                double yPos = data.getDistanceRaced() + MIN_SWITCH_DISTANCE + 30.0;
                                yPos += deltaPosition * MIN_SWITCH_DISTANCE + (Math.max(0, Math.min(1, data.getSpeed() / 300.0)) * SWITCH_INCREASE_MAX_FACTOR) * 10;

                                blockpoint = new Point2D.Double(xPos, yPos);
                                iWantToBlock = true;
                                if (!(point == NO_RECOMMENDED_POINT)) {
                                    if ((point.getX() + 0.1 >= blockpoint.getX() && point.getX() - 0.1 <= blockpoint.getX())) {
                                    } else {
                                        point = blockpoint;
                                    }
                                } else {
                                    point = new Point2D.Double(xPos, yPos);
                                }
                            }
                        }//System.out.println("xPos: "+xPos);    

                    } else {
                        point = NO_RECOMMENDED_POINT;
                        iWantToBlock = false;
                    }
                }
            }

            //Check gaps
            if (active != null && active.size() > 0) {
                // Find gap
                int i = 0;

                active.get(i).setResponsable(true);

                if (ahead.size() == 1) {
                    artOfPosition = PositionType.OVERTAKING;
                } else {
                    artOfPosition = PositionType.BLOCKING;
                }

                Point2D.Double ausweich = new Point2D.Double(0.0, 0.0);
                double leftgap = active.get(i).getLeftGap();
                double rightgap = active.get(i).getRightGap();
                double x = active.get(i).getPosition().getX();
                double y = active.get(i).getPosition().getY();
                double xPos = 0.0;
                double xNeben = 0.0;
                boolean rightGapUsed = false;
                double xrechtsneben = 0;
                double xlinksneben = 0;
                double switchx = 0.0;
                boolean shouldBrake = false;
                int usedOppo = -1;

                if (leftgap > gapWidth || rightgap > gapWidth) {
                    usedOppo = i;

                    if ((leftgap <= gapWidth && rightgap > gapWidth) || (rightgap <= gapWidth && leftgap > gapWidth)) {
                        if (rightgap > leftgap) {
                            if (x < 0) {
                                //RIGHTGAP is the only acteptable and we are on left side of oppo. so take the gap!
                                rightGapUsed = true;
                                xNeben = +(rightgap / 2) * 1.3;
                                xPos = SensorData.calcRelativeTrackPosition(x + myPosition + xNeben, model.getWidth());
                                switchx = x + myPosition + xNeben;
                            } else {
                                /**
                                 * RIGHTGAP is the only acteptable, but we are
                                 * on the wrong side so check if distance is big
                                 * enough to take gap on the other side if not
                                 * so: Brake!
                                 */
                                if (y > 9) {
                                    rightGapUsed = true;
                                    xNeben = +(rightgap / 2) * 1.3;
                                    xPos = SensorData.calcRelativeTrackPosition(x + myPosition + xNeben, model.getWidth());
                                    switchx = x + myPosition + xNeben;
                                } else {
                                    speed = data.getSpeed() - 5;
                                    active.get(i).setCritical(true);
                                    shouldBrake = true;
                                }
                            }
                        } else {


                            if (x > 0) {
                                //LEFTGAP is the only acteptable and we are on left side of oppo. so take the gap!
                                rightGapUsed = false;
                                xNeben = -(leftgap / 2) * 1.3;
                                xPos = SensorData.calcRelativeTrackPosition(x + myPosition + xNeben, model.getWidth());
                                switchx = x + myPosition + xNeben;
                            } else {
                                /**
                                 * LEFTGAP is the only acteptable, but we are on
                                 * the wrong side so check if distance is big
                                 * enough to take gap on the other side if not
                                 * so: Brake!
                                 */
                                if (y > 9) {
                                    rightGapUsed = false;
                                    xNeben = -(leftgap / 2) * 1.3;
                                    xPos = SensorData.calcRelativeTrackPosition(x + myPosition + xNeben, model.getWidth());
                                    switchx = x + myPosition + xNeben;
                                } else {
                                    speed = data.getSpeed() - 5;
                                    active.get(i).setCritical(true);
                                    shouldBrake = true;
                                }
                            }


                        }

                    } else {
                        // both gaps are wide enough - use that side of oppo on which you are allready
                        xrechtsneben = (rightgap / 2) * 1.3;
                        xlinksneben = -(leftgap / 2) * 1.3;

                        if (x > 0) {
                            xPos = SensorData.calcRelativeTrackPosition(x + myPosition + xlinksneben, model.getWidth());
                            switchx = x + myPosition + xlinksneben;
                            rightGapUsed = false;
                        } else {
                            xPos = SensorData.calcRelativeTrackPosition(x + myPosition + xrechtsneben, model.getWidth());
                            switchx = x + myPosition + xrechtsneben;
                            rightGapUsed = true;
                        }
                    }
                    //if no brake action was found yet set new wanted point
                    if (!shouldBrake) {
                        double deltaPosition = Math.abs(myPosition - xNeben);
                        double yPos = data.getDistanceRaced() + MIN_SWITCH_DISTANCE;
                        yPos += deltaPosition * MIN_SWITCH_DISTANCE + (Math.max(0, Math.min(1, data.getSpeed() / 300.0)) * SWITCH_INCREASE_MAX_FACTOR) * 1.5;
                        speed = NO_RECOMMENDED_SPEED;
                        ausweich = new Point2D.Double(xPos, yPos);
                        if (!(point == NO_RECOMMENDED_POINT)) {
                            if ((point.getX() + 0.1 >= ausweich.getX() && point.getX() - 0.1 <= ausweich.getX())) {
                            } else {
                                point = ausweich;
                            }
                        } else {
                            point = new Point2D.Double(xPos, yPos);
                        }
                    }


                    double speedY = data.getSpeed();
                    double neededY = 0.0;
                    double timetoCrash = active.get(i).getTimeToCrash();


                    //check if i'm in the zone near and behind to opponent
                    if (x >= -2.1 && x <= 2.1) { //gefährdeter Bereich

                        neededY = Plan2013.calcSwitchDistance(data, switchx);
                        double timeChance = neededY / speedY;


                        // System.out.println("timeChance: " + timeChance + " / TimetoCrash: " + timetoCrash);


                        if ((timeChance > timetoCrash && data.getSpeed() - 20 > 0) || (timeChance < 0.15 && y > 3)) {
                            if (timetoCrash < 10 && timetoCrash > 6) {
                                speed = data.getSpeed() - 2;
                            } else if (timetoCrash > 3 && timetoCrash <= 6) {
                                speed = data.getSpeed() - 5;
                            } else if (timetoCrash <= 3) {
                                speed = data.getSpeed() - 7;
                            }
                            active.get(i).setCritical(true);

                        } else {
                            speed = NO_RECOMMENDED_SPEED;
                        }
                    } else {
                        speed = NO_RECOMMENDED_SPEED;
                    }


                } else { // NO GAP FOUND - Bring speed down to Oppos Speed
                    speed = data.getSpeed() + active.get(0).getSpeedDiffVec().getY();
                    active.get(0).setCritical(true);
                    //System.out.println("no gap found!");
                    point = NO_RECOMMENDED_POINT; // be careful with this... maybe it would be better to hold position
                }

                if ((safemode || data.getDamage() > 1000) && !shouldBrake) {
                    for (int z = 0; z < active.size(); z++) {

                        if (usedOppo == z) {
                            if (active.get(z).getSpeedDiffVec().getY() * 3.6 < -50.0) {
                                speed = data.getSpeed() - 5;
                                active.get(z).setCritical(true);
                            }
                        } else {
                            if (active.get(z).getSpeedDiffVec().getY() * 3.6 < -60.0) {
                                speed = data.getSpeed() - 2;
                                active.get(z).setCritical(true);
                            }

                        }
                    }
                }
            } else {

                if (!iWantToBlock) {
                    point = NO_RECOMMENDED_POINT;
                }
            }


        } else if (current.isCorner()) {
             //Prebrakeassitanceagent for Corners
            if ((safemode||data.getDamage()>1000)) {
                    for (int z = 0; z < active.size(); z++) {
                            
                        
                            if (active.get(z).getSpeedDiffVec().getY()*3.6 < -50.0) {
                                speed = data.getSpeed() - 5;
                                active.get(z).setCritical(true);
                                
                                return;
                            }

                        
                    }
                }
            
            
            
            iWantToBlock = false;
            point = NO_RECOMMENDED_POINT;
            Integer rightCornerFactor1 = new Double(track[12]).intValue();
            Integer rightCornerFactor2 = new Double(track[11]).intValue();

            Integer leftCornerFactor1 = new Double(track[6]).intValue();
            Integer leftCornerFactor2 = new Double(track[5]).intValue();

            critRCornPoly.reset();
            critLCornPoly.reset();
            CornerPoly.clear();
            if (active.size() > 0) {
                if (current.isLeft()) {
                    if (GRAPHICAL_DEBUG) {

                        CornerPoly.add(new Point2D.Double(4 * data.getSpeed() / 100, 2));
                        CornerPoly.add(new Point2D.Double(-2, (8 + leftCornerFactor2 * active.get(0).getSpeedDiffVec().getY() / zoneFactor * -1)));
                        CornerPoly.add(new Point2D.Double(-5 - leftCornerFactor2, 2 * leftCornerFactor2 * (data.getSpeed() / 40)));
                        CornerPoly.add(new Point2D.Double(-5 - leftCornerFactor1, leftCornerFactor2));
                        CornerPoly.add(new Point2D.Double(-1, 0));
                    }


                    critLCornPoly.addPoint(4 * (new Double(data.getSpeed() / 100).intValue()), 2);
                    critLCornPoly.addPoint(-2, (8 + leftCornerFactor2 * new Double(active.get(0).getSpeedDiffVec().getY() / zoneFactor * -1).intValue()));
                    critLCornPoly.addPoint(-5 - leftCornerFactor2, 2 * leftCornerFactor2 * (new Double(data.getSpeed() / 40).intValue()));
                    critLCornPoly.addPoint(-5 - leftCornerFactor1, leftCornerFactor2);
                    critLCornPoly.addPoint(-1, 0);

                } else {
                    if (GRAPHICAL_DEBUG) {
                        CornerPoly.add(new Point2D.Double(-4 * data.getSpeed() / 100, 2));
                        CornerPoly.add(new Point2D.Double(2, (8 + rightCornerFactor2 * active.get(0).getSpeedDiffVec().getY() / zoneFactor * -1)));
                        CornerPoly.add(new Point2D.Double(5 + rightCornerFactor2, 2 * rightCornerFactor2 * (data.getSpeed() / 40)));
                        CornerPoly.add(new Point2D.Double(5 + rightCornerFactor1, rightCornerFactor2));
                        CornerPoly.add(new Point2D.Double(1, 0));
                    }
                    critRCornPoly.addPoint(-4 * (new Double(data.getSpeed() / 100).intValue()), 2);
                    critRCornPoly.addPoint(2, (8 + rightCornerFactor2 * new Double(active.get(0).getSpeedDiffVec().getY() / zoneFactor * -1).intValue()));
                    critRCornPoly.addPoint(5 + rightCornerFactor2, 2 * rightCornerFactor2 * (new Double(data.getSpeed() / 40).intValue()));
                    critRCornPoly.addPoint(5 + rightCornerFactor1, rightCornerFactor2);
                    critRCornPoly.addPoint(1, 0);

                }
                int x = 0;

                if (active.size() > 1) {
                    x = 1;
                    if (safemode) {
                        x = active.size() - 1;
                    }
                }
                boolean shouldbrake = false;
                for (int i = 0; i <= x; i++) {
                    if (active.get(i).getTimeToCrash() < 5.0) {
                        if ((current.isRight() && critRCornPoly.intersects(active.get(i).getRectangle()))
                                || ((current.isLeft() && critLCornPoly.intersects(active.get(i).getRectangle())))) {
                            speed = data.getSpeed() - 5;
                            active.get(i).setCritical(true);
                            shouldbrake = true;
                        } else {
                            if (!shouldbrake) {
                                speed = NO_RECOMMENDED_SPEED;
                            }
                        }


                    } else {
                        speed = NO_RECOMMENDED_SPEED;
                    }
                }
            } else {
                speed = NO_RECOMMENDED_SPEED;
            }
        }

        // Lets check a min_distance to all active cars in Front
        //this is contra productive for overtaking!!!!!
        //look more far if current is corner

        lookahead = MIN_DISTANCE_TO_SLOW_WOD;

        if (data.getDamage() > 2100 || posInRace == 1) {
            if (data.getDamage() > 5000) {
                MIN_DISTANCE_TO_SLOW = 10;
            }
            lookahead = MIN_DISTANCE_TO_SLOW;
        }
        cornerSafe = 0.0;
        if (current.isCorner()) {
            cornerSafe = 2.1;
        }
        for (int i = 0; i < active.size(); i++) {
            if (active.get(i).getPosition().distance(0.0, 0.0) < lookahead + cornerSafe) {
                active.get(i).setCritical(true);
                if (speed == NO_RECOMMENDED_SPEED) {
                    speed = data.getSpeed() - 5;
                } else {
                    //Speed was allready decreased
                    if (speed > 20) {
                        speed = speed - 2;
                    }
                }


            }

        }


        //Absolute emergency brake. Looks at all cars ahead - So DiffVec is not relevant here!
        for (int i = 0; i < ahead.size(); i++) {
            if (ahead.get(i).getPosition().distance(0.0, 0.0) < 5.9 && ahead.get(i).getPosition().getY() > 4) {
                ahead.get(i).setCritical(true);
                if (speed == NO_RECOMMENDED_SPEED) {
                    speed = data.getSpeed() - 5;
                } else {
                    //Speed was allready decreased
                    if (speed > 20) {
                        speed = speed - 2;
                    }
                }


            }

        }
        // Safe Brakeassistance right before an Corner - DO NOT OVERTAKE anymore if it is to risky
        boolean fullhelp = false;
        if (nextCorner != null) {
            fullhelp = !nextCorner.isFull();
        }
        if (remainingStraight < 100 && safemode
                && fullhelp
                && (current.isStraight()
                || current.isFull())) {
            double myspeed = data.getSpeed();
            double timetoStraightEnd = myspeed / remainingStraight;


            for (int i = 0; i < active.size(); i++) {
                double timeForSafeOvertake = (active.get(i).getSpeedDiffVec().getY() * -1 / active.get(i).getPosition().getY()) + 1;
                if (active.get(i).getTimeToCrash() < timetoStraightEnd && !(timeForSafeOvertake > timetoStraightEnd)) {
                    speed = myspeed + active.get(i).getSpeedDiffVec().getY();
                    active.get(i).setCritical(true);


                }



            }
        }

    }

    /**
     * Returns the recommended position on the track to avoid, block or overtake
     * other cars. The x-coordinate corresponds to the relative position on the
     * track, as defined by the SCRC sensormodel (1 left edge of the track, 0
     * middle of the track, -1 right edge of the track). The y-coordinate is the
     * race distance at which the given x position should be reached. This can
     * be computed as the current race distance given by the sensor data plus a
     * certain distance needed to move the car to the x position. Might return
     * NO_RECOMMENDED_POINT if there is no recommendation.
     *
     * @return
     */
    @Override
    public java.awt.geom.Point2D getRecommendedPosition() {
        return point;
    }

    @Override
    public PositionType getPositionType() {
        return artOfPosition;
    }

    /**
     * Returns the recommended speed to avoid crashing into other cars. Might
     * return NO_RECOMMENDE_SPEED if there is no need to slow down.
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
    public TrackModel getTrackModel() {
        return model;
    }

    @Override
    public void reset() {


        point = OpponentObserver.NO_RECOMMENDED_POINT;
        speed = OpponentObserver.NO_RECOMMENDED_SPEED;

        lastNumCars = -1;

        tracker.reset();
        /*lastOppDistance = -1;
         lastOppCtr = 0;
         lastPacket = -1;*/
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
            DecimalFormat df = new DecimalFormat(",##0.00");

            Integer rightend = 0;
            Integer leftend = 0;
            double Yresponsable = 0.0;
            if (graphdata != null) {
                double[] track = graphdata.getTrackEdgeSensors();
                //Streckenrand holen
                leftend = new Double(track[0] * PPM).intValue();
                rightend = new Double(track[17] * PPM).intValue();

            }
            int height = new Double(getHeight() * 0.65).intValue();


            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.BLACK);
            //g.drawString("Kai Test", 10, 10);
            //Auto zeichen
            g.drawRect((getWidth() / 2), height - 60, 20, 50);

            //g.drawRect(0, 0, 20, 50);

            g.setColor(Color.GRAY);
            //Fadenkreuz
            g.drawLine(20, height - 35, getWidth() - 20, height - 35);
            g.drawLine((getWidth() / 2) + 10, 20, (getWidth() / 2) + 10, getHeight() - 20);

            //Streckerand
            g.setColor(Color.RED);
            g.drawLine((getWidth() / 2) - leftend, 20, (getWidth() / 2) - leftend, getHeight() - 20);
            g.drawLine((getWidth() / 2) + rightend, 20, (getWidth() / 2) + rightend, getHeight() - 20);

            g.setColor(Color.GRAY);
            //Ovale
            //g.drawOval(0, 0, 100, 100);
            g.drawOval((getWidth() / 2) - 40, height - 85, 100, 100);
            g.drawOval((getWidth() / 2) - 90, height - 135, 200, 200);
            g.drawOval((getWidth() / 2) - 140, height - 185, 300, 300);
            g.drawOval((getWidth() / 2) - 190, height - 240, 400, 400);

            g.drawString("Je Ring 5 Meter(d=10)", (getWidth() / 2) + 140, height + 140);

            String segment = "Segment: ";
            if (graphdata != null) {
                int currentIndex = model.getIndex(graphdata.getDistanceFromStartLine());
                TrackSegment current = model.getSegment(currentIndex);

                if (current.isStraight()) {
                    segment += "STRAIGHT";
                } else if (current.isFull()) {
                    segment += "FULL SPEED CORNER";
                } else if (current.isCorner()) {
                    segment += "Corner";
                }
                g.drawString(segment, getWidth() - 200, 20);
            }

            g.setColor(Color.BLACK);
            opponents = tracker.getOpponentsAsArray();
            if (opponents == null) {
                return;
            }


            String anzahl = "Gegner: ";
            anzahl = anzahl.concat(Integer.toString(opponents.length));
            g.drawString(anzahl, getWidth() - 200, 32);


            String oppoidStr = "Sum(Gegner)= ";
            oppoidStr = oppoidStr.concat(Integer.toString(tracker.getOppoId()));
            g.drawString(oppoidStr, getWidth() - 200, 44);

            String luecke = "Lückengröße: " + gapWidth;
            g.drawString(luecke, getWidth() - 200, 66);

            String safeBehave = "Safemode? " + safemode;
            g.drawString(safeBehave, getWidth() - 200, 78);

            String critBrake = "krit. Bremsdis.: " + (lookahead + cornerSafe);
            g.drawString(critBrake, getWidth() - 200, 90);

            String blocking = "Blocking? " + iWantToBlock;
            g.drawString(blocking, getWidth() - 200, 102);
            String speedStr = "";
            if (speed == NO_RECOMMENDED_SPEED) {
                speedStr += "Wunschspeed: Kein Limit";
            } else {
                speedStr += "Wunschspeed: " + df.format(speed);
            }
            g.drawString(speedStr, getWidth() - 380, 20);

            String Xpoint = "";
            if (point != NO_RECOMMENDED_POINT && point != null) {
                Xpoint += "Wunsch X: " + df.format(point.getX());
            } else {
                Xpoint += "Wunsch X: -----";
            }
            g.drawString(Xpoint, getWidth() - 380, 32);


            String Ypoint = "";

            if (point != NO_RECOMMENDED_POINT && point != null) {
                Ypoint += "m bis switch: " + df.format(point.getY() - graphdata.getDistanceFromStartLine());
            } else {
                Ypoint += "m bis switch: -----";
            }
            g.drawString(Ypoint, getWidth() - 380, 44);
            // String gametick = "Gametick: " + Integer.toString(tracker.getGameticks());
            //g.drawString(gametick, getWidth() - 200, 66);


            //draw criticalzone
            g.setColor(Color.ORANGE);
            //g.drawRect((getWidth() / 2) + 10 + new Double(criticalZone.getX() * PPM).intValue(), height - 60 + 25 - new Double(criticalZone.getHeight()).intValue() * PPM, new Double(criticalZone.getWidth()).intValue() * PPM, new Double(criticalZone.getHeight()).intValue() * PPM);


            GraphdebugPoly.reset();
            for (int i = 0; i < CornerPoly.size(); i++) {
                GraphdebugPoly.addPoint((getWidth() / 2) + 10 + new Double(CornerPoly.get(i).getX() * PPM).intValue(), height - 60 + 25 - new Double(CornerPoly.get(i).getY() * PPM).intValue());

            }

            g.drawPolygon(GraphdebugPoly);

            g.setColor(Color.BLACK);


            for (int i = 0; i < opponents.length; i++) {

                int x = new Double(opponents[i].getPosition().getX() * PPM).intValue();
                int y = new Double(opponents[i].getPosition().getY() * PPM).intValue();
                String nummer = Integer.toString(opponents[i].getNumber());
                //opponents[i].


                String speedX = (df.format(opponents[i].getSpeedDiffVec().getX() * 3.6));
                speedX = speedX.concat(" km/h");

                String speedY = df.format(opponents[i].getSpeedDiffVec().getY() * 3.6);
                speedY = speedY.concat(" km/h");

                String timeMinDistance = df.format(opponents[i].getTimeToCrash());

                String gapL = df.format(opponents[i].getLeftGap()) + " m";
                String gapR = df.format(opponents[i].getRightGap()) + " m";
                g.setColor(Color.GRAY);
                g.drawString(speedX, (getWidth() / 2) - 60 + x, height - 100 + 37 - y);
                g.drawString(speedY, (getWidth() / 2) - 60 + x, height - 100 + 49 - y);
                g.drawString(nummer, (getWidth() / 2) - 50 + x, height - 100 + 25 - y);
                //g.drawString(gapL, (getWidth()/2)-50+x, height-50+61-y);
                //g.drawString(gapR, (getWidth()/2)-50+x, height-50+73-y); 

                //draw standard opponents
                g.setColor(Color.BLACK);
                g.drawRect((getWidth() / 2) - 10 + x, height - 40 - 25 - y, 20, 50);

                g.drawLine(x + (getWidth() / 2), height - 40 - y, (getWidth() / 2) + 10, height - 35);
                //fill activ opponents green
                g.setColor(Color.GREEN);
                if (opponents[i].isActive()) {
                    g.fillRect((getWidth() / 2) - 10 + x, height - 40 - 25 - y, 20, 50);
                }
                g.setColor(Color.YELLOW);
                if (opponents[i].isResponsable()) {
                    g.fillRect((getWidth() / 2) - 10 + x, height - 40 - 25 - y, 20, 50);
                    Yresponsable = height - 60 + 25 - y;
                }
                //fill critical opponents red
                g.setColor(Color.RED);
                if (opponents[i].isCritical()) {
                    g.fillRect((getWidth() / 2) - 10 + x, height - 40 - 25 - y, 20, 50);
                    Yresponsable = height - 60 + 25 - y;
                }
                /**
                 * if (opponents[i].getPosition().distance(0.0, 0.0)<4){
                 * System.out.println("-----------------------------------------------------");
                 * System.out.println("Distanz "+
                 * df.format(opponents[i].getPosition().distance(0.0, 0.0))+"
                 * bei "+opponents[i].getNumber()); System.out.println("y= "+
                 * df.format(opponents[i].getPosition().getY()));
                 * System.out.println("ahead?"+ opponents[i].isAhead());
                 * System.out.println("-----------------------------------------------------");
                 * }
                 */
                if (opponents[i].getRightGap() != 0.0 && opponents[i].getLeftGap() != 0.0) {
                    g.setColor(Color.BLUE);
                    int xgapenderight = new Double(opponents[i].getRightGap() * PPM).intValue() + x + (getWidth() / 2);
                    int xgapendeleft = new Double(x + (getWidth() / 2) - opponents[i].getLeftGap() * PPM).intValue();
                    g.drawLine(x + (getWidth() / 2), height - 40 - y, xgapenderight, height - 40 - y);
                    g.drawLine(x + (getWidth() / 2), height - 40 - y, xgapendeleft, height - 40 - y);

                    String StrGapR = "T";
                    String StrGapL = "T";
                    if (opponents[i].getRightOppo() != null) {
                        StrGapR = new Integer(opponents[i].getRightOppo().getNumber()).toString();
                    }
                    if (opponents[i].getLeftOppo() != null) {
                        StrGapL = new Integer(opponents[i].getLeftOppo().getNumber()).toString();
                    }


                    g.drawString("R: " + StrGapR, (getWidth() / 2) - 50 + x, height - 100 + 61 - y);
                    g.drawString("L: " + StrGapL, (getWidth() / 2) - 50 + x, height - 100 + 73 - y);

                }





                g.setColor(Color.BLACK);
                g.drawString("T:" + timeMinDistance, (getWidth() / 2) - 50 + x, height - 100 + 85 - y);
            }

            g.setColor(Color.BLUE);
            if (point != null) {
                Double Xausweich = (-(point.getX() - 1) * model.getWidth() / 2 - leftend / PPM) * PPM + getWidth() / 2;
                Double YAusweich = Yresponsable;
                //System.out.println(point.getY()-graphdata.getDistanceFromStartLine());
                g.drawOval(Xausweich.intValue(), YAusweich.intValue(), 4, 4);
            }
        }
    }
}
