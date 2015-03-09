package calculations;

import utils.Vector3d;

import java.util.List;

/**
 * Created by Igor on 07-Mar-15.
 */
public class Dataset {
    private List<Vector3d> planetsPositions;

    public Dataset(List<Vector3d> planetsPositions) {
        this.planetsPositions = planetsPositions;
    }

    public Vector3d getPlanet(int id) {
        if (planetsPositions.size() > id-1) { // TODO: think of proper validation
            return planetsPositions.get(id);
        }
        return null;
    }
}
