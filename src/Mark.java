import utils.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by Igor on 15-Mar-15.
 */
public class Mark {
    private static VertexArray2 object;

    private Vector3f color;

    private Matrix4f theObject = Matrix4f.identity();

    public Mark(Vector3f pos, Vector3f color) {
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
//            object = new VertexArray2(generateBoxVertices(), generateBoxIndices());
        } else {
            object = new VertexArray2(model.getVerticesArray(), model.getVertexIndicesArray(), model.getNormalsArray());
        }

        // For distance division
//        this.scale(new Vector3f(distanceDivider, distanceDivider, distanceDivider));
        theObject = (Matrix4f.translate(pos)).multiply(Matrix4f.scale(new Vector3f(0.1f, 0.1f, 0.1f)));
    }

    public void render() {
        Shader.main.enable();

        Shader.main.setUniform3f("color", color);
//        if (mode == MODE_MAIN) {
//            Shader.main.setUniformMat4f("objectMatrix", theObject);
//        } else if (mode == MODE_COORDINATES) {
        Shader.main.setUniformMat4f("objectMatrix", theObject);
//        }

        object.render();

        Shader.main.disable();
    }
}
