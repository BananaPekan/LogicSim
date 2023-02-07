package banana.pekan.logicsim.component;

import banana.pekan.logicsim.Main;

import java.awt.*;

public class Toggler {

    boolean toggled;
    Point point;

    int port;


    public void setPort(int port) {
        this.port = port;
    }

    public Toggler(Point point, int port, boolean initialState) {
        this.toggled = initialState;
        this.point = point;
        this.port = port;
        Main.board.getInputPorts().get(port).setPowered(initialState);
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        Main.board.getInputPorts().get(port).setPowered(toggled);
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getPort() {
        return port;
    }
}
