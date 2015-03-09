package calculations;

import utils.Vector3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor on 07-Mar-15.
 */
public class Planet {
    private Vector3d startPosition;

    private List<Vector3d> tail = new ArrayList<>();

    private String name;
    private Vector3d position;
    private Vector3d velocity;
    private Vector3d acceleration;
    private double mass;
    private double radius;

    public Planet(String name, Vector3d position, Vector3d velocity,
                  Vector3d acceleration, double mass, double radius) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.mass = mass;
        this.radius = radius;

        startPosition = new Vector3d(position.x, position.y, position.z);
    }

    public Vector3d getPosition() {
        return position;
    }

    public Vector3d getVelocity() {
        return velocity;
    }

    public Vector3d getAcceleration() {
        return acceleration;
    }

    public void addAcceleration(Vector3d a) {
        acceleration.x = acceleration.x + a.x;
        acceleration.y = acceleration.y + a.y;
        acceleration.z = acceleration.z + a.z;
    }

    public void setAcceleration(Vector3d a) {
        acceleration.x = a.x;
        acceleration.y = a.y;
        acceleration.z = a.z;
    }

    public void applyMovement() {
        position.x = position.x + velocity.x + acceleration.x/2;
        position.y = position.y + velocity.y + acceleration.y/2;
        position.z = position.z + velocity.z + acceleration.z/2;

        velocity.x = velocity.x + acceleration.x;
        velocity.y = velocity.y + acceleration.y;
        velocity.z = velocity.z + acceleration.z;

//        position.setX(position.getX() + velocity.getX() + acceleration.getX() / 2);
//        position.setY(position.getY() + velocity.getY() + acceleration.getY() / 2);
//        position.setZ(position.getZ() + velocity.getZ() + acceleration.getZ() / 2);

//        velocity.setX(velocity.getX() + acceleration.getX());
//        velocity.setY(velocity.getY() + acceleration.getY());
//        velocity.setZ(velocity.getZ() + acceleration.getZ());
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

    public Vector3d getStartPosition() {
        return startPosition;
    }
}
