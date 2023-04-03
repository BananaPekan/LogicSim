package banana.pekan.logicsim.component;

import banana.pekan.logicsim.board.Board;
import banana.pekan.logicsim.component.components.CustomComponent;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ComponentWriter {

    public void write(CustomComponent component, String path) {
        String componentCode = "";

        String name = component.getName();
        int inputs = component.getInputPortsNum();
        int outputs = component.getOutputPortsNum();
        Color componentColor = component.getColor();
        int[] color = new int[]{componentColor.getRed(), componentColor.getGreen(), componentColor.getBlue()};

        componentCode += "name: " + name + '\n';
        componentCode += "inputs: " + inputs + '\n';
        componentCode += "outputs: " + outputs + '\n';
        componentCode += "color: " + color[0] + ", " + color[1] + ", " + color[2] + "\n\n";

        HashMap<String, Integer> components = new HashMap<>();
        HashMap<Component, String> componentVariables = new HashMap<>();

        Board board = component.getAsBoard();

        for (Component comp : board.getComponents()) {
            String compName = comp.getName();
            int amount = 0;
            if (!components.containsKey(compName)) {
                components.put(compName, amount);
            }
            amount = components.get(compName);

            String objectName = compName.toLowerCase() + amount;

            componentCode += "insert " + objectName + " : " + compName + '\n';

            componentVariables.put(comp, objectName);

            components.put(compName, amount + 1);
        }

        componentCode += '\n';

        for (Component comp : componentVariables.keySet()) {
            String compName = componentVariables.get(comp);
            for (int i = 0; i < comp.getInputPortsNum(); i++) {
                Port inputPort = comp.getInputPorts().get(i);
                ArrayList<Wire> wires = inputPort.getWires();

                for (Wire wire : wires) {
                    Port from = wire.getPort1();
                    Component base = from.getComponent();
                    if (wire.getPort1().getComponent() == null) {
                        componentCode += "connect BOARD input " + board.getInputPorts().indexOf(from) + " : " + compName + " input " + i + '\n';
                    }
                    else {
                        String outputComp = componentVariables.get(base);
                        componentCode += "connect " + outputComp + " output " + base.getOutputPorts().indexOf(from) + " : " + compName + " input " + i + '\n';
                    }
                }

            }
            for (int i = 0; i < comp.getOutputPortsNum(); i++) {
                Port outputPort = comp.getOutputPorts().get(i);
                ArrayList<Wire> wires = outputPort.getWires();

                for (Wire wire : wires) {
                    Port to = wire.getPort2();
                    if (to.getComponent() == null) {
                        componentCode += "connect " + compName + " output " + comp.getOutputPorts().indexOf(wire.getPort1()) + " : BOARD output " + board.getOutputPorts().indexOf(to) + '\n';
                    }
                }

            }
        }

        try {
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(componentCode);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
