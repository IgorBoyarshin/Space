import utils.Shader;
import utils.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor on 15-Mar-15.
 */
public class ListMarks {
    //    public List<Mark> marks;
    public List<List<Mark>> listOfMarks;
    private int n;

    public ListMarks(int n) {
        this.n = n;
        listOfMarks = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            listOfMarks.add(new ArrayList<Mark>());
        }
    }

    public void add(int n, Vector3f pos, Vector3f color) {
        listOfMarks.get(n).add(new Mark(pos, color));
    }

    public void clear() {
        listOfMarks = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            listOfMarks.add(new ArrayList<Mark>());
        }
    }

    public void render() {
        if (listOfMarks.get(0).size() > 0) {
            Shader.main.enable();

            Shader.main.setUniform3f("color", new Vector3f(1.0f, 1.0f, 1.0f));

            Mark.object.bind();
            for (List<Mark> list : listOfMarks) {
                for (Mark mark : list) {
                    Shader.main.setUniformMat4f("objectMatrix", mark.theObject);
                    Mark.object.draw();
                }
            }

            Shader.main.disable();
        }
    }
}
