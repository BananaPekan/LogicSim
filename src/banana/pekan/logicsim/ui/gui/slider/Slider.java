package banana.pekan.logicsim.ui.gui.slider;

import java.awt.*;
import java.util.Random;

public class Slider {

    int minValue;
    int value;
    int maxValue;

    int x;
    int y;
    int width;
    int height;

    public Slider(int x, int y, int width, int height, int min, int value, int max) {
        this.minValue = min;
        this.maxValue = max;
        this.value = value;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.marginY = height;
    }

    public Slider(int x, int y, int width, int height, int min, int max) {
        this.minValue = min;
        this.maxValue = max;
        this.value = new Random().nextInt(max + 1) + min;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.marginY = height;
    }

    public void randomize() {
        this.value = new Random().nextInt(maxValue + 1) + minValue;
    }

    public void setValue(int value, boolean adjust) {
        if (adjust && value < minValue) {
            this.value = minValue;
        }
        else if (adjust && value > maxValue) {
            this.value = maxValue;
        }
        else {
            this.value = value;
        }
    }


    public void setValue(int value) {
//        if (value >= minValue && value <= maxValue) {
//            this.value = value;
//        }
        setValue(value, false);
    }

    public int getValue() {
        return value;
    }

    int marginY = 0;

    boolean visible = true;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void render(Graphics g, int mouseX, int mouseY, Color sliderColor, Color valueColor) {
        if (!isVisible()) {
            return;
        }
        // Saving stuff
        Color prev = g.getColor();

        // Color
//        Color color = new Color(0xFF2F78A4);
        g.setColor(sliderColor);

        // Drawing the slider
//        g.fillRect(x - width / 2, y - height / 2, width, height);

        boolean mouseOver = isMouseOver(mouseX, mouseY, x - width / 2, y - height / 2, x + width, y + height);

        if (mouseOver) {
            if (marginY < height) {
                marginY += 2;
                if (marginY > height) {
                    marginY = height;
                }
            }
        }
        else if (marginY > 2) {
            marginY--;
        }

        float seg = (float) width / maxValue;

        if (marginY == 2) {
            g.fillRect(x - width / 2, y - height / 2, width, marginY);
            g.setColor(valueColor);
            g.fillRect(x - width / 2, y - height / 2, (int) (seg * value), marginY);
        }
        else {
            g.fillRoundRect(x - width / 2, y - height / 2, width, marginY, 10, 0);
            g.setColor(valueColor);
            g.fillRoundRect(x - width / 2, y - height / 2, (int) (seg * value), marginY, 10, 0);
        }

        int textWidth = getTextWidth(g, String.valueOf(value));
        int textHeight = getTextHeight(g, String.valueOf(value));

        Font font = g.getFont();
        int size = (int) (font.getSize() * 0.75f);
        g.setFont(new Font(font.getName(), Font.BOLD, size));

        g.setColor(Color.WHITE.darker());
        g.drawString(String.valueOf(value), x - textWidth / 2 + width / 2, y + height / 2 + textHeight / 3);

        g.setFont(font);

        // Restoring stuff
        g.setColor(prev);

    }

    public int getTextWidth(Graphics g, String text) {
        return (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
    }

    public int getTextHeight(Graphics g, String text) {
        return (int) g.getFontMetrics().getStringBounds(text, g).getHeight();
    }

    public void onClick(int mouseX, int mouseY) {
        if (!isVisible()) {
            return;
        }
        if (isMouseOver(mouseX, mouseY, x - width / 2, y - height / 2, x + width, y + height)) {
            int valueX = mouseX - (x - width / 2);
            float seg = (float) maxValue / width;
            setValue((int) (seg * valueX), true);
            startDrag();
        }
    }

    boolean isDragged = false;

    public void onMouseDrag(int mouseX, int mouseY) {
        if (!isVisible()) {
            return;
        }
        if (!isDragged) return;
        int valueX = mouseX - (x - width / 2);
        float seg = (float) maxValue / width;
        setValue((int) (seg * valueX), true);
    }

    public void startDrag() {
        isDragged = true;
    }

    public void stopDrag() {
        this.isDragged = false;
    }

    public boolean isMouseOver(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
