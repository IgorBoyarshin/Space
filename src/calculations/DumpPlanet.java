package calculations;

import utils.Vector3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor on 07-Mar-15.
 */
public class DumpPlanet {
    private String name;
    private double mass;
    private double radius;

    private List<Vector3d> position;
    private List<Vector3d> velocity;
    private List<Vector3d> acceleration;

    public DumpPlanet(String name, double mass, double radius) {
        position = new ArrayList<>();
        velocity = new ArrayList<>();
        acceleration = new ArrayList<>();

        this.name = name;
        this.mass = mass;
        this.radius = radius;
    }

    public int getSize() {
        return position.size();
    }

    public void add(Vector3d p, Vector3d v, Vector3d a) {
        position.add(p);
        velocity.add(v);
        acceleration.add(a);
    }

    public Vector3d getPosition(int second) {
        if (second < 0 || second >= position.size()) {
            return null;
        }

        return position.get(second);
    }

    public Vector3d getVelocity(int second) {
        if (second < 0 || second >= velocity.size()) {
            return null;
        }

        return velocity.get(second);
    }

    public Vector3d getAcceleration(int second) {
        if (second < 0 || second >= acceleration.size()) {
            return null;
        }

        return acceleration.get(second);
    }

    public String getName() {
        return name;
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }
}
