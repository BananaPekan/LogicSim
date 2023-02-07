package banana.pekan.logicsim.ui.gui.button;

import banana.pekan.logicsim.ui.Window;

import java.awt.*;

public class Button {

    int x;
    int y;

    int opacityMargin = 0;

    Window.ClickEvent clickEvent;

    int width;

    String text;

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

    int height;

    public String getText() {
        return this.text;
    }


    public Button(String text, int x, int y, int width, int height, Window.ClickEvent clickEvent) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.clickEvent = clickEvent;
    }

    public void render(Graphics g, int lastMouseX, int lastMouseY) {
        Color buttonColor = new Color(0x2C56A5);
        g.setColor(isMouseOver(lastMouseX, lastMouseY) ? buttonColor : buttonColor.darker());
        g.fillRect(getX(), getY(), getWidth(), getHeight());
        int textWidth = (int) g.getFontMetrics().getStringBounds(getText(), g).getWidth();
        int textHeight = (int) g.getFontMetrics().getStringBounds(getText(), g).getHeight();
        g.setColor(Color.WHITE.darker());
        g.drawString(getText(), getX() - textWidth /2 + getWidth() / 2, getY() + getHeight() / 2 + textHeight / 3);
    }

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

    public boolean isMouseOver(int mouseX, int mouseY) {
        int x1 = x;
        int y1 = y;
        int x2 = x + width;
        int y2 = y + height;
        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;

    }

    public void checkClick(int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            clickEvent.run();
        }
    }


}