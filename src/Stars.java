import utils.*;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Igor on 17-Mar-15.
 */
public class Stars {
    private static VertexArray2 object;

    public Stars(int amount, float radius) {
        Model model = null;
        String modelName = "cube.obj";
        try {
            model = OBJLoader.loadModel(new File("models//" + modelName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (model == null) {
            System.err.print("Could not load model '" + modelName + "'");
//            object = new VertexArray2(generateBoxVertices(), generateBoxIndices());
        } else {
            generate(amount, radius, model);
//            object = new VertexArray2(model.getVerticesArray(), model.getVertexIndicesArray(), model.getNormalsArray());
        }
    }

    // max 4000
    private void generate(int amount, float radius, Model model) {
        if (amount < 4000) {
            float[] baseVertices = model.getVerticesArray();
            short[] baseIndices = model.getVertexIndicesArray();

            float[] vertices = new float[baseVertices.length * amount];
            short[] indices = new short[baseIndices.length * amount];
            Random r = new Random(System.currentTimeMillis());
//            float scale = 0.2f;


            // scale base
//            for (int i = 0; i < baseVertices.length; i++) {
//                baseVertices[i] *= scale;
//            }

            for (short i = 0; i < amount; i++) {
                Vector3f pos = genPosition(radius);
                float scale = r.nextFloat() * 0.7f + 0.1f;

                for (int j = 0; j < baseVertices.length; j += 3) {

                    vertices[i * baseVertices.length + j] = baseVertices[j] * scale + pos.x;
                    vertices[i * baseVertices.length + j + 1] = baseVertices[j + 1] * scale + pos.y;
                    vertices[i * baseVertices.length + j + 2] = baseVertices[j + 2] * scale + pos.z;
                }

                for (short j = 0; j < baseIndices.length; j++) {
                    indices[i * baseIndices.length + j] = (short) (baseIndices[j] + i * (baseVertices.length / 3));
//                    System.out.println("f" + indices[i * baseIndices.length + j]);
                }
            }

//            System.out.println("Length " + indices[indices.length-1]);
            object = new VertexArray2(vertices, indices, model.getNormalsArray());
//            object = new VertexArray2(model.getVerticesArray(), model.getVertexIndicesArray(), model.getNormalsArray());
        } else {
            System.out.println("Error");
            System.err.println("Could not properly generate stars");
        }
    }

    private Vector3f genPosition(float radius) {
        Random r = new Random();

        boolean sign = r.nextInt() % 2 == 0;
        float x = radius - 2 * r.nextFloat() * radius;
        float y = radius - 2 * r.nextFloat() * radius;
        float z = sign ? 1.0f * (float) Math.sqrt(Math.abs(radius * radius - x * x - y * y))
                : -1.0f * (float) Math.sqrt(Math.abs(radius * radius - x * x - y * y));

//        System.out.println(x + " " + y + " " + z);

        return new Vector3f(x, y, z);
    }

    public void render(Matrix4f camera) {
        Shader.stars.enable();

        Shader.stars.setUniformMat4f("cameraMatrixNoPos", camera);

        object.render();

        Shader.stars.disable();
    }
}
