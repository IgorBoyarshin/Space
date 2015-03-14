package utils;

import java.io.*;

/**
 * Created by Igor on 14-Mar-15.
 */
public class OBJLoader {
    public static Model loadModel(File f) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        Model m = new Model();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("v ")) {
                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                float z = Float.valueOf(line.split(" ")[3]);
                m.vertices.add(new Vector3f(x, y, z));
            } else if (line.startsWith("vn ")) {
                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                float z = Float.valueOf(line.split(" ")[3]);
                m.normals.add(new Vector3f(x, y, z));
            } else if (line.startsWith("f ")) {
                Vector3s vertexIndex = new Vector3s(
                        Short.valueOf(line.split(" ")[1].split("/")[0]),
                        Short.valueOf(line.split(" ")[2].split("/")[0]),
                        Short.valueOf(line.split(" ")[3].split("/")[0]));
                Vector3s normalIndex = new Vector3s(
                        Short.valueOf(line.split(" ")[1].split("/")[2]),
                        Short.valueOf(line.split(" ")[2].split("/")[2]),
                        Short.valueOf(line.split(" ")[3].split("/")[2]));
                m.faces.add(new Face(vertexIndex, normalIndex));
//                System.out.println("Face: " + vertexIndex.x + " " + vertexIndex.y + " " + vertexIndex.z);
            }
        }

        return m;
    }
}
