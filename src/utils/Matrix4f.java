package utils;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

/**
 * Created by Igor on 01-Mar-15.
 */
public class Matrix4f {
    private final int SIZE = 4 * 4;
    public float[] matrix = new float[SIZE];

    public Matrix4f() {

    }

    public static Matrix4f perspective(final int WINDOW_WIDTH, final int WINDOW_HEIGHT,
                                       float fFovDeg, float fzNear, float fzFar) {
        Matrix4f result = new Matrix4f();

        float degToRad = 3.14159f * 2.0f / 360.0f;
        float fFovRad = fFovDeg*degToRad;
        float fFrustumScale = 1.0f / (float)Math.tan(fFovRad / 2.0f);

        result.matrix[0] = fFrustumScale / (WINDOW_WIDTH * 1.0f / (float) WINDOW_HEIGHT);
        result.matrix[5] = fFrustumScale;
        result.matrix[10] = (fzFar + fzNear) / (fzNear - fzFar);
        result.matrix[14] = (2 * fzFar * fzNear) / (fzNear - fzFar);
        result.matrix[11] = -1.0f;

        return result;
    }

    public static Matrix4f identity() {
        Matrix4f result = new Matrix4f();

//        for (int i = 0; i < 4 * 4; i++) {
//            result.matrix[i] = 0.0f;
//        }

        result.matrix[0 + 0 * 4] = 1.0f;
        result.matrix[1 + 1 * 4] = 1.0f;
        result.matrix[2 + 2 * 4] = 1.0f;
        result.matrix[3 + 3 * 4] = 1.0f;

        return result;
    }

    public Matrix4f multiply(Matrix4f matrixB) {
        Matrix4f result = new Matrix4f();

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                float sum = 0.0f;
                for (int e = 0; e < 4; e++) {
//                    sum += this.matrix[e + y * 4] + matrixB.matrix[x + e * 4];
                    sum += this.matrix[x + e * 4] * matrixB.matrix[e + y * 4];
                }
                result.matrix[x + y * 4] = sum;
            }
        }

        return result;
    }

    public static Matrix4f scale(Vector3f vector) {
        Matrix4f result = identity();

        result.matrix[0 + 0 * 4] = vector.x;
        result.matrix[1 + 1 * 4] = vector.y;
        result.matrix[2 + 2 * 4] = vector.z;

        return result;
    }

    public static Matrix4f translate(Vector3f vector) {
        Matrix4f result = identity();

        result.matrix[0 + 3 * 4] = vector.x;
        result.matrix[1 + 3 * 4] = vector.y;
        result.matrix[2 + 3 * 4] = vector.z;

        return result;
    }

    /** Angle in Degrees
     */
    public static Matrix4f rotate(float angle, float x, float y, float z) {
        Matrix4f result = identity();
        float r = (float) toRadians(angle);
        float cos = (float) cos(r);
        float sin = (float) sin(r);
        float omc = 1.0f - cos;

        result.matrix[0 + 0 * 4] = x * omc + cos;
        result.matrix[1 + 0 * 4] = y * x * omc + z * sin;
        result.matrix[2 + 0 * 4] = x * z * omc - y * sin;

        result.matrix[0 + 1 * 4] = x * y * omc - z * sin;
        result.matrix[1 + 1 * 4] = y * omc + cos;
        result.matrix[2 + 1 * 4] = y * z * omc + x * sin;

        result.matrix[0 + 2 * 4] = x * z * omc + y * sin;
        result.matrix[1 + 2 * 4] = y * z * omc - x * sin;
        result.matrix[2 + 2 * 4] = z * omc + cos;

        return result;
    }

    public FloatBuffer toFloatBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(matrix.length);
        buffer.put(matrix);
        buffer.flip();
        return buffer;
    }
}
