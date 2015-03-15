package calculations;

import utils.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Igor on 07-Mar-15.
 */
public class ControlCenter {
    private int currentStep = 0;
    private int stepDuration = 1; // In seconds
    // Only for calculating live
    private int applyForceDuration = 1;

    private Dump currentDump;
    private List<CalculationsPlanet> calculationsPlanets;

    // Settings
    private static final int percentPrintFrequency = 1;

    private Mode currentMode;

    public static enum Mode {
        DUMP,
        LIVE_CALCULATING
    }

    public ControlCenter() {

    }

    /*
        Objects will get altered as they will be being used now
    */
    public void calculateForObjects(List<CalculationsPlanet> objects, int applyForceDuration) {
        currentMode = Mode.LIVE_CALCULATING;
        currentDump = null;
        calculationsPlanets = objects;

        this.stepDuration = 1; // Will not be used. We process every single second
        this.currentStep = 0;
        this.applyForceDuration = applyForceDuration;

        System.out.println(":> Will process further for objects");
        System.out.println();
    }

    public void useDump(String name) {
        currentMode = Mode.DUMP;
        currentDump = new Dump();
        calculationsPlanets = null;

        System.out.println(":> Will load dump '" + name + "'");
        System.out.println();
        currentDump.loadData("dumps//" + name + ".scd");

        System.out.println();
        System.out.println(":> Finished loading Data from '" + name + "'");

        currentStep = 0;
        stepDuration = currentDump.getSecondsInterval();

        System.out.println(":> Dump '" + name + "' is now primary");
    }

    // -=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+
    // -=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+
    // -=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+-=+

    public void createDump(String name, int seconds, int accuracy, int stepDuration, List<CalculationsPlanet> calculationsPlanets) {
        long startTime = System.currentTimeMillis();

        int y = div(seconds, 365 * 24 * 3600);
        int d = div(seconds - y * (365 * 24 * 3600), 24 * 3600);
        int h = div(seconds - y * (365 * 24 * 3600) - d * (24 * 3600), 3600);
        int s = (seconds - y * (365 * 24 * 3600) - d * (24 * 3600) - h * (3600));

        Dump dump = new Dump(name, stepDuration);
        System.out.println("### Creating Dump ###");
        System.out.println("Name: " + name);

        System.out.print("Time: ");
        if (y > 0) {
            System.out.print(y + "years ");
        }
        if (d > 0) {
            System.out.print(d + "days ");
        }
        if (h > 0) {
            System.out.print(h + "hours ");
        }
        if (s > 0) {
            System.out.print(s + "seconds");
        }
        System.out.println();

        System.out.println("Accuracy: " + accuracy);
        System.out.println("Step duration: " + stepDuration + " seconds");
        System.out.println("For planets:");

        int r = 0;
        for (CalculationsPlanet p : calculationsPlanets) {
            String n = p.getName();

            System.out.print(" " + n);
            dump.addPlanet(new DumpPlanet(n, p.getMass(), p.getRadius()));
            dump.getPlanet(r).add(p.getPosition().copy(), p.getVelocity().copy(), p.getAcceleration().copy());
            r++;
        }
        System.out.println();

        int percent = 1;
        for (int i = 1; i < seconds; i++) {
            if (i % accuracy == 0) {
                processNextSecond(calculationsPlanets, true);
            } else {
                processNextSecond(calculationsPlanets, false);
            }

            // Every stepDuration we dump the current set of attributes
            if (i % stepDuration == 0) {
                for (int j = 0; j < calculationsPlanets.size(); j++) {
                    CalculationsPlanet p = calculationsPlanets.get(j);
                    dump.getPlanet(j).add(p.getPosition().copy(), p.getVelocity().copy(), p.getAcceleration().copy());
                }
            }

            if (seconds / 100 * percent == i) {
                if (percent % percentPrintFrequency == 0) { // percent frequency
                    System.out.println(percent + "% complete.");
                }

                percent += 1;
            }
        }
        System.out.println("100% complete.");

        long delta = System.currentTimeMillis() - startTime;
        System.out.println("### Finished creating Dump ###");
        System.out.println("## Took " + delta / 1000 + "." + (delta - (delta / 1000)) + " seconds ##");

        currentDump = dump;
        System.out.println();
        System.out.println(":> Successfully finished creating dump");
        System.out.println(":> Dump '" + name + "' is now primary");
        System.out.println();

        dump.saveData("dumps//" + name + ".scd");

        System.out.println();
        System.out.println(":> Finished saving Data to '" + name + "'");
//        System.out.println();
    }

    public void processNextStep() {
        if (currentMode.equals(Mode.DUMP)) {
            if (currentStep + 1 <= currentDump.getMaxSecond()) {
                currentStep++;
            }
        } else if (currentMode.equals(Mode.LIVE_CALCULATING)) {
            processNextSecond();
            currentStep++;
        }
    }

    public void processNextSteps(int n) {
        if (currentMode.equals(Mode.DUMP)) {
            if (currentStep + n <= currentDump.getMaxSecond()) {
                currentStep += n;
            }
        } else if (currentMode.equals(Mode.LIVE_CALCULATING)) {
            for (int i = 0; i < n; i++) {
                processNextSecond();
                currentStep++;
            }
        }
    }

    // To be used while calculating live
    private void processNextSecond() {
        if (currentStep % applyForceDuration == 0) {
            processNextSecond(calculationsPlanets, true);
        } else {
            processNextSecond(calculationsPlanets, false);
        }
    }

    // To be used while creating a dump
    private void processNextSecond(List<CalculationsPlanet> calculationsPlanets, boolean applyForce) {
        if (applyForce) {
            for (int i = 0; i < calculationsPlanets.size(); i++) {
                calculationsPlanets.get(i).setAcceleration(new Vector3d());
            }

            for (int i = 0; i < calculationsPlanets.size(); i++) {
                for (int j = i + 1; j < calculationsPlanets.size(); j++) {
                    gravity(calculationsPlanets.get(i), calculationsPlanets.get(j), true, true, false);
                }
            }
        }

        for (CalculationsPlanet p : calculationsPlanets) {
            p.applyMovement();
        }
    }

    private double gravity(CalculationsPlanet a, CalculationsPlanet b, boolean applyToA, boolean applyToB, boolean returnForce) {
        double G = 6.67e-11d;

        double m1 = a.getMass();
        double m2 = b.getMass();
        double x1 = a.getPosition().x;
        double x2 = b.getPosition().x;
        double y1 = a.getPosition().y;
        double y2 = b.getPosition().y;
        double z1 = a.getPosition().z;
        double z2 = b.getPosition().z;

        double rSqrX = (x1 - x2) * (x1 - x2);
        double rSqrY = (y1 - y2) * (y1 - y2);
        double rSqrZ = (z1 - z2) * (z1 - z2);
        double r = Math.sqrt(rSqrX + rSqrY + rSqrZ);

        // It's not the actual gravity. It's without masses for future purposes
        double gravity = G / (rSqrX + rSqrY + rSqrZ);

        if (applyToA) {
            double acc = gravity * m2 / r;
            a.addAcceleration(new Vector3d(acc * (x2 - x1), acc * (y2 - y1), acc * (z2 - z1)));
        }

        if (applyToB) {
            double acc = gravity * m1 / r;
            b.addAcceleration(new Vector3d(acc * (x1 - x2), acc * (y1 - y2), acc * (z1 - z2)));
        }

        double force = 1.0d;

        if (returnForce) {
            force = G * m1 * m2 / (rSqrX + rSqrY + rSqrZ);
        }

        return force;
    }

    public List<CalculationsPlanet> getPlanetsFromDumpForStep(int step) {
        if (currentMode.equals(Mode.DUMP)) {
            List<CalculationsPlanet> list = new ArrayList<>();
            for (int i = 0; i < currentDump.getAmountOfPlanets(); i++) {
                DumpPlanet p = currentDump.getPlanet(i);
                CalculationsPlanet planet = new CalculationsPlanet(
                        p.getName(), p.getPosition(step), p.getVelocity(step), p.getAcceleration(step),
                        p.getMass(), p.getRadius());

                list.add(planet);
            }
            return list;
        }

        return null;
    }

    public int getMaxDumpStep() {
        if (currentMode.equals(Mode.DUMP)) {
            return currentDump.getMaxSecond();
        }

        return -1;
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    public double getBiggestObjectRadius() {
        double max = 1.0f;

        if (currentMode.equals(Mode.DUMP)) {
            for (int i = 0; i < currentDump.getAmountOfPlanets(); i++) {
                double radius = currentDump.getPlanet(i).getRadius();
                if (radius > max) {
                    max = radius;
                }
            }
        } else if (currentMode.equals(Mode.LIVE_CALCULATING)) {
            for (int i = 0; i < calculationsPlanets.size(); i++) {
                double radius = calculationsPlanets.get(i).getRadius();
                if (radius > max) {
                    max = radius;
                }
            }
        }

        return max;
    }

    public double getMaxRemoteness() {
        double max = 1.0f;

        if (currentMode.equals(Mode.DUMP)) {
            for (int i = 0; i < currentDump.getAmountOfPlanets(); i++) {
                double distanceScr = distanceScrBetween(new Vector3d(), currentDump.getPlanet(i).getPosition(0));
                if (distanceScr > max) {
                    max = distanceScr;
                }
            }
        } else if (currentMode.equals(Mode.LIVE_CALCULATING)) {
            for (int i = 0; i < calculationsPlanets.size(); i++) {
                double distanceScr = distanceScrBetween(new Vector3d(), calculationsPlanets.get(i).getPosition());
                if (distanceScr > max) {
                    max = distanceScr;
                }
            }
        }

        return Math.sqrt(max);
    }

    private double distanceScrBetween(Vector3d a, Vector3d b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double dz = a.z - b.z;

        return (dx * dx + dy * dy + dz * dz);
    }

    public DumpPlanet getDumpPlanet(int planetId) {
        if (currentDump != null) {
            return currentDump.getPlanet(planetId);
        }
        return null;
    }

    public int getPlanetsSize() {
        if (currentMode.equals(Mode.DUMP)) {
            return currentDump.getAmountOfPlanets();
        } else if (currentMode.equals(Mode.LIVE_CALCULATING)) {
            return calculationsPlanets.size();
        }

        return -1;
    }

    public int getStepDuration() {
        return stepDuration;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getCurrentSecond() {
        return currentStep * stepDuration;
    }

    public void setCurrentStep(int step) {
        if (currentMode.equals(Mode.DUMP)) {
            if (step >= 0 && step <= currentDump.getMaxSecond()) {
                currentStep = step;
            }
        }
    }

    public void incCurrentStep() {
        if (currentMode.equals(Mode.DUMP)) {
            if (currentStep < currentDump.getMaxSecond()) {
                currentStep++;
            }
        } else if (currentMode.equals(Mode.LIVE_CALCULATING)) {
            currentStep++;
            processNextSecond();
        }
    }

    public String getPlanetName(int id) {
        if (currentMode.equals(Mode.DUMP)) {
            if (id < currentDump.getAmountOfPlanets()) {
                return currentDump.getPlanet(id).getName();
            }
        } else if (currentMode.equals(Mode.LIVE_CALCULATING)) {
            if (id < calculationsPlanets.size()) {
                return calculationsPlanets.get(id).getName();
            }
        }

        return "";
    }

    public double getPlanetRadius(int id) {
        if (currentMode.equals(Mode.DUMP)) {
            if (id < currentDump.getAmountOfPlanets()) {
                return currentDump.getPlanet(id).getRadius();
            }
        } else if (currentMode.equals(Mode.LIVE_CALCULATING)) {
            if (id < calculationsPlanets.size()) {
                return calculationsPlanets.get(id).getRadius();
            }
        }

        return -1.0d;
    }

    public Dataset getDatasetForStep(int step) {
        if (currentMode.equals(Mode.DUMP)) {
            List<Vector3d> planetsForDataset = new ArrayList<>();
            for (int i = 0; i < currentDump.getAmountOfPlanets(); i++) {
                planetsForDataset.add(currentDump.getPlanet(i).getPosition(step));
            }

            return new Dataset(planetsForDataset);
        }

        return null;
    }

    public Dataset getDatasetForThisStep() {
        List<Vector3d> planetsForDataset = new ArrayList<>();

        if (currentMode.equals(Mode.DUMP)) {
            for (int i = 0; i < currentDump.getAmountOfPlanets(); i++) {
                Vector3d pos = currentDump.getPlanet(i).getPosition(currentStep);
                planetsForDataset.add(pos);
            }
        } else if (currentMode.equals(Mode.LIVE_CALCULATING)) {
            for (int i = 0; i < calculationsPlanets.size(); i++) {
                Vector3d pos = calculationsPlanets.get(i).getPosition();
                planetsForDataset.add(pos);
            }
        }

        return new Dataset(planetsForDataset);
    }


    public List<CalculationsPlanet> initRandomObjects() {
        List<CalculationsPlanet> list = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            list.add(generateRandomObject(1.827e12d, 2.41e4d));
        }

        return list;
    }

    public CalculationsPlanet generateRandomObject(double maxPosition, double maxSpeed) {
        Random r = new Random();
        Vector3d pos = new Vector3d(r.nextDouble() * maxPosition, r.nextDouble() * maxPosition, r.nextDouble() * maxPosition);
//        Vector3d vel = new Vector3d(r.nextDouble() * maxSpeed, r.nextDouble() * maxSpeed, r.nextDouble() * maxSpeed);
        Vector3d vel = new Vector3d();
        int name = r.nextInt();
        double mass = 5.976e29d;
        CalculationsPlanet calculationsPlanet = new CalculationsPlanet("p" + name, pos, vel, new Vector3d(), mass, 6786e3d / 2.0d);
        return calculationsPlanet;
    }

    public List<CalculationsPlanet> initSunAndEarthWithoutVelocity() {
        // 5601600 seconds, ~65 days
        List<CalculationsPlanet> list = new ArrayList<>();

        list.add(new CalculationsPlanet("Sun", new Vector3d(), new Vector3d(), new Vector3d(), 1.989e30d, 696e6d));
        list.add(new CalculationsPlanet("Earth", new Vector3d(1.496e11d, 0.0d, 0.0d), new Vector3d(0.0d, 0.0d, 0.0d),
                new Vector3d(), 5.976e24d, 6378e3d));

        return list;
    }

    public List<CalculationsPlanet> initSunAndEarth() {
        List<CalculationsPlanet> list = new ArrayList<>();

        list.add(new CalculationsPlanet("Sun", new Vector3d(), new Vector3d(), new Vector3d(), 1.989e30d, 696e6d));
        list.add(new CalculationsPlanet("Earth", new Vector3d(1.496e11d, 0.0d, 0.0d),
                new Vector3d(0.0d, 0.0d, 2.979e4d), new Vector3d(), 5.976e24d, 6378e3d));

        return list;
    }

    public List<CalculationsPlanet> initPlanets() {
        List<CalculationsPlanet> list = new ArrayList<>();

        list.add(initSun());
        list.add(initMercury());
        list.add(initVenus());
        list.add(initEarth());
        list.add(initMars());
        list.add(initJupiter());
        list.add(initSaturn());
        list.add(initUranus());
        list.add(initNeptune());

        return list;
    }


    private CalculationsPlanet initSun() {
        //Planet Sun = new Planet("Sun", new Vector(), new Vector(), new Vector(), 332946 * 5.976e24d, 696e6d);
        CalculationsPlanet Sun = new CalculationsPlanet("Sun", new Vector3d(), new Vector3d(), new Vector3d(), 1.989e30d, 696e6d);
        return Sun;
    }

    private CalculationsPlanet initMercury() {
        CalculationsPlanet Mercury = new CalculationsPlanet("Mercury", new Vector3d(-5.79e10d, 0.0d, 0.0d), new Vector3d(0.0d, 0.0d, -4.787e4d), new Vector3d(), 5.976e24d * 0.055d, 4878e3d / 2.0d);
        return Mercury;
    }

    private CalculationsPlanet initVenus() {
        CalculationsPlanet Venus = new CalculationsPlanet("Venus", new Vector3d(0.0d, 0.0d, -1.082e11d), new Vector3d(3.503e4d, 0.0d, 0.0d), new Vector3d(), 5.976e24d * 0.815d, 12103e3d / 2.0d);
        return Venus;
    }

    private CalculationsPlanet initEarth() {
        CalculationsPlanet Earth = new CalculationsPlanet("Earth", new Vector3d(1.496e11d, 0.0d, 0.0d), new Vector3d(0.0d, 0.0d, 2.979e4d), new Vector3d(), 5.976e24d, 6378e3d);
        //Planet Earth = new Planet("Earth", new Vector(1.496e11d, 0.0d, 0.0d), new Vector(0.0d, 0.0d, 0.0d), new Vector(), 5.976e24d, 6378e3d);
        return Earth;
    }

    private CalculationsPlanet initMars() {
        CalculationsPlanet Mars = new CalculationsPlanet("Mars", new Vector3d(0.0d, 0.0d, 2.279e11d), new Vector3d(-2.41e4d, 0.0d, 0.0d), new Vector3d(), 5.976e24d * 0.108d, 6786e3d / 2.0d);
        return Mars;
    }

    private CalculationsPlanet initJupiter() {
        CalculationsPlanet Jupiter = new CalculationsPlanet("Jupiter", new Vector3d(-7.783e11d, 0.0d, 0.0d), new Vector3d(0.0d, 0.0d, -1.31e4d), new Vector3d(), 5.976e24d * 317.9d, 143.0e6 / 2.0d);
        return Jupiter;
    }

    private CalculationsPlanet initSaturn() {
        CalculationsPlanet Saturn = new CalculationsPlanet("Saturn", new Vector3d(0.0d, 0.0d, -1.427e12d), new Vector3d(9.6e3d, 0.0d, 0.0d), new Vector3d(), 5.976e24d * 95.181d, 120.0e6d / 2.0d);
        return Saturn;
    }

    private CalculationsPlanet initUranus() {
        CalculationsPlanet Uranus = new CalculationsPlanet("Uranus", new Vector3d(2.8966e12d, 0.0d, 0.0d), new Vector3d(0.0d, 0.0d, 6.8e3d), new Vector3d(), 5.976e24d * 14.531d, 51118.0e3d / 2.0d);
        return Uranus;
    }

    private CalculationsPlanet initNeptune() {
        //Planet Neptune = new Planet("Neptune", new Vector(0.0d, 4.4966e12d, 0.0d), new Vector(-5.4e3d, 0.0d, 0.0d), new Vector(), 5.976e24d * 17.135d, 49528.0e3d / 2.0d);
        CalculationsPlanet Neptune = new CalculationsPlanet("Neptune", new Vector3d(0.0d, 0.0d, 4.4966e12d), new Vector3d(-5.4e3d, 0.0d, 0.0d), new Vector3d(), 102.4e24d, 49528.0e3d / 2.0d);
        return Neptune;
    }

    private int div(int a, int b) {
        return (a - a % b) / b;
    }
}
