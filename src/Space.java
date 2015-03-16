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
    private Settings settings = new Settings();

    private enum DistanceSystem {
        REALISTIC, REALISTIC_SMALLER_DISTANCE_BIGGER_RADIUS,
        OBJECTS, ALL_VISIBLE
    }

    private DistanceSystem distanceSystem;

    private Camera camera;
    private Thread renderingThread;
    private boolean renderingThreadRunning = false;

    // Technical stuff
    private GLFWErrorCallback errorCallback;
    private long window;
    private Input input = new Input();
    private InputMouse inputMouse = new InputMouse();
    private final long inputKeyDelay = 250;
    private long inputKeyLast = System.currentTimeMillis();

    // FPS
    private long lastTime = System.currentTimeMillis();
    private int fps = 0;

    // Space
    private ControlCenter controlCenter;

    // Objects for rendering
    private List<Planet> planets = new ArrayList<>();
    private List<ListMarks> marks = new ArrayList<>();
    private Object ground;

    public void start() {
        System.out.println(":> Starting the program");

        controlCenter = new ControlCenter();
//        controlCenter.createDump("Big_50y_High", 50*365*24*3600, 60, 24*3600, controlCenter.initPlanets());

        controlCenter.useDump("Main_10y");
//        controlCenter.calculateForObjects(controlCenter.initPlanets(), 1);
//        controlCenter.calculateForObjects(controlCenter.initPlanets(), 24*3600);
        settings.setCurrentSpeedType(1);

        cleanMarks();

        distanceSystem = DistanceSystem.ALL_VISIBLE;

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

        ground = new Object(Object.MODE_MAIN, new Vector3f(0.3f, 0.1f, 0.8f));
        ground.scale(new Vector3f(settings.maxViewingDistance / 2, settings.maxViewingDistance / 2, settings.maxViewingDistance / 2));

        switch (distanceSystem) {
            case OBJECTS: {
                Planet.distanceDivider = 1000.0f;
                Planet.positionDivider = 1.0f;
            }
            break;
            case ALL_VISIBLE: {
                Planet.distanceDivider = (float) controlCenter.getMaxRemoteness() / settings.maxViewingDistance * 2.0f;
                Planet.positionDivider = 0.9f;
            }
            break;
            case REALISTIC_SMALLER_DISTANCE_BIGGER_RADIUS: {
                float positionDivider = 20.0f;
                float biggestObjectSize = 20.0f;
                Planet.distanceDivider = (float) controlCenter.getBiggestObjectRadius() / biggestObjectSize;
                Planet.positionDivider = positionDivider;
            }
            break;
            case REALISTIC: {
                float positionDivider = 1.0f;
                float biggestObjectSize = 20.0f;
                Planet.distanceDivider = (float) controlCenter.getBiggestObjectRadius() / biggestObjectSize;
                Planet.positionDivider = positionDivider;
            }
            break;
        }

        // Init Planets for rendering
        Dataset dataset = controlCenter.getDatasetForThisStep();
        for (int i = 0; i < controlCenter.getPlanetsSize(); i++) {
            float radius = (float) controlCenter.getPlanetRadius(i);
            planets.add(new Planet(Object.MODE_COORDINATES, controlCenter.getPlanetName(i),
                    new Vector3f(1.0f - i * 1.0f / controlCenter.getPlanetsSize(), 0.25f, i * 1.0f / controlCenter.getPlanetsSize())));
            planets.get(i).update(dataset.getPlanet(i));

            switch (distanceSystem) {
                case OBJECTS: {
                    planets.get(i).scale(new Vector3f(radius, radius, radius));
                }
                break;
                case ALL_VISIBLE: {
                    float r = 2.0f * (float) Math.pow(10, 8) * (float) Math.sqrt(Math.sqrt(radius));
                    planets.get(i).scale(new Vector3f(r, r, r));
                }
                break;
                case REALISTIC_SMALLER_DISTANCE_BIGGER_RADIUS: {
                    planets.get(i).scale(new Vector3f(radius, radius, radius));
                }
                break;
                case REALISTIC: {
                    planets.get(i).scale(new Vector3f(radius, radius, radius));
                }
                break;
            }
        }
    }

    private void prepareScene() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        Shader.loadAll();

        Shader.main.enable();
        Shader.main.setUniformMat4f("camera", camera.getMatrix());
        Matrix4f perspective = Matrix4f.perspective(WIDTH, HEIGHT, 67.0f, 0.2f, settings.maxViewingDistance);
        Shader.main.setUniformMat4f("perspectiveMatrix", perspective);
        Shader.main.disable();
    }

    private void prepareRenderingSettings() {
        glViewport(0, 0, WIDTH, HEIGHT);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glEnable(GL_DEPTH_TEST);

//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);
//        glFrontFace(GL_CW);
    }

    private void cleanMarks() {
        marks = new ArrayList<>();
        for (int i = 0; i < controlCenter.getPlanetsSize(); i++) {
            marks.add(new ListMarks());
        }
    }

    private void moveFromDumpToLive() {
        if (controlCenter.getCurrentMode().equals(ControlCenter.Mode.DUMP)) {
            controlCenter.calculateForObjects(controlCenter.getPlanetsFromDumpForStep(controlCenter.getCurrentStep()), 3600);
        }
    }

    @Override
    public void run() {
        init();

        while (renderingThreadRunning) {

            update();
            render();

            if (settings.calculateAndPrintFPS) {
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

        if (Input.keys[GLFW_KEY_P] && !Input.keys[GLFW_KEY_LEFT_SHIFT]) {
            settings.playing = true;
        }

        if (Input.keys[GLFW_KEY_P] && Input.keys[GLFW_KEY_LEFT_SHIFT]) {
            settings.playing = false;
        }

//        if (Input.keys[GLFW_KEY_T] && !Input.keys[GLFW_KEY_LEFT_SHIFT]) {
//            showTerminal = true;
//        }
//        if (Input.keys[GLFW_KEY_T] && Input.keys[GLFW_KEY_LEFT_SHIFT]) {
//            showTerminal = false;
//        }

        if (Input.keys[GLFW_KEY_F] && !Input.keys[GLFW_KEY_LEFT_SHIFT]) {
            settings.calculateAndPrintFPS = true;
        }

        if (Input.keys[GLFW_KEY_F] && Input.keys[GLFW_KEY_LEFT_SHIFT]) {
            settings.calculateAndPrintFPS = false;
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

        if (Input.keys[GLFW_KEY_BACKSPACE]) {
            System.out.println(controlCenter.getFullTime(settings.getCurrentSpeed() * controlCenter.getStepDuration()));
        }

        // One-click buttons

        long time = System.currentTimeMillis();
        if (time - inputKeyLast > inputKeyDelay) {
            if (Input.keys[GLFW_KEY_EQUAL]) { // plus
                settings.intCurrentSpeed();

                inputKeyLast = time;
            }
            if (Input.keys[GLFW_KEY_MINUS]) { // plus
                settings.decCurrentSpeed();

                inputKeyLast = time;
            }

            if (Input.keys[GLFW_KEY_B]) {
                moveFromDumpToLive();
                settings.setCurrentSpeedType(0);

                inputKeyLast = time;
            }

            if (Input.keys[GLFW_KEY_Y] && !Input.keys[GLFW_KEY_LEFT_SHIFT]) {
                settings.displayMarks = true;

                inputKeyLast = time;
            } else if (Input.keys[GLFW_KEY_Y] && Input.keys[GLFW_KEY_LEFT_SHIFT]) {
                settings.displayMarks = false;
                cleanMarks();

                inputKeyLast = time;
            }

            if (Input.keys[GLFW_KEY_C]) {
                if (controlCenter.getCurrentMode().equals(ControlCenter.Mode.DUMP)) {
                    System.out.println("Current step: " + controlCenter.getCurrentStep() + " of " + controlCenter.getMaxDumpStep() +
                            "; Current second: " + controlCenter.getCurrentSecond());
                    inputKeyLast = time;
                } else {
                    System.out.println("Current step: " + controlCenter.getCurrentStep() +
                            "; Current second: " + controlCenter.getCurrentSecond());
                    inputKeyLast = time;
                }
            }

            if (Input.keys[GLFW_KEY_U]) {
                Vector3f position = camera.getPosition();
                System.out.println("Position (" + position.x + " ; " + position.y + " ; " + position.z + ")");

                inputKeyLast = time;
            }
        }
    }

    private void processTerminal() {
        settings.calculateAndPrintFPS = false;

        System.out.print(":>>");

        // Terminal
        // Wait for input
    }

    private void update() {
        glfwPollEvents();

        keyboard();

        if (settings.showTerminal) {
            processTerminal();
        }

        // If playing then update position and inc second for being displayed
        if (settings.playing) {
            for (int i = 0; i < settings.currentSpeedSteps; i++) {
                if (settings.displayMarks) {
                    if (controlCenter.getCurrentSecond() % settings.marksStepDurationInSeconds == 0) {
                        for (int j = 0; j < planets.size(); j++) {
                            marks.get(j).marks.add(new Mark(planets.get(j).getPos(), planets.get(j).getColor()));
                        }
                    }
                }

                controlCenter.processNextStep();
            }
//            controlCenter.processNextSteps(speedSteps);

            Dataset dataset = controlCenter.getDatasetForThisStep();
            for (int i = 0; i < planets.size(); i++) {
                planets.get(i).update(dataset.getPlanet(i));
            }
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

        if (settings.displayMarks) {
            for (ListMarks listMarks : marks) {
                for (Mark mark : listMarks.marks) {
                    mark.render();
                }
            }
        }

        glfwSwapBuffers(window);
    }

    public static void main(String[] args) {
        new Space().start();
    }
}
