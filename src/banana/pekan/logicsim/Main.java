package banana.pekan.logicsim;

import banana.pekan.logicsim.board.Board;
import banana.pekan.logicsim.component.ComponentRegistry;
import banana.pekan.logicsim.component.ComponentReader;
import banana.pekan.logicsim.component.ComponentWriter;
import banana.pekan.logicsim.ui.Window;

import java.io.File;

public class Main {

    public static Board board;

    public static void main(String[] args) {

        ComponentRegistry.init();

        ComponentReader componentReader = new ComponentReader(".");

        File dir = new File(".");
        File [] files = dir.listFiles((dir1, name) -> name.endsWith(".compbuilder"));

        if (files != null) {
            for (File componentFile : files) {
                componentReader.read(componentFile.getName());
            }
        }

        board = new Board();

        Board.loadedComponents = Board.loadComponents();

        Window window = new Window((int) (1024), (int) (720));
    }

}
