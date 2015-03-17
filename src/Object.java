import utils.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by Igor on 01-Mar-15.
 */
public class Object {
    private static VertexArray2 object;

    private Matrix4f theObject = Matrix4f.identity();
    private Matrix4f rotation = Matrix4f.identity();
    private Matrix4f translation = Matrix4f.identity();
    private Matrix4f scalation = Matrix4f.identity();

    private Vector3f color;

    private final int mode;
    public static final int MODE_MAIN = 0;
    public static final int MODE_COORDINATES = 1;

    public Object(final int mode, Vector3f color) {
        this.mode = mode;
        this.color = color;

        Model model = null;
        String modelName = "cube.obj";
        try {
            model = OBJLoader.loadModel(new File("models//" + modelName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (model == null) {
            System.err.print("Could not load model '" + modelName + "'");
        } else {
            object = new VertexArray2(model.getVerticesArray(), model.getVertexIndicesArray(), model.getNormalsArray());
        }

    }

//    public Object(final int mode, float[] vertices, byte[] indices) {
//        this.mode = mode;
//
////        float[] vertices = new float[]{
////                1.0f, 1.0f, 1.0f,
////                1.0f, 1.0f, -1.0f,
////                1.0f, -1.0f, -1.0f,
////                1.0f, -1.0f, 1.0f,
////
////                -1.0f, 1.0f, 1.0f,
////                -1.0f, 1.0f, -1.0f,
////                -1.0f, -1.0f, -1.0f,
////                -1.0f, -1.0f, 1.0f,
////
////                1.0f, 1.0f, 1.0f,
////                1.0f, 1.0f, -1.0f,
////                -1.0f, 1.0f, -1.0f,
////                -1.0f, 1.0f, 1.0f,
////        };
//
////        byte[] indices = new byte[]{
////                0, 1, 2,
////                0, 2, 3,
////
////                6, 5, 4,
////                6, 4, 7,
////
////                8, 11, 10,
////                8, 10, 9
////        };
//
//        object = new VertexArray(vertices, indices);
//    }

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
            scalation = scalation.multiply(Matrix4f.scale(vector));
            recalculate();
        } else if (mode == MODE_COORDINATES) {
            scalation = Matrix4f.scale(vector);
        }
    }

    public void translate(Vector3f vector) {
        if (mode == MODE_MAIN) {
            translation = translation.multiply(Matrix4f.translate(vector)); // Accumulate
            recalculate();
        } else if (mode == MODE_COORDINATES) {
            translation = Matrix4f.translate(vector); // Apply to default
        }
    }

    public void update() {
        rotate(1.0f, 0.0f, 1.0f, 1.0f);
    }

    public void render() {
//        translate(new Vector3f(0.01f, 0.0f, 0.0f));

        Shader.main.enable();

        Shader.main.setUniform1f("applyLighting", 0.0f);
        Shader.main.setUniform3f("color", color);

        if (mode == MODE_MAIN) {
            Shader.main.setUniformMat4f("objectMatrix", theObject);
        } else if (mode == MODE_COORDINATES) {
            Shader.main.setUniformMat4f("objectMatrix", translation.multiply(scalation));
        }

        object.render();

        Shader.main.disable();
    }
}
