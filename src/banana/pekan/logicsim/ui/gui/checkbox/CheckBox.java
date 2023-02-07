package banana.pekan.logicsim.ui.gui.checkbox;

import java.awt.*;

public class CheckBox {

    boolean checked;
    int x;
    int y;

    int width;
    int height;

    public CheckBox(int x, int y, int square) {
        this.x = x;
        this.y = y;
        this.width = square;
        this.height = square;
    }

    public void render(Graphics g) {
        Color prev = g.getColor();
        g.setColor(new Color(0x00FF97));

        g.fillRect(x - width / 2, y - height / 2, width, 4);
        g.fillRect(x - width / 2, y - height / 2, 4, height);
        g.fillRect(x - width / 2, y + height / 2, width, 4);
        g.fillRect(x + width / 2, y - height / 2, 4, height + 4);

        if (isChecked()) {
            g.fillRect(x - width / 2 + 8, y - height / 2 + 8, width - 12, height - 12);
        }

        g.setColor(prev);
    }

    public void onClick(int mouseX, int mouseY, Runnable execute) {
        if (isMouseOver(mouseX, mouseY, x - width / 2, y - height / 2, x + width / 2, y + height / 2)) {
            toggle();
            execute.run();
        }
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggle() {
        this.checked = !this.checked;
    }

    public boolean isMouseOver(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
    }

}
