package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor on 14-Mar-15.
 */

public class Model {
    public List<Vector3f> vertices = new ArrayList<Vector3f>();
    public List<Vector3f> normals = new ArrayList<Vector3f>();
    public List<Face> faces = new ArrayList<Face>();

    public Model() {

    }

    public float[] getVerticesArray() {
        float[] array = new float[vertices.size() * 3];

        for (int i = 0; i < vertices.size(); i++) {
            Vector3f vector = vertices.get(i);

            array[3 * i] = vector.x;
            array[3 * i + 1] = vector.y;
            array[3 * i + 2] = vector.z;
        }

        return array;
    }

    public float[] getNormalsArray() {
        float[] array = new float[faces.size() * 3];

        for (int i = 0; i < faces.size(); i++) {
            short n = faces.get(i).normal.x;
            array[i * 3] = normals.get(n - 1).x;
            array[i * 3 + 1] = normals.get(n - 1).y;
            array[i * 3 + 2] = normals.get(n - 1).z;
        }

        return array;
    }

    public short[] getVertexIndicesArray() {
        short[] array = new short[faces.size() * 3];

        for (int i = 0; i < faces.size(); i++) {
            Vector3s vector = faces.get(i).vertex;

            array[3 * i] = (short) (vector.x - 1);
            array[3 * i + 1] = (short) (vector.y - 1);
            array[3 * i + 2] = (short) (vector.z - 1);
        }

        return array;
    }
}
