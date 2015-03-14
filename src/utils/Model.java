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

    public List<Vector3s> getVertexIndices() {
        List<Vector3s> indices = new ArrayList<>();

        for (Face face : faces) {
            indices.add(face.vertex);
        }

        return indices;
    }

    public float[] getVerticesArray() {
        float[] array = new float[vertices.size() * 3];

        for (int i = 0; i < 3 * vertices.size(); i += 3) {
            Vector3f vector = vertices.get(i/3);

            array[i] = vector.x;
            array[i + 1] = vector.y;
            array[i + 2] = vector.z;
        }

        return array;
    }

    public short[] getVertexIndicesArray() {
        short[] array = new short[faces.size() * 3];

        for (int i = 0; i < 3 * faces.size(); i += 3) {
            Vector3s vector = faces.get(i/3).vertex;

            array[i] = vector.x;
            array[i + 1] = vector.y;
            array[i + 2] = vector.z;
        }

        return array;
    }
}
