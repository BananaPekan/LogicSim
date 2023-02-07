package banana.pekan.logicsim;

import banana.pekan.logicsim.board.Board;
import banana.pekan.logicsim.ui.Window;

public class Main {

    public static Board board;

    public static void main(String[] args) {
        board = new Board();
        Board.loadedComponents = Board.loadComponents();
        Window window = new Window((int) (1024), (int) (720));
    }

}
