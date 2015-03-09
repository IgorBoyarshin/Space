package utils;

/**
 * Created by Igor on 07-Mar-15.
 */
public class Vector3d {
    public double x, y, z;

    public Vector3d() {
        x = 0.0d;
        y = 0.0d;
        z = 0.0d;
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f toFloat() {
        return new Vector3f((float) x, (float) y, (float) z);
    }

    public Vector3d copy() {
        return new Vector3d(x, y, z);
    }
}
