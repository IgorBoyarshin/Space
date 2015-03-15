import utils.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by Igor on 07-Mar-15.
 */
public class Planet {
    private static VertexArray2 object;
//    private static VertexArray object;

    public static float distanceDivider = 1.0f;
    public static float positionDivider = 1.0f;

    private final String name;

    private Vector3f color;

    private Matrix4f theObject = Matrix4f.identity();
    private Matrix4f rotation = Matrix4f.identity();
    private Matrix4f translation = Matrix4f.identity();
    private Matrix4f scalation = Matrix4f.identity();

    private final int mode;
    public static final int MODE_MAIN = 0;
    public static final int MODE_COORDINATES = 1;

    public Vector3f pos;

    public Planet(int mode, final String name, Vector3f color) {
        this.name = name;
        this.mode = mode;
        this.color = color;

        Model model = null;
        String modelName = "planet.obj";
        try {
            model = OBJLoader.loadModel(new File("models//" + modelName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (model == null) {
            System.err.print("Could not load model '" + modelName + "'");
//            object = new VertexArray2(generateBoxVertices(), generateBoxIndices());
        } else {
            object = new VertexArray2(model.getVerticesArray(), model.getVertexIndicesArray(), model.getNormalsArray());
        }

        // For distance division
//        this.scale(new Vector3f(distanceDivider, distanceDivider, distanceDivider));
        this.scale(new Vector3f(1.0f, 1.0f, 1.0f));
    }

    public Vector3f getColor() {
        return color;
    }

    // Scales the actual position to be within the 1.0 box(a little bigger)
    public void update(Vector3d newPosition) {
        if (newPosition != null) { // TODO: why>??
            Vector3f newPositionFloat = new Vector3f();
            newPositionFloat.x = (float) (newPosition.x / distanceDivider / positionDivider);
            newPositionFloat.y = (float) (newPosition.y / distanceDivider / positionDivider);
            newPositionFloat.z = (float) (newPosition.z / distanceDivider / positionDivider);
            translate(newPositionFloat);

            pos = newPositionFloat; // Not needed anymore
        } else {

        }
    }

    public Vector3f getPos() {
        return pos;
    }

    public String getName() {
        return name;
    }

    private void recalculate() {
        theObject = translation.multiply(rotation.multiply(scalation));
    }

    public void rotate(float angle, float x, float y, float z) {
        if (mode == MODE_MAIN) {
            rotation = rotation.multiply(Matrix4f.rotate(angle, x, y, z));
            recalculate();
        }
    }

    public void scale(Vector3f vector) {
        if (mode == MODE_MAIN) {
            scalation = scalation.multiply(Matrix4f.scale(
                    new Vector3f(vector.x / distanceDivider, vector.y / distanceDivider, vector.z / distanceDivider)));
            recalculate();
        } else if (mode == MODE_COORDINATES) {
//            scalation = Matrix4f.scale(vector);
            scalation = Matrix4f.scale(
                    new Vector3f(vector.x / distanceDivider, vector.y / distanceDivider, vector.z / distanceDivider));
        }
    }

    public void translate(Vector3f vector) {
        if (mode == MODE_MAIN) {
            translation = translation.multiply(Matrix4f.translate(vector)); // Accumulate
            recalculate();
        } else if (mode == MODE_COORDINATES) {
            translation = Matrix4f.translate(vector); // Apply to default

//            Vector3f changed = new Vector3f(vector.x, vector.y, vector.z);
//            translation = Matrix4f.translate(changed); // Apply to default
        }
    }

    public void render() {
        Shader.main.enable();

        Shader.main.setUniform3f("color", color);
        if (mode == MODE_MAIN) {
            Shader.main.setUniformMat4f("objectMatrix", theObject);
        } else if (mode == MODE_COORDINATES) {
            Shader.main.setUniformMat4f("objectMatrix", translation.multiply(scalation));
        }

        object.render();

        Shader.main.disable();
    }

    private static float[] generateBoxVertices() {
        float[] vertices = new float[]{
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,

                -1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,

                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
        };

        return vertices;
    }

    private static short[] generateBoxIndices() {
        short[] indices = new short[]{
                0, 1, 2,
                0, 2, 3,

                6, 5, 4,
                6, 4, 7,

                8, 11, 10,
                8, 10, 9
        };

        return indices;
    }
}
