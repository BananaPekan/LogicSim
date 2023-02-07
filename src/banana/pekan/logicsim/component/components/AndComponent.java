package banana.pekan.logicsim.component.components;

import banana.pekan.logicsim.component.Component;

import java.awt.*;

public class AndComponent extends Component {

    public AndComponent(int x, int y) {
        super(2, 1, x, y);
        this.isCodeBased = true;
        setColor(new Color(0x4CDC42));
        setName("AND");
    }

    @Override
    public void runCode() {
        boolean[] power = getPower();
        setPower(0, power[0] && power[1]);
    }
}
