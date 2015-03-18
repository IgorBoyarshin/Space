/**
 * Created by Igor on 15-Mar-15.
 */
public class Settings {
    public final float maxViewingDistance = (float) Math.pow(10, 3) * 1.0f;
    public final int marksStepDurationInSeconds = 30 * 24 * 3600;

    public boolean calculateAndPrintFPS = false;
    public boolean showTerminal = false;
    public boolean playing = false;
    public boolean displayMarks = false;
    public boolean displayStars = true;

    public int currentSpeedSteps = 1;
    private final int[] speedLive = new int[]{
            3600,
            2 * 3600,
            4 * 3600,
            8 * 3600,
            16 * 3600,
            24 * 3600,
            32 * 3600,
            40 * 3600,
            48 * 3600,
            56 * 3600,
            64 * 3600,
            72 * 3600
    };
    private final int[] speedDump = new int[]{
            1,
            2,
            4,
            8,
            16,
            32
    };
    private int currentSpeed = 0;
    private int currentLiveSpeed = 2;
    private int currentDumpSpeed = 0;
    public boolean dumpForward = true;

    public int getCurrentSpeed() {
//        return currentLiveSpeed;
        return currentSpeedSteps;
    }

    public void setCurrentSpeedType(int id) {
        if (id == 0 || id == 1) {
            currentSpeed = id;
        }
    }

    public void intCurrentSpeed() {
        if (currentSpeed == 0) {
            if (currentLiveSpeed + 1 < speedLive.length) {
                currentLiveSpeed++;
            }
            currentSpeedSteps = speedLive[currentLiveSpeed];
        } else if (currentSpeed == 1) {
            if (currentDumpSpeed + 1 < speedDump.length) {
                currentDumpSpeed++;
            }
            currentSpeedSteps = speedDump[currentDumpSpeed];
        }
    }

    public void decCurrentSpeed() {
        if (currentSpeed == 0) {
            if (currentLiveSpeed - 1 >= 0) {
                currentLiveSpeed--;
                currentSpeedSteps = speedLive[currentLiveSpeed];
            }
        } else if (currentSpeed == 1) {
            if (currentDumpSpeed - 1 >= 0) {
                currentDumpSpeed--;
                currentSpeedSteps = speedDump[currentDumpSpeed];
            }
        }
    }

    public Settings() {

    }

}
