package banana.pekan.logicsim.component;

import java.io.Serializable;

public class Wire implements Serializable, Cloneable {

    public Port port1;
    public Port port2;

    public boolean powered = false;

    public boolean isOutput = false;

    public boolean isOutput() {
        return isOutput;
    }

    public void setOutput(boolean output) {
        isOutput = output;
    };

    public Port getPort1() {
        return port1;
    }

    public void setPort1(Port port1) {
        this.port1 = port1;
    }

    public Port getPort2() {
        return port2;
    }

    public void setPort2(Port port2) {
        this.port2 = port2;
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    public Wire(Port a, Port b) {
        this.port1 = a;
        this.port2 = b;
    }

    @Override
    public Wire clone() {
        try {
            return (Wire) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
