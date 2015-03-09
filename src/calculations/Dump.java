package calculations;

import utils.Vector3d;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor on 07-Mar-15.
 */
public class Dump {
    private String name;
    private List<DumpPlanet> planets;

    private int secondsInterval;

    public Dump() {
        name = "default";
        secondsInterval = 1;
        planets = new ArrayList<>();
    }

    // TODO: name is unnecessary
    public Dump(String name, int secondsInterval) {
        this.name = name;
        this.secondsInterval = secondsInterval;
        planets = new ArrayList<>();
    }

    public int getSecondsInterval() {
        return secondsInterval;
    }

    public int getAmountOfPlanets() {
        return planets.size();
    }

    public int getMaxSecond() {
        if (planets.get(0) != null) {
            return planets.get(0).getSize();
        }

        return 0;
    }

    public void loadData(String path) {
        System.out.println("### Loading Data from '" + path + "' ###");

        planets = new ArrayList<>(); // clear previous planets

        try {
            BufferedReader in = new BufferedReader(new FileReader(path));

//            name = in.readLine(); // Dump name
            secondsInterval = Integer.parseInt(in.readLine());
            int size = Integer.parseInt(in.readLine()); // amount of planets
            int amountOfSteps = Integer.parseInt(in.readLine());

            for (int i = 0; i < size; i++) {

                String name = in.readLine();
                if (name.charAt(0) == '%') { // Sign for redirecting
                    in.close();

                    System.out.println("### Redirecting Loading Data to '" + name.substring(1, name.length() - 1) + "' ###");
                    in = new BufferedReader(new FileReader(name.substring(1, name.length() - 1))); // Why 1???

                    name = in.readLine(); // Read the actual new name
                }

                double mass = Double.parseDouble(in.readLine());
                double radius = Double.parseDouble(in.readLine());

                DumpPlanet p = new DumpPlanet(name, mass, radius);
//                int secondsSize = Integer.parseInt(in.readLine());

                for (int j = 0; j < amountOfSteps; j++) {
//                    Vector3d pos = new Vector3d(Double.parseDouble(in.readLine()),
//                            Double.parseDouble(in.readLine()), 0.0d);
//                    Vector3d vel = new Vector3d(Double.parseDouble(in.readLine()),
//                            Double.parseDouble(in.readLine()), 0.0d);
//                    Vector3d acc = new Vector3d(Double.parseDouble(in.readLine()),
//                            Double.parseDouble(in.readLine()), 0.0d);

                    //TODO
                    Vector3d pos = new Vector3d(Double.parseDouble(in.readLine()),
                            Double.parseDouble(in.readLine()),Double.parseDouble(in.readLine()));
                    Vector3d vel = new Vector3d(Double.parseDouble(in.readLine()),
                            Double.parseDouble(in.readLine()),Double.parseDouble(in.readLine()));
                    Vector3d acc = new Vector3d(Double.parseDouble(in.readLine()),
                            Double.parseDouble(in.readLine()),Double.parseDouble(in.readLine()));

                    p.add(pos, vel, acc);
                }

                planets.add(p);
            }

            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveData(String path) {
        System.out.println("### Saving Data to '" + path + "' ###");

        final int maxFileLength = 20000;
        int curFileLength = 0;
        int curPieceNum = 0;

        try {
            PrintWriter out = new PrintWriter(new FileWriter(path));

//            out.println(name);
            out.println(secondsInterval);

            int size = planets.size();
            out.println(size);
            int amountOfSteps = planets.get(0).getSize(); // The same for every planet
            out.println(amountOfSteps);

            curFileLength += 3;

            for (int planetId = 0; planetId < size; planetId++) {
                DumpPlanet p = planets.get(planetId);
                out.println(p.getName());
                out.println(p.getMass());
                out.println(p.getRadius());

                curFileLength += 1;

                for (int j = 0; j < amountOfSteps; j++) {
                    out.println(p.getPosition(j).x);
                    out.println(p.getPosition(j).y);
                    out.println(p.getPosition(j).z);
                    out.println(p.getVelocity(j).x);
                    out.println(p.getVelocity(j).y);
                    out.println(p.getVelocity(j).z);
                    out.println(p.getAcceleration(j).x);
                    out.println(p.getAcceleration(j).y);
                    out.println(p.getAcceleration(j).z);

                    curFileLength += 9;

//                    out.print(p.getPosition(j).getX() + " ");
//                    out.print(p.getPosition(j).getY() + " ");
//                    out.print(p.getPosition(j).getZ() + " ");
//                    out.print(p.getVelocity(j).getX() + " ");
//                    out.print(p.getVelocity(j).getY() + " ");
//                    out.print(p.getVelocity(j).getZ() + " ");
//                    out.print(p.getAcceleration(j).getX() + " ");
//                    out.print(p.getAcceleration(j).getY() + " ");
//                    out.println(p.getAcceleration(j).getZ());
                }
                out.flush();

                if (curFileLength > maxFileLength && planetId < size - 1) {
                    curFileLength = 0;
                    curPieceNum++;

                    String ext = path.substring(path.length() - 4, path.length());
                    String name = path.substring(0, path.length() - 4);
                    out.print("%" + name + "_" + curPieceNum + ext + "%"); // thus we say where to look next for loader
                    out.flush();
                    out.close();

                    System.out.println("### Redirecting Data to '" + name + "_" + curPieceNum + ext + "' ###");
                    out = new PrintWriter(new FileWriter(name + "_" + curPieceNum + ext));
                }
            }

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPlanet(DumpPlanet p) {
        planets.add(p);
    }

    public DumpPlanet getPlanet(int index) {
        if (index < 0 || index >= planets.size()) {
            return null;
        }

        return planets.get(index);
    }

    public DumpPlanet getPlanet(String name) {
        for (DumpPlanet p : planets) {
            if (p.getName().equals(name)) {
                return p;
            }
        }

        return null;
    }
}
