package utils;

/**
 * Created by Igor on 14-Mar-15.
 */
public class Face {
    public Vector3s vertex = new Vector3s();
    public Vector3s normal = new Vector3s();

    public Face(Vector3s vertex, Vector3s normal) {
        this.vertex = vertex;
        this.normal = normal;
    }
}
