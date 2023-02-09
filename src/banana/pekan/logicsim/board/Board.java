package banana.pekan.logicsim.board;

import banana.pekan.logicsim.component.Component;
import banana.pekan.logicsim.component.ComponentRegistry;
import banana.pekan.logicsim.component.Port;
import banana.pekan.logicsim.component.Wire;
import banana.pekan.logicsim.component.components.CustomComponent;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.RandomAccess;

public class Board implements Serializable {


    Color componentBoardColor = Color.GRAY;

    ArrayList<Component> components = new ArrayList<>();

    public ArrayList<Component> addQueue = new ArrayList<>();
    public ArrayList<Component> removeQueue = new ArrayList<>();

    public static ArrayList<CustomComponent> loadedComponents = new ArrayList<>();

    int inputNodes;
    int outputNodes;

    ArrayList<Port> inputPorts = new ArrayList<>();
    ArrayList<Port> outputPorts = new ArrayList<>();

    public int getInputNodes() {
        return inputNodes;
    }

    public void setInputNodes(int inputNodes) {
        this.inputNodes = inputNodes;
    }

    public int getOutputNodes() {
        return outputNodes;
    }

    public void setOutputNodes(int outputNodes) {
        this.outputNodes = outputNodes;
    }

    public ArrayList<Port> getInputPorts() {
        return inputPorts;
    }

    public void setInputPorts(ArrayList<Port> inputPorts) {
        this.inputPorts = inputPorts;
    }

    public ArrayList<Port> getOutputPorts() {
        return outputPorts;
    }

    public void setOutputPorts(ArrayList<Port> outputPorts) {
        this.outputPorts = outputPorts;
    }

    public Board() {
        setup();
    }

    public void setup() {
        inputPorts.clear();
        outputPorts.clear();
        components.clear();
        inputNodes = 2;
        outputNodes = 1;
        for (int i = 0; i < inputNodes; i++) {
            inputPorts.add(new Port(true, i));
        }
        for (int i = 0; i < outputNodes; i++) {
            outputPorts.add(new Port(false, i));
        }
    }

    public void initializeClean() {
        inputPorts.clear();
        outputPorts.clear();
        components.clear();
        for (int i = 0; i < inputNodes; i++) {
            inputPorts.add(new Port(true, i));
        }
        for (int i = 0; i < outputNodes; i++) {
            outputPorts.add(new Port(false, i));
        }
    }

    public void addComponent(Component comp) {
        components.add(comp);
    }

    public void removeComponentQueue(Component comp) {
        removeQueue.add(comp);
    }

    public void removeComponent(Component comp) {
        components.remove(comp);
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<Component> components) {
        this.components = components;
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

    public void save(String fileName) {
        fileName = fileName.replace("\n", "");
        Random random = new Random();
        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();
        ComponentBoard board = new ComponentBoard(inputNodes, outputNodes, inputPorts, outputPorts, components, new Color(r, g, b));
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(board);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String fileName, float r, float g, float b, float alpha) {
        fileName = fileName.replace("\n", "");
        Random random = new Random();
        ComponentBoard board = new ComponentBoard(inputNodes, outputNodes, inputPorts, outputPorts, components, new Color(r, g, b, alpha));
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(board);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object loadBoard(String fileName, boolean convertToBoard) {
        ComponentBoard componentBoard;
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            componentBoard = (ComponentBoard) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        if (convertToBoard) {
            Board board = new Board();
            board.setInputNodes(componentBoard.getInputNodes());
            board.setOutputNodes(componentBoard.getOutputNodes());
            board.setInputPorts(componentBoard.getInputPorts());
            board.setOutputPorts(componentBoard.getOutputPorts());
            board.setComponents(componentBoard.getComponents());
            board.setComponentBoardColor(componentBoard.getColor());
            return board;
        }
        return componentBoard;
    }

    public static Board load(String fileName) {
        return (Board) loadBoard(fileName, true);
    }

    public void clear() {
        setup();
    }

    public static ArrayList<CustomComponent> loadComponents() {
        ArrayList<CustomComponent> loadedComponents = new ArrayList<>();

        File dir = new File(".");
        File [] files = dir.listFiles((dir1, name) -> name.endsWith(".component"));

        if (files != null) {
            for (File componentFile : files) {
//                System.out.println(componentFile.getAbsoluteFile());
                ComponentBoard loadedBoard = (ComponentBoard) loadBoard(componentFile.getAbsolutePath(), false);
                CustomComponent customComponent = null;
                if (loadedBoard != null) {
                    customComponent = new CustomComponent(componentFile.getName(), loadedBoard.getInputNodes(), loadedBoard.getOutputNodes(), loadedBoard, loadedBoard.getColor());

                    loadedComponents.add(customComponent);
                }
            }
        }
        return loadedComponents;
    }

    public void setComponentBoardColor(Color color) {
        componentBoardColor = color;
    }

    public void incrementInputs() {
        inputPorts.add(new Port(true, inputNodes));
        inputNodes++;
    }

    public void decrementInputs() {
        inputNodes--;
    }

    public void incrementOutputs() {
        outputPorts.add(new Port(false, outputNodes));
        outputNodes++;
    }

    public void decrementOutputs() {
        outputNodes--;
    }


}
