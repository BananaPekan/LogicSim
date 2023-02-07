package banana.pekan.logicsim.component;

import java.io.Serializable;
import java.util.ArrayList;

public class Port implements Serializable, Cloneable {

    boolean isOutput;
    boolean powered;
    int portNum;

    ArrayList<Wire> wires;

    Component component;

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Port(boolean isOutput, int portNum) {
        this.wires = new ArrayList<>();
        this.isOutput = isOutput;
        this.portNum = portNum;
    }

    public boolean isOutput() {
        return isOutput;
    }

    public void setOutput(boolean output) {
        isOutput = output;
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    public int getPortNum() {
        return portNum;
    }

    public void removeWire(Wire wire) {
        wires.remove(wire);
    }

    public void connectWire(Wire wire) {
        if (!isOutput) {
            if (wires.size() > 0) {
                for (Wire storedWire : wires) {
                    if (storedWire.getPort1() != null && storedWire.getPort2() != null) {
                        storedWire.setPort1(null);
                        storedWire.setPort2(null);
                    }
                }
            }
            wires.clear();
            wires.add(wire);
        }
        else {
            wires.add(wire);
        }
    }

    public ArrayList<Wire> getWires() {
        return wires;
    }

    public void setWires(ArrayList<Wire> wires) {
        this.wires = wires;
    }

    @Override
    public Port clone() {
        try {
            return (Port) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
