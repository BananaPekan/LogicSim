package banana.pekan.logicsim.ui;

import banana.pekan.logicsim.Main;
import banana.pekan.logicsim.board.Board;
import banana.pekan.logicsim.component.Component;
import banana.pekan.logicsim.component.components.AndComponent;
import banana.pekan.logicsim.component.components.CustomComponent;
import banana.pekan.logicsim.component.components.NotComponent;

import java.awt.*;
import java.util.ArrayList;

public class Toolbar {

    ArrayList<Button> buttons = new ArrayList<>();

    int height = 60;

    Window window;

    int lastX = 0;

    public Toolbar(Window window) {
        this.window = window;
        initializeButtons();
    }

    public void initializeButtons() {
        lastX = 0;
        buttons.clear();
        addButton(window, "NOT", () -> {
            NotComponent notComponent = new NotComponent(window.getWidth() / 2, window.getHeight() / 2);
            Main.board.addComponent(notComponent);
            window.holdingComponent = notComponent;
        });
        addButton(window, "AND", () -> {
            AndComponent andComponent = new AndComponent(window.getWidth() / 2, window.getHeight() / 2);
            Main.board.addComponent(andComponent);
            window.holdingComponent = andComponent;
        });
        for (int i = 0; i < Board.loadedComponents.size(); i++) {
            CustomComponent loadedComponent = Board.loadedComponents.get(i);
            addButton(window, loadedComponent.getName(), () -> {
                CustomComponent customComponent = loadedComponent.get();
                customComponent.setX(window.getWidth() / 2);
                customComponent.setY(window.getHeight() / 2);
                Main.board.addComponent(customComponent);
                window.holdingComponent = customComponent;
            });
        }
    }

    public void addButton(Window window, String text, ClickEvent clickEvent) {
        int screenHeight = window.getHeight();
        int width = getTextWidth(window.g, text) + 20;
        buttons.add(new Button(text, lastX, screenHeight - height, width, height, clickEvent));
        lastX += width;
    }

    public int getTextWidth(Graphics g, String text) {
        return (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
    }

    public int getTextHeight(Graphics g, String text) {
        return (int) g.getFontMetrics().getStringBounds(text, g).getHeight();
    }

    public void draw(int screenWidth, int screenHeight, Graphics g) {

        g.setColor(new Color(0x2C56A5).darker());

        g.fillRect(0, screenHeight - height, screenWidth, screenHeight);

        int lastMouseX = window.lastMouseX;
        int lastMouseY = window.lastMouseY;

        for (Button button : buttons) {

            boolean mouseOver = button.isMouseOver(lastMouseX, lastMouseY);

            int textWidth = getTextWidth(g, button.getText());
            int textHeight = getTextHeight(g, button.getText());
//            button.setHeight(height);
            Color color = new Color(0x646998E6, true);
//            Color hoverColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() - opaque);
            Color hoverColor = Color.LIGHT_GRAY;
            hoverColor = new Color(hoverColor.getRed(), hoverColor.getGreen(), hoverColor.getBlue(), (int)button.opaque);

            int opaque = (int) button.opaque;

            if (mouseOver) {
                if (opaque < 50) {
                    button.opaque += 1f;
                }
            }
            else {
                if (opaque > 0) {
                    button.opaque -= 1f;
                }
            }

//          ORIGINAL:  g.setColor(button.isMouseOver(lastMouseX, lastMouseY) ? color.brighter() : color);
            g.setColor(color);
            g.fillRect(button.x, button.y, button.width, button.height);

            g.setColor(hoverColor);
            g.fillRect(button.x, button.y, button.width, button.height);
//            System.out.println(button.y);

            g.setColor(Color.WHITE.darker());
            g.drawString(button.text, button.x - textWidth / 2 + button.width / 2, button.y + button.height / 2 + textHeight / 3);
        }
    }

    public class Button {

        int x;
        int y;

        float opaque;

        ClickEvent clickEvent;

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


        public Button(String text, int x, int y, int width, int height, ClickEvent clickEvent) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.clickEvent = clickEvent;
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

    public interface ClickEvent {

        public void run();

    }


}
