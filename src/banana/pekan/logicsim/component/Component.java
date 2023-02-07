package banana.pekan.logicsim.component;

import banana.pekan.logicsim.board.Board;
import banana.pekan.logicsim.board.ComponentBoard;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Component implements Serializable, Cloneable {

    protected ComponentBoard operationsBoard;

    protected boolean isCodeBased = false;

    int x;
    int y;

    int inputPorts;
    int outputPorts;

    boolean isDragged;

    public boolean isDragged() {
        return isDragged;
    }

    public void setDragged(boolean dragged) {
        isDragged = dragged;
    }

    String name = "Component";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected Color componentColor = Color.GRAY;

    public Color  getColor() {
        return componentColor;
    }

    public ArrayList<Port> getInputPorts() {
        return inputPortsArray;
    }

    public ArrayList<Port> getOutputPorts() {
        return outputPortsArray;
    }

    public ArrayList<Port> getAllPorts() {
        ArrayList<Port> ports = new ArrayList<>();
        ports.addAll(inputPortsArray);
        ports.addAll(outputPortsArray);
        return ports;
    }

    public int getInputPortsNum() {
        return inputPorts;
    }

    public int getOutputPortsNum() {
        return outputPorts;
    }

    public void setColor(Color componentColor) {
        this.componentColor = componentColor;
    }

    ArrayList<Port> inputPortsArray = new ArrayList<>();
    ArrayList<Port> outputPortsArray = new ArrayList<>();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    int width;
    int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setOperationsBoard(ComponentBoard operationsBoard) {
        this.operationsBoard = operationsBoard;
    }

    public Component(int inputPorts, int outputPorts, int x, int y) {
        this.inputPorts = inputPorts;
        this.outputPorts = outputPorts;
        this.x = x;
        this.y = y;

        for (int i = 0; i < inputPorts; i++) {
            inputPortsArray.add(new Port(false, i));
            inputPortsArray.get(i).setComponent(this);
        }

        for (int i = 0; i < outputPorts; i++) {
            outputPortsArray.add(new Port(true, i));
            outputPortsArray.get(i).setComponent(this);
        }

    }

    public void connectWire(Wire wire, boolean isOutput, int port) {
        if (!isOutput) {
            inputPortsArray.get(port).connectWire(wire);
        }
        else {
            outputPortsArray.get(port).connectWire(wire);
        }
    }

    public void run() {
        if (isCodeBased) {
            runCode();
            return;
        }
        runOps();
    }

    public void runOps() {
        if (operationsBoard == null) return;
        for (int i = 0; i < inputPorts; i++) {
            operationsBoard.getInputPorts().get(i).setPowered(inputPortsArray.get(i).isPowered());
        }
        operationsBoard.update();
        for (int i = 0; i < outputPorts; i++) {
            outputPortsArray.get(i).setPowered(operationsBoard.getOutputPorts().get(i).isPowered());
        }
    }

    public void runCode() {

    }

    public boolean[] getPower() {
        boolean[] power = new boolean[inputPorts];
        for (int i = 0; i < inputPorts; i++) {
            power[i] = inputPortsArray.get(i).isPowered();
        }
        return power;
    }

    public void setPower(int port, boolean power) {
        outputPortsArray.get(port).setPowered(power);
    }

    @Override
    public Component clone() {
        try {
            return (Component) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
