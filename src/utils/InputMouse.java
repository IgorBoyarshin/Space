package utils;

import org.lwjgl.glfw.GLFWCursorPosCallback;

/**
 * Created by Igor on 08-Mar-15.
 */
public class InputMouse extends GLFWCursorPosCallback {
    private static double oldX, oldY;
    private static boolean firstTime = true;

    public static float dx, dy;

    @Override
    public void invoke(long window, double xPos, double yPos) {
//        if (firstTime) {
//            firstTime = false;
//
//            oldX = xPos;
//            oldY = yPos;
//        } else {
//            dx = (float) (xPos - oldX);
//            dy = (float) (yPos - oldY);
//
//            oldX = xPos;
//            oldY = yPos;
//        }
        dx = (float) xPos;
        dy = (float) yPos;
    }
}
