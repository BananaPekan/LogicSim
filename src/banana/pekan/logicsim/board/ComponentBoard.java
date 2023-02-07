package banana.pekan.logicsim.board;

import banana.pekan.logicsim.component.Component;
import banana.pekan.logicsim.component.Port;
import banana.pekan.logicsim.component.Wire;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class ComponentBoard implements Serializable {

    Color color;

    int inputNodes;
    int outputNodes;

    ArrayList<Port> inputPorts;
    ArrayList<Port> outputPorts;

    ArrayList<Component> components;

    public int getInputNodes() {
        return inputNodes;
    }

    public int getOutputNodes() {
        return outputNodes;
    }

    public ArrayList<Port> getInputPorts() {
        return inputPorts;
    }

    public ArrayList<Port> getOutputPorts() {
        return outputPorts;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public Color getColor() {
        return color;
    }

    public ComponentBoard(int inputNodes, int outputNodes, ArrayList<Port> inputPorts, ArrayList<Port> outputPorts, ArrayList<Component> components, Color color) {
        this.inputNodes = inputNodes;
        this.outputNodes = outputNodes;
        this.inputPorts = inputPorts;
        this.outputPorts = outputPorts;
        this.components = components;
        this.color = color;
    }

    public void update() {
        for (Port port : inputPorts) {
            handlePort(port);
        }
        for (Port port : outputPorts) {
            handlePort(port);
        }
        for (Component component : components) {
            component.run();
            ArrayList<Port> ports = component.getAllPorts();
            for (Port port : ports) {
                handlePort(port);
            }
        }
    }

    public void handlePort(Port port) {
        if (!port.isOutput() && port.getWires().size() == 0) {
            port.setPowered(false);
        }
        else {
            for (Wire wire : port.getWires()) {
                if (wire.isOutput != port.isOutput()) {
                    continue;
                }
                wire.setPowered(port.isPowered());
                Port connectedPort = wire.getPort2();
                if (connectedPort != null) connectedPort.setPowered(wire.isPowered());
            }
        }
    }

}
