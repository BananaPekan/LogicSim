package banana.pekan.logicsim.component;

import banana.pekan.logicsim.Main;

import java.awt.*;

public class OutputPort {

    Point point;

    int port;


    public void setPort(int port) {
        this.port = port;
    }

    public OutputPort(Point point, int port) {
        this.point = point;
        this.port = port;
    }

    public boolean isToggled() {
        return Main.board.getOutputPorts().get(port).isPowered();
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
