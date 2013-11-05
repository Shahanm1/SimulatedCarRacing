/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer;


import de.janquadflieg.mrracer.evo.Individual2011;
import de.janquadflieg.mrracer.evo.tools.*;

import java.io.*;
import java.util.*;


/**
 * Helper class to create properties objects for the parameters of the controllers
 * from EA log files.
 *
 * @author quad
 */
public class PropertiesCreator {

    public static final String IND_NUMBER = "IND_NUMBER";
    public static final String IND_FITNESS = "IND_FITNESS";

    private static final HashMap<String, String> map = new HashMap<String, String>();

    private static final ArrayList<String> DRIVE_PARAMS = new ArrayList<String>();

    private static final ArrayList<String> RECOVERY_PARAMS = new ArrayList<String>();

    static{
        map.put(IND_NUMBER, "indno");
        map.put(IND_FITNESS, "fitnessValues");
        map.put("-MrRacer2011.Plan--PLAN.BCC-", "P_BCC");
        map.put("-MrRacer2011.Plan--PLAN.targetSpeeds--GLF.B-", "TS_B");
        map.put("-MrRacer2011.Plan--PLAN.targetSpeeds--GLF.M-", "TS_M");
        map.put("-MrRacer2011.Plan--PLAN.targetSpeeds--GLF.V-", "TS_V");
        map.put("-MrRacer2011.Plan--PLAN.targetSpeeds--GLF.Q-", "TS_Q");

        map.put("-MrRacer2011.Acc--DAB.accDamp--GLF.B-", "AD_B");
        map.put("-MrRacer2011.Acc--DAB.accDamp--GLF.M-", "AD_M");
        map.put("-MrRacer2011.Acc--DAB.accDamp--GLF.V-", "AD_V");
        map.put("-MrRacer2011.Acc--DAB.accDamp--GLF.Q-", "AD_Q");

        map.put("-MrRacer2011.Acc--DAB.brakeDamp--GLF.B-", "BD_B");
        map.put("-MrRacer2011.Acc--DAB.brakeDamp--GLF.M-", "BD_M");
        map.put("-MrRacer2011.Acc--DAB.brakeDamp--GLF.V-", "BD_V");
        map.put("-MrRacer2011.Acc--DAB.brakeDamp--GLF.Q-", "BD_Q");

        map.put("-MrRacer2011.Clutch--Clutch.f--GLF.B-", "CLUTCH_B");
        map.put("-MrRacer2011.Clutch--Clutch.f--GLF.M-", "CLUTCH_M");
        map.put("-MrRacer2011.Clutch--Clutch.f--GLF.V-", "CLUTCH_V");
        map.put("-MrRacer2011.Clutch--Clutch.f--GLF.Q-", "CLUTCH_Q");
        map.put("-MrRacer2011.Clutch--Clutch.maxSpeed-", "CLUTCH_SPEED");

        map.put("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.fowardAngleD-", "OFF_F_ANGLE");
        map.put("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.forwardMinAcc-", "OFF_F_MIN");
        map.put("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.forwardMaxAcc-", "OFF_F_MAX");
        map.put("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.backwardAngleD-", "OFF_B_ANGLE");
        map.put("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.backwardMinAcc-", "OFF_B_MIN");
        map.put("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.backwardMaxAcc-", "OFF_B_MAX");

        DRIVE_PARAMS.add("-MrRacer2011.Plan--PLAN.BCC-");
        DRIVE_PARAMS.add("-MrRacer2011.Plan--PLAN.targetSpeeds--GLF.B-");
        DRIVE_PARAMS.add("-MrRacer2011.Plan--PLAN.targetSpeeds--GLF.M-");
        DRIVE_PARAMS.add("-MrRacer2011.Plan--PLAN.targetSpeeds--GLF.V-");
        DRIVE_PARAMS.add("-MrRacer2011.Plan--PLAN.targetSpeeds--GLF.Q-");

        DRIVE_PARAMS.add("-MrRacer2011.Acc--DAB.accDamp--GLF.B-");
        DRIVE_PARAMS.add("-MrRacer2011.Acc--DAB.accDamp--GLF.M-");
        DRIVE_PARAMS.add("-MrRacer2011.Acc--DAB.accDamp--GLF.V-");
        DRIVE_PARAMS.add("-MrRacer2011.Acc--DAB.accDamp--GLF.Q-");

        DRIVE_PARAMS.add("-MrRacer2011.Acc--DAB.brakeDamp--GLF.B-");
        DRIVE_PARAMS.add("-MrRacer2011.Acc--DAB.brakeDamp--GLF.M-");
        DRIVE_PARAMS.add("-MrRacer2011.Acc--DAB.brakeDamp--GLF.V-");
        DRIVE_PARAMS.add("-MrRacer2011.Acc--DAB.brakeDamp--GLF.Q-");

        DRIVE_PARAMS.add("-MrRacer2011.Clutch--Clutch.f--GLF.B-");
        DRIVE_PARAMS.add("-MrRacer2011.Clutch--Clutch.f--GLF.M-");
        DRIVE_PARAMS.add("-MrRacer2011.Clutch--Clutch.f--GLF.V-");
        DRIVE_PARAMS.add("-MrRacer2011.Clutch--Clutch.f--GLF.Q-");
        DRIVE_PARAMS.add("-MrRacer2011.Clutch--Clutch.maxSpeed-");

        RECOVERY_PARAMS.add("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.fowardAngleD-");
        RECOVERY_PARAMS.add("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.forwardMinAcc-");
        RECOVERY_PARAMS.add("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.forwardMaxAcc-");
        RECOVERY_PARAMS.add("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.backwardAngleD-");
        RECOVERY_PARAMS.add("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.backwardMinAcc-");
        RECOVERY_PARAMS.add("-MrRacer2011.Recovery--DFBB.offTrack--OTRB.backwardMaxAcc-");
    }    

    public static ArrayList<Individual2011> getAll(String fileName, String track,
            IndividualParser parser) {
        ArrayList<Individual2011> result = new ArrayList<Individual2011>();
        HashMap<String, Integer> header = new HashMap<String, Integer>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String line = reader.readLine();

            // Find header
            while (!line.startsWith("#         indno        genno fitnessValues")) {
                line = reader.readLine();
            }


            // Read header
            StringTokenizer tokenizer = new StringTokenizer(line.substring(1).trim()," \t", false);
            for(int i=0; tokenizer.hasMoreTokens(); ++i){
                header.put(tokenizer.nextToken(), i);
            }            

            line = reader.readLine();

            while (line != null && line.trim().length() > 0) {
                Individual2011 ind = parser.parse(line, track, header);
                ind.source = fileName;
                result.add(ind);
                line = reader.readLine();
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        //System.out.println("Read "+result.size()+" entries");

        return result;
    }

    public static Individual2011 getIndividual(String fileName, String track, int indID,
            IndividualParser parser) {
        ArrayList<Individual2011> list = getAll(fileName, track, parser);

        for (int i = 0; i < list.size(); ++i) {
            Individual2011 ind = list.get(i);
            if (ind.indID == indID) {
                return ind;
            }
        }

        return null;
    }


    public static void main(String[] args) {
        try {
            //String file = "F:\\Quad\\Experiments\\Torcs-Test4f\\Torcs-Testrun_0006inds.log";
            //String file = "F:\\Quad\\Experiments\\CIG-2011\\EA-Runs\\TestRun1-Wheel2-Noisy\\run01\\Torcs-Noisy_0001inds.log";
            //String file = "F:\\Quad\\Experiments\\CIG-2011\\EA-Runs\\TestRun2-Wheel2-Noisy\\run01\\Torcs-Noisy_0001inds.log";
            //String file = "F:\\Quad\\Experiments\\CIG-2011\\EA-Runs\\TestRun3-Wheel2-Noisy\\run01\\Torcs-Noisy_0001inds.log";
            //String file = "F:\\Quad\\Experiments\\CIG-2011\\EA-Runs\\TestRun4-Wheel2-Noisy\\run01\\Torcs-Noisy_0001inds.log";
            //String fileDrive = "F:\\Quad\\Experiments\\CIG-2011\\EA-Runs\\TestRun5-Wheel2-Noisy\\run01\\Torcs-Noisy_0001inds.log";
            //String fileDrive = "F:\\Quad\\Experiments\\SCRC2011-Gecco\\DirtOptimization\\Torcs-Dirt-Noisy_0001inds.log";
            //String fileDrive = "F:\\Quad\\Experiments\\SCRC2011-Gecco\\DirtOptimization\\run03-2011-06-30\\Torcs-Dirt-Noisy_0001inds.log";
            //String fileDrive = "F:\\Quad\\Experiments.alt\\SCRC2011-CIG\\clutch-optimization\\run11\\Torcs-CIG-with-clutch_0001inds.log";
            String fileDrive = "F:\\Quad\\Experiments\\Clutch\\Complete\\run-01\\Torcs-Complete_0001inds.log";
            int idDrive = 664+1;
            //String trackDrive = "Dirt-6";
            String trackDrive = "Wheel2";
            
            //String fileRecovery = "F:\\Quad\\Experiments\\SCRC2011-Gecco\\RecoveryOptimization\\run01-2011-06-17\\Torcs-Noisy_0001inds.log";
            //String fileRecovery = "F:\\Quad\\Experiments\\SCRC2011-Gecco\\RecoveryOptimization\\run02-2011-06-20\\Torcs-Noisy-Recovery_0001inds.log";
            String fileRecovery = "F:\\Quad\\Experiments.alt\\SCRC2011-Gecco\\RecoveryOptimization\\run03-2011-06-24\\Torcs-Noisy-Recovery_0001inds.log";
            int idRecovery = 591;
            String trackRecovery = "Wheel2";
            
            //String saveTo = "f:\\quad\\svn\\Diplomarbeit\\Code\\projects\\MrRacer\\src\\de\\janquadflieg\\mrracer\\data\\mrracer2011cig_a";
            String saveTo = "f:\\test";

            Individual2011 indDrive = PropertiesCreator.getIndividual(fileDrive, trackDrive, idDrive, new GenericParser(DRIVE_PARAMS, map));
            // only enable this for dirt settings!
            indDrive.properties.setProperty("-MrRacer2011.Acc--DAB.dos-", String.valueOf(false));
            System.out.println("Properties of the driving individual:");
            indDrive.properties.list(System.out);

            Individual2011 indRecovery = PropertiesCreator.getIndividual(fileRecovery, trackRecovery, idRecovery, new GenericParser(RECOVERY_PARAMS, map));
            System.out.println("Properties of the recovery individual:");
            indRecovery.properties.list(System.out);

            String comment = "Default parameters for MrRacer2011, driving behaviour from " + indDrive.source + " indID " + indDrive.indID
                    + ", fitness: ";
            Iterator<Map.Entry<String, Double>> it = indDrive.fitness.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Double> entry = it.next();
                comment += entry.getKey() + "=" + entry.getValue();
                if (it.hasNext()) {
                    comment += ", ";
                }
            }

            comment += ", recovery behaviour from " + indRecovery.source + " indID " + indRecovery.indID
                    + ", fitness: ";
            it = indRecovery.fitness.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Double> entry = it.next();
                comment += entry.getKey() + "=" + entry.getValue();
                if (it.hasNext()) {
                    comment += ", ";
                }
            }

            Properties p = indDrive.properties;
            p.putAll(indRecovery.properties);

            System.out.println(comment);
            p.list(System.out);

            FileOutputStream out = new FileOutputStream(new File(saveTo));
            p.store(out, comment);
            out.close();

            System.out.println("done");

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
