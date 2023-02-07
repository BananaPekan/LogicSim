package banana.pekan.logicsim.component.components;

import banana.pekan.logicsim.board.Board;
import banana.pekan.logicsim.board.ComponentBoard;
import banana.pekan.logicsim.component.Component;

import java.awt.*;

public class CustomComponent extends Component {

    String fileName;

    public CustomComponent(String fileName, int inputPorts, int outputPorts, ComponentBoard board, Color color) {
        super(inputPorts, outputPorts, 0, 0);
        this.operationsBoard = board;
        this.isCodeBased = false;
        this.fileName = fileName;
        this.setName(fileName.replace(".component", ""));
        this.setColor(color);
    }

    public CustomComponent get() {
        return new CustomComponent(fileName, getInputPortsNum(), getOutputPortsNum(), (ComponentBoard) Board.loadBoard(fileName, false), componentColor);
    }

}
