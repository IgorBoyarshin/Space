import utils.Matrix4f;
import utils.Vector3f;

/**
 * Created by Igor on 08-Mar-15.
 */
public class Camera {
    private static float movementSpeed = 0.14f;
    private static final float rotationSpeed = 0.005f;

    private static final float movementSpeed0 = 0.14f * 0.3f;
    private static final float movementSpeed1 = 0.14f * 1.0f;
    private static final float movementSpeed2 = 0.14f * 5.0f;
    private static final float movementSpeed3 = 0.14f * 10.0f;
    private static final float movementSpeed4 = 0.14f * 50.0f;

    private Vector3f position;
    private float pitch, yaw; // in radians

    private final float PI = 3.14159f;
    private final float maxPitch = 0.98f * (PI / 2);
    private final float minPitch = -0.98f * (PI / 2);

    public Camera(Vector3f position, float pitch, float yaw) {
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void setMovementSpeed0() {
        movementSpeed = movementSpeed0;
    }

    public void setMovementSpeed1() {
        movementSpeed = movementSpeed1;
    }

    public void setMovementSpeed2() {
        movementSpeed = movementSpeed2;
    }

    public void setMovementSpeed3() {
        movementSpeed = movementSpeed3;
    }

    public void setMovementSpeed4() {
        movementSpeed = movementSpeed4;
    }

    public void setPosition(Vector3f vector) {
        position = vector;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void moveForward() {
        position.x -= movementSpeed * (float) Math.cos(PI / 2.0f - yaw);
        position.y -= movementSpeed * (float) Math.cos(PI / 2.0f - pitch);
        position.z += movementSpeed * (float) (Math.cos(pitch) * Math.cos(yaw));

//        position.x += movementSpeed * (float) Math.cos(yaw);
//        position.y += movementSpeed * (float) Math.sin(pitch);
//        position.z += movementSpeed * (float) (Math.cos(pitch) * Math.cos(yaw));
    }

    public void moveBackwards() {
        position.x += movementSpeed * (float) Math.cos(PI / 2.0f - yaw);
        position.y += movementSpeed * (float) Math.cos(PI / 2.0f - pitch);
        position.z -= movementSpeed * (float) (Math.cos(pitch) * Math.cos(yaw));
    }

    // NO Y
    public void moveLeft() {
        position.x += movementSpeed * (float) Math.cos(yaw);
//        position.y -= movementSpeed * (float) Math.cos(PI / 2.0f - pitch);
        position.z += movementSpeed * (float) (Math.sin(yaw));
    }

    // NO Y
    public void moveRight() {
        position.x -= movementSpeed * (float) Math.cos(yaw);
//        position.y += movementSpeed * (float) Math.cos(PI / 2.0f - pitch);
        position.z -= movementSpeed * (float) (Math.sin(yaw));
    }

    public void moveUp() {
        position.y += movementSpeed;
    }

    public void moveDown() {
        position.y -= movementSpeed;
    }

    public void translate(Vector3f vector) {
        position.x += vector.x;
        position.y += vector.y;
        position.z += vector.z;
    }

    public void addPitch(float angle) {
        pitch += angle * rotationSpeed;

        if (pitch > maxPitch) {
            pitch = maxPitch;
        }
        if (pitch < minPitch) {
            pitch = minPitch;
        }

//        System.out.println("Pitch: " + toDegrees(pitch) + " Yaw: " + toDegrees(yaw));
    }

    public void setPitch(float angle) {
        if (angle < maxPitch && angle > minPitch) {
            pitch = angle;
        }
    }

    public void addYaw(float angle) {
        yaw += angle * rotationSpeed;

        if (yaw > 2 * PI) {
            yaw -= 2 * PI;
        }
        if (yaw < 0) {
            yaw += 2 * PI;
        }

//        System.out.println("Pitch: " + toDegrees(pitch) + " Yaw: " + toDegrees(yaw));
    }

    public void setYaw(float angle) {
        yaw = angle;
    }

    public float toDegrees(float radians) {
        return radians * 360.0f / 2.0f / PI;
    }

    public Vector3f getPosition() {
        return new Vector3f(-position.x, -position.y, position.z);
    }

    public Matrix4f getMatrixNoPos() {
        Matrix4f matrix = Matrix4f.identity();
        Matrix4f rot1 = Matrix4f.rotate(toDegrees(pitch), 1.0f, 0.0f, 0.0f);
        Matrix4f rot2 = Matrix4f.rotate(toDegrees(yaw), 0.0f, 1.0f, 0.0f);
        matrix = rot1.multiply(rot2);

        return matrix;
    }

    public Matrix4f getMatrix() {
        Matrix4f matrix = Matrix4f.identity();
        Matrix4f rot1 = Matrix4f.rotate(toDegrees(pitch), 1.0f, 0.0f, 0.0f);
        Matrix4f rot2 = Matrix4f.rotate(toDegrees(yaw), 0.0f, 1.0f, 0.0f);
        Matrix4f pos = Matrix4f.translate(new Vector3f(position.x, -position.y, position.z));
        matrix = rot1.multiply(rot2).multiply(pos);

        return matrix;
    }
}
