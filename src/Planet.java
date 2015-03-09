import utils.*;

/**
 * Created by Igor on 07-Mar-15.
 */
public class Planet {
    private static VertexArray object;

    public static double positionDivider = 1.0f;

    private final String name;

    private Vector3f color;

    private Matrix4f theObject = Matrix4f.identity();
    private Matrix4f rotation = Matrix4f.identity();
    private Matrix4f translation = Matrix4f.identity();
    private Matrix4f scalation = Matrix4f.identity();

    private final int mode;
    public static final int MODE_MAIN = 0;
    public static final int MODE_COORDINATES = 1;

    public Planet(int mode, final String name, Vector3f color) {
//        super(mode, generatePlanetVertices(), generatePlanetIndices());
//        super(mode);

        this.name = name;
        this.mode = mode;
        this.color = color;

        object = new VertexArray(generatePlanetVertices(), generatePlanetIndices());
    }

    // Scales the actual position to be within the 1.0 box(a little bigger)
    public void update(Vector3d newPosition) {
        if (newPosition != null) { // TODO: why>??
            Vector3f newPositionFloat = new Vector3f();
            newPositionFloat.x = (float) (newPosition.x / positionDivider);
            newPositionFloat.y = (float) (newPosition.y / positionDivider);
            newPositionFloat.z = (float) (newPosition.z / positionDivider);
            translate(newPositionFloat);
        } else {

        }
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

    private static float[] generatePlanetVertices() {
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

    private static byte[] generatePlanetIndices() {
        byte[] indices = new byte[]{
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
