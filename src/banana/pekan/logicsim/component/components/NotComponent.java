package banana.pekan.logicsim.component.components;

import banana.pekan.logicsim.component.Component;

import java.awt.*;
import java.util.Arrays;

public class NotComponent extends Component {

    public NotComponent(int x, int y) {
        super(1, 1, x, y);
        this.isCodeBased = true;
//      ORIGINAL:  setColor(new Color(0xDC4242));
        setColor(new Color(0xD85252));
        setName("NOT");
    }

    @Override
    public void runCode() {
        boolean[] power = getPower();
        setPower(0, !power[0]);
    }
}
