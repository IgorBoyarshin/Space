import calculations.ControlCenter;
import calculations.Dataset;
import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import utils.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.Callbacks.glfwSetCallback;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by Igor on 01-Mar-15.
 */
public class Space implements Runnable {

    private final String TITLE = "Space";
    private final int WIDTH = 900;
    private final int HEIGHT = 600;

    // Settings
    private boolean calculateAndPrintFPS = false;
    private boolean showTerminal = false;
    private boolean playing = false;

    private Camera camera;
    private Thread renderingThread;
    private boolean renderingThreadRunning = false;

    // Technical stuff
    private GLFWErrorCallback errorCallback;
    private long window;
    private Input input = new Input();
    private InputMouse inputMouse = new InputMouse();

    // FPS
    private long lastTime = System.currentTimeMillis();
    private int fps = 0;

    // Space
    private ControlCenter controlCenter;
    private final float maxViewingDistance = (float)Math.pow(10,3) * 1.0f;
    // Objects for rendering
    private List<Planet> planets = new ArrayList<>();
    private Object object;
    private Object ground;

    public void start() {
        System.out.println(":> Starting the program");

        String dumpName = "test";
        controlCenter = new ControlCenter(ControlCenter.Mode.DUMP);
//        controlCenter.createDump("Main_10y", 10*365*24*3600, 1024, 24*3600, controlCenter.initPlanets());

        /* Tell it:
        - What Dump file to use
        - If not, then calculating:
            - What planets to have (ability to add and remove)
            - Precision and stuff
            - Maybe starting parameters(default) for planets
        */

        controlCenter.useDump("Main_10y");
        controlCenter.setCurrentStep(0);
        Planet.positionDivider = 1.1f * controlCenter.getMaxRemoteness() / (maxViewingDistance / 2.0f);

        renderingThreadRunning = true;
        renderingThread = new Thread(this, "Space Rendering");
        renderingThread.start();
    }

    private void init() {
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

        if (glfwInit() != GL11.GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetCallback(window, input);
        glfwSetCursorPosCallback(window, inputMouse);
//        glfwSetCallback(window, new Input());
//        glfwSetCursorPosCallback(window, new InputMouse());
//        glfwSetCursorPos(window, WIDTH / 2.0d, HEIGHT / 2.0d);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPos(window, 0, 0);

        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH) / 4 * 3,
                (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GLContext.createFromCurrent();

        // ------------------------------------------------------------------------------

        System.out.println();
        System.out.println("Starting LWJGL " + Sys.getVersion());
        System.out.println("Opengl: " + GL11.glGetString(GL_VERSION));
        System.out.println();

        prepareRenderingSettings();
        prepareGeneral();
        prepareScene();
    }

    private void prepareGeneral() {
        camera = new Camera(new Vector3f(0.0f, 10.0f, -25.0f), 0.0f, 0.0f);
        camera.setMovementSpeed2();

        object = new Object(Object.MODE_MAIN, new Vector3f(0.2f, 0.8f, 0.5f));

        ground = new Object(Object.MODE_MAIN, new Vector3f(0.3f, 0.1f, 0.8f));
        ground.scale(new Vector3f(maxViewingDistance / 2, maxViewingDistance / 2, maxViewingDistance / 2));

        // Init Planets for rendering
        Dataset dataset = controlCenter.getDatasetForStep(0);
        for (int i = 0; i < controlCenter.getPlanetsSize(); i++) {
//            double mass = controlCenter.getDumpPlanet(i).getMass();
            double radius = controlCenter.getDumpPlanet(i).getRadius();
            if (i == 1) {
                planets.add(new Planet(Object.MODE_COORDINATES, controlCenter.getDumpPlanet(i).getName(),
                        new Vector3f(1.0f, 1.0f, 1.0f)));
            } else {
                planets.add(new Planet(Object.MODE_COORDINATES, controlCenter.getDumpPlanet(i).getName(),
                        new Vector3f(i * 1.0f / controlCenter.getPlanetsSize(), 0.85f, 1.0f - i * 1.0f / controlCenter.getPlanetsSize())));
            }
            planets.get(i).update(dataset.getPlanet(i));
//            float scaler = (float) (radius / Planet.positionDivider);
//            float scaler = (float) (Math.sqrt(radius / Planet.positionDivider));
            float scaler = (float) (Math.sqrt(Math.sqrt(radius / Planet.positionDivider)));
//            System.out.println("Scaler: " + scaler);
//            planets.get(i).scale(new Vector3f(0.2f, 0.2f, 0.2f));
            planets.get(i).scale(new Vector3f(scaler * 5.0f, scaler * 5.0f, scaler * 5.0f));
        }
    }

    private void prepareScene() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        Shader.loadAll();

        Shader.main.enable();
        Shader.main.setUniformMat4f("camera", camera.getMatrix());
        Matrix4f perspective = Matrix4f.perspective(WIDTH, HEIGHT, 67.0f, 0.2f, maxViewingDistance);
        Shader.main.setUniformMat4f("perspectiveMatrix", perspective);
        Shader.main.disable();
    }

    private void prepareRenderingSettings() {
        glViewport(0, 0, WIDTH, HEIGHT);
//        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glEnable(GL_DEPTH_TEST);
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);
//        glFrontFace(GL_CW);
    }

    @Override
    public void run() {
        init();

        while (renderingThreadRunning) {

            update();
            render();

            if (calculateAndPrintFPS) {
                fps++;
                long curTime = System.currentTimeMillis();
                if (curTime - lastTime > 1000) {
                    if (curTime - lastTime > 2000) { // Has been off for a long time
                        lastTime = System.currentTimeMillis();
                    } else {
                        lastTime += 1000;
                    }
//                    lastTime = System.currentTimeMillis();
                    System.out.println("FPS: " + fps);
                    fps = 0;
                }
            }

            if (glfwWindowShouldClose(window) == GL_TRUE) {
                renderingThreadRunning = false;
            }
        }

        clean();
    }

    private void clean() {
        glfwDestroyWindow(window);
        glfwTerminate();
        errorCallback.release();
    }

    private void keyboard() {
        if (Input.keys[GLFW_KEY_ESCAPE]) {
            System.out.println(":> Terminating the program");
            renderingThreadRunning = false;
        }

        camera.addYaw(InputMouse.dx);
        camera.addPitch(InputMouse.dy);
        glfwSetCursorPos(window, 0.0d, 0.0d);
        InputMouse.dx = 0.0f;
        InputMouse.dy = 0.0f;

        if (Input.keys[GLFW_KEY_0]) {
            camera.setMovementSpeed0();
        }
        if (Input.keys[GLFW_KEY_1]) {
            camera.setMovementSpeed1();
        }
        if (Input.keys[GLFW_KEY_2]) {
            camera.setMovementSpeed2();
        }
        if (Input.keys[GLFW_KEY_3]) {
            camera.setMovementSpeed3();
        }
        if (Input.keys[GLFW_KEY_4]) {
            camera.setMovementSpeed4();
        }

        if (Input.keys[GLFW_KEY_U]) {
            Vector3f position = camera.getPosition();
            System.out.println("Position (" + position.x + " ; " + position.y + " ; " + position.z + ")");
        }

        if (Input.keys[GLFW_KEY_P] && !Input.keys[GLFW_KEY_LEFT_SHIFT]) {
            playing = true;
        }
        if (Input.keys[GLFW_KEY_P] && Input.keys[GLFW_KEY_LEFT_SHIFT]) {
            playing = false;
        }

//        if (Input.keys[GLFW_KEY_T] && !Input.keys[GLFW_KEY_LEFT_SHIFT]) {
//            showTerminal = true;
//        }
//        if (Input.keys[GLFW_KEY_T] && Input.keys[GLFW_KEY_LEFT_SHIFT]) {
//            showTerminal = false;
//        }

        if (Input.keys[GLFW_KEY_F] && !Input.keys[GLFW_KEY_LEFT_SHIFT]) {
            calculateAndPrintFPS = true;
        }
        if (Input.keys[GLFW_KEY_F] && Input.keys[GLFW_KEY_LEFT_SHIFT]) {
            calculateAndPrintFPS = false;
        }

        if (Input.keys[GLFW_KEY_Z]) {
            camera.moveUp();
        }
        if (Input.keys[GLFW_KEY_X]) {
            camera.moveDown();
        }
        if (Input.keys[GLFW_KEY_A]) {
            camera.moveLeft();
        }
        if (Input.keys[GLFW_KEY_D]) {
            camera.moveRight();
        }
        if (Input.keys[GLFW_KEY_W]) {
            camera.moveForward();
        }
        if (Input.keys[GLFW_KEY_S]) {
            camera.moveBackwards();
        }
    }

    private void processTerminal() {
        calculateAndPrintFPS = false;

        System.out.print(":>>");

        // Terminal
        // Wait for input
    }

    private void update() {
        glfwPollEvents();

        keyboard();
        object.update();

        if (showTerminal) {
            processTerminal();
        }

        if (playing) {
            Dataset dataset = controlCenter.getDatasetForStep(controlCenter.getCurrentStep());
            for (int i = 0; i < planets.size(); i++) {
                planets.get(i).update(dataset.getPlanet(i));
            }
            controlCenter.incCurrentStep();
//            System.out.println(controlCenter.getCurrentSecond());
        }

        Shader.main.enable();
        Shader.main.setUniformMat4f("camera", camera.getMatrix());
        Shader.main.disable();
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for (Planet planet : planets) {
            planet.render();
        }
//        object.render();
        ground.render();

        glfwSwapBuffers(window);
    }

    public static void main(String[] args) {
        new Space().start();
    }
}
