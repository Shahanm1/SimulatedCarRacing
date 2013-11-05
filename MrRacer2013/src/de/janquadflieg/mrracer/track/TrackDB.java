/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.track;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 *
 * @author Jan Quadflieg
 */
public class TrackDB
        implements Serializable {

    public static final String PRECOMPILE_TRACK = "precompile-track";
    public static final TrackModel UNKNOWN_MODEL = null;
    static final long serialVersionUID = 543417241306257651L;
    private ArrayList<TrackModel> tracks = new ArrayList<>();
    private static final double EPSILON = 0.5;
    //private HashMap<Integer, HashMap<Integer, ArrayList<TrackModel>>> fastLookupMap =
    //         new HashMap<Integer, HashMap<Integer, ArrayList<TrackModel>>>();

    public TrackDB() {
    }

    public void add(TrackModel tm) {
        tracks.add(tm);
    }

    public TrackModel get(int position, double distance, double width) {
        ArrayList<TrackModel> results = new ArrayList<>();

        for (TrackModel tm : tracks) {
            //tm.print();
            //System.out.println(tm.getName()+" "+position+" "+tm.getGridPosition(position));
            double d = tm.getGridPosition(position);
            double w = tm.getWidth();

            //System.out.println((distance-EPSILON)+" "+d+" "+(distance+EPSILON));

            if (distance - EPSILON < d && d < distance + EPSILON &&
                    width - EPSILON < w && w < width + EPSILON) {
                //return tm;
                results.add(tm);
            }
        }

        if (results.isEmpty()) {
            return null;

        } else {
            return results.get(0);

        }
    }

    public TrackModel getByName(String name){
        ArrayList<TrackModel> results = new ArrayList<>();

        for (TrackModel tm : tracks) {
            if (tm.getName().equalsIgnoreCase(name)) {
                //return tm;
                results.add(tm);
            }
        }

        if (results.isEmpty()) {
            return UNKNOWN_MODEL;

        } else {
            return results.get(0);
        }
    }

    public static TrackDB create() {
        try {
            InputStream in = new Object().getClass().getResourceAsStream("/de/janquadflieg/mrracer/data/trackdb");
            ZipInputStream zin = new ZipInputStream(in);
            ZipEntry entry = zin.getNextEntry();
            ObjectInputStream oin = new ObjectInputStream(zin);
            TrackDB result = (TrackDB) oin.readObject();

            oin.close();

            TrackDB.loadTracks(result);

            return result;

        } catch (Exception e) {
            e.printStackTrace(System.out);

            return new TrackDB();
        }
    }

    private static void loadTracks(TrackDB trackDB) {
        java.io.File currentDirectory = new java.io.File(".");
        java.io.File[] files = currentDirectory.listFiles();
        //System.out.println("Looking for trackmodels in dir " + currentDirectory.getAbsolutePath());

        for (java.io.File f : files) {
            if (f.isFile() && f.getName().endsWith(TrackModel.TM_EXT)) {
                try {
                    String filename = "." + java.io.File.separator + f.getName();
                    //System.out.println("Loading trackmodel from file \"" + filename + "\".");
                    trackDB.add(TrackModel.load(filename));

                } catch (Exception e) {
                    System.out.println(f);
                    e.printStackTrace(System.out);
                }
            }
        }
    }

    public int size(){
        return this.tracks.size();
    }

    public TrackModel get(int i){
        return tracks.get(i);
    }

    public static void main(String[] args) {
        try {
//            TrackDB loaded = TrackDB.create();
//            for (TrackModel t : loaded.tracks) {
//                t.print();
//                System.out.println("");
//                System.out.println(t.getName());
//                System.out.println("Apexes:");
//                for(int i=0; i < t.size(); ++i){
//                    TrackSegment ts = t.getSegment(i);
//                    if(ts.isCorner()){
//                        System.out.println(ts.toString());
//                        double[] a = ts.getApexes();
//                        for(int j=0; j < a.length; ++j){
//                            String s = "["+j+"] "+Utils.dTS(a[j])+" / ";
//                            s += Utils.dTS(a[j]-ts.getStart()) + " ";
//                            if(ts.getType() != Situations.TYPE_FULL && j==0){
//                                s += Utils.dTS(ts.getSubSegment(0).getLength()/t.getWidth())+" ";
//                            }
//                            s += Situations.toShortString(ts.getSubSegment(ts.getIndex(a[j])).getType())+" ";
//                            System.out.print(s);
//                        }
//                        System.out.println("");
//                    }
//                }
//                System.out.println("");
//            }
            if (1 != 1) {
                return;
            }



            // code, um die datenbank zu erzeugen
            TrackDB db = new TrackDB();

            TrackModel tm;
            //File directory = new File("/home/quad/Diplomarbeit/Data/Tracks/NewTrackModels");
            //File directory = new File("g:\\Studium\\Diplomarbeit\\Data\\Tracks\\TrackModels");
            File directory = new File("f:\\quad\\svn\\Diplomarbeit\\Data\\Tracks\\NewTrackModels");
            
            File[] files = directory.listFiles();

            System.out.println(directory+" "+files.length+" files");

            java.util.Arrays.sort(files);

            for (int i = 0; i < files.length; ++i) {
                if (!(files[i].isDirectory() ||
                        files[i].getAbsolutePath().contains(".svn") ||
                        files[i].getAbsolutePath().endsWith("~"))) {
                    System.out.println(files[i].getAbsolutePath());
                    tm = TrackModel.load(files[i].getAbsolutePath());
                    db.add(tm);
                //System.out.println("");
                    tm.print();
                }
            }

//            Iterator<Integer> it = db.fastLookupMap.keySet().iterator();
//            while(it.hasNext()){
//                //System.out.println("***********************************");
//                Integer width = it.next();
//                //System.out.println("Width: "+width);
//                HashMap<Integer, ArrayList<TrackModel>> map = db.fastLookupMap.get(width);
//
//                Iterator<Integer> it2 = map.keySet().iterator();
//                while(it2.hasNext()){
//                    Integer position = it2.next();
//                    //System.out.println("Position: "+position);
//                    ArrayList<TrackModel> list = map.get(position);
//                    Iterator<TrackModel> it3 = list.iterator();
//                    while(it3.hasNext()){
//                        TrackModel model = it3.next();
//                        //System.out.println(model.getName()+", "+model.getGridPosition(position));
//                    }
//                }
//            }

            boolean zip = true;
            //String fileName = "/home/quad/Diplomarbeit/Code/projects/MrRacer/src/de/janquadflieg/mrracer/data/trackdb";
            //String fileName = "g:\\Studium\\Diplomarbeit\\Code\\projects\\MrRacer\\src\\de\\janquadflieg\\mrracer\\data\\trackdb";
            String fileName = "f:\\quad\\svn\\Diplomarbeit\\Code\\projects\\MrRacer\\src\\de\\janquadflieg\\mrracer\\data\\trackdb";

            FileOutputStream fo = new FileOutputStream(fileName);
            ObjectOutputStream oo = null;
            ZipOutputStream zo = null;

            if (zip) {
                zo = new ZipOutputStream(fo);

                ZipEntry entry = new ZipEntry("trackdb");
                zo.putNextEntry(entry);
                zo.setLevel(9);

                oo = new ObjectOutputStream(zo);

            } else {
                oo = new ObjectOutputStream(fo);
            }

            oo.writeObject(db);
            oo.flush();

            if (zip) {
                zo.closeEntry();
            }

            oo.close();
            fo.close();

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}