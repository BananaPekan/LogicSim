package banana.pekan.logicsim.component;

import banana.pekan.logicsim.board.Board;
import banana.pekan.logicsim.component.Component;
import banana.pekan.logicsim.component.Port;
import banana.pekan.logicsim.component.Wire;
import banana.pekan.logicsim.component.components.AndComponent;
import banana.pekan.logicsim.component.components.CustomComponent;
import banana.pekan.logicsim.component.components.NotComponent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ComponentReader {

    String path;

    public ComponentReader(String path) {
        this.path = path;
    }

    public void read(String fileName) {

        File file = new File(path, fileName);

        String desc = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                desc += line + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Board board = new Board();

        int inputs = 0;
        int outputs = 0;
        String componentName = "";

        String[] properties = desc.split("\n");
        for (String property : properties) {
            String[] identifiers = property.split(":");
            String identifier = identifiers[0].strip();
            if (identifier.equals("name")) {
                componentName = identifiers[1].strip();
            }
            else if (identifier.equals("inputs")) {
                inputs = Integer.parseInt(identifiers[1].strip());
            }
            else if (identifier.equals("outputs")) {
                outputs = Integer.parseInt(identifiers[1].strip());
            }
        }

        board.setInputNodes(inputs);
        board.setOutputNodes(outputs);

        board.initializeClean();

        ArrayList<String[]> componentsString = new ArrayList<>();
        HashMap<String[], int[]> connections = new HashMap<>();

        for (String property : properties) {
            String[] identifiers = property.split(":");
            String identifier = identifiers[0].strip();
            String[] instructions = identifier.split(" ");
            if (instructions[0].equals("insert")) {
                String name = instructions[1].strip();
                componentsString.add(new String[]{identifiers[1].strip(), name.strip()});
            }
            else if (instructions[0].equals("connect")) {
                String name = instructions[1];
                int type = instructions[2].equals("input") ? 0 : 1;
                int port = Integer.parseInt(instructions[3]);
                String[] component = identifiers[1].strip().split(" ");
                String compName = component[0];
                int connType = component[1].equals("input") ? 0 : 1;
                int compPort = Integer.parseInt(component[2]);

                connections.put(new String[]{name, compName}, new int[]{type, port, connType, compPort});

            }
        }

        HashMap<String, Component> components = new HashMap<>();

        Board.loadedComponents = Board.loadComponents();

        for (String[] comp : componentsString) {
            String type = comp[0];
            String name = comp[1];

            if (type.equals("NOT")) {
                components.put(name, new NotComponent(100, 100));
            }
            else if (type.equals("AND")) {
                components.put(name, new AndComponent(100, 100));
            }
            else {
                CustomComponent component = null;
                for (CustomComponent iterateComp : Board.loadedComponents) {
                    if (iterateComp.getName().equals(type)) {
                        component = iterateComp.get();
                    }
                }
                if (component != null) {
                    component.setX(100);
                    component.setY(100);
                    components.put(name, component);
                }
            }

        }

        for (String comp : components.keySet()) {
            board.addComponent(components.get(comp));
        }

        for (String[] compNames : connections.keySet()) {

            Component a = null;
            Component b = null;

            if (!compNames[0].equals("BOARD")) {
                a = components.get(compNames[0]);
            }

            if (!compNames[1].equals("BOARD")) {
                b = components.get(compNames[1]);
            }

            int[] connection = connections.get(compNames);

            Port portA;
            Port portB;

            if (a != null) {
                portA = connection[0] == 0 ? a.getInputPorts().get(connection[1]) : a.getOutputPorts().get(connection[1]);
            }
            else {
                portA = connection[0] == 0 ? board.getInputPorts().get(connection[1]) : board.getOutputPorts().get(connection[1]);
            }

            if (b != null) {
                portB = connection[2] == 0 ? b.getInputPorts().get(connection[3]) : b.getOutputPorts().get(connection[3]);
            }
            else {
                portB = connection[2] == 0 ? board.getInputPorts().get(connection[3]) : board.getOutputPorts().get(connection[3]);
            }

            Wire wire = new Wire(portA, portB);

            wire.setOutput(portA.isOutput());

            if (a != null) {
                a.connectWire(wire, connection[0] != 0, connection[1]);
            }
            else {
                portA.connectWire(wire);
            }

            if (b != null) {
                b.connectWire(wire, connection[2] != 0, connection[3]);
            }
            else {
                portB.connectWire(wire);
            }

        }

        board.save(componentName + ".component");

    }

}
