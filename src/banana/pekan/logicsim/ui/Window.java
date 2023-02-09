package banana.pekan.logicsim.ui;

import banana.pekan.logicsim.Main;
import banana.pekan.logicsim.board.Board;
import banana.pekan.logicsim.component.*;
import banana.pekan.logicsim.component.Component;
import banana.pekan.logicsim.ui.gui.checkbox.CheckBox;
import banana.pekan.logicsim.ui.gui.slider.Slider;
import banana.pekan.logicsim.ui.gui.button.Button;
import banana.pekan.logicsim.utils.ObjectValidator;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Window {

    JFrame frame;
    public JPanel panel;

    boolean isMouseDragged = false;

    int width;
    int height;

    JTextArea componentNameArea;

    Color TRANSPARENT = new Color(0 ,0, 0, 0);

    int portSize = 20;

    Graphics g;

    Wire holdingWire = null;
    Component holdingComponent = null;

    Board nextBoard;

    CopyOnWriteArrayList<Point> drawnPorts = new CopyOnWriteArrayList<>();
    ArrayList<Port> storedPorts = new ArrayList<>();

    ArrayList<Point> drawnPortsSwitch = new ArrayList<>();
    ArrayList<Port> storedPortsSwitch = new ArrayList<>();

    int lastMouseX;
    int lastMouseY;

    boolean isTextFocused = false;

    public boolean isMouseOver(int x1, int y1, int x2, int y2, int mouseX, int mouseY) {
        return mouseX <= x2 && mouseX >= x1 && mouseY <= y2 && mouseY >= y1;
    }

    public static int getRainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.8f, 0.7f).getRGB();
    }

    Toolbar toolbar;

    ArrayList<Toggler> togglers;
    ArrayList<OutputPort> outputPorts;

    boolean resetBoard = false;

    int togglerSize = 32;

    Slider red = new Slider(512, 100, 100, 15, 0, 255);
    Slider green = new Slider(512, 130, 100, 15, 0, 255);
    Slider blue = new Slider(512, 160, 100, 15, 0, 255);

    CheckBox colorCheckBox = new CheckBox(200, 200, 24);

    public int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public Window(int width, int height) {

        red.setVisible(false);
        green.setVisible(false);
        blue.setVisible(false);

        componentNameArea = new JTextArea(new DefaultStyledDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if ((getLength() + str.length()) <= 32) {
                    super.insertString(offs, str, a);
                }
                else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        togglers = new ArrayList<>();
        outputPorts = new ArrayList<>();
        this.width = width;
        this.height = height;

        frame = new JFrame("LogicSim");
        frame.setBounds(0, 0, width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ObjectValidator shouldEdit = new ObjectValidator(false, false);

        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {

                if (shouldEdit.arePositive()) {
                    toolbar.startEdit();
                }

                if (!shouldEdit.arePositive() && shouldEdit.hasCycled()) {
                    toolbar.stopEditing();
                }

                boolean isResizedX = !resizedWidth.doMatch();
                boolean isResizedY = !resizedHeight.doMatch();

                if (isResizedX) {
                    resizedWidth.setB(resizedWidth.getA());
                }

                if (isResizedY) {
                    resizedHeight.setB(resizedHeight.getA());
                }

                Board board = Main.board;

                if (resetBoard) {
                    Board.loadedComponents = Board.loadComponents();
                    board.clear();
                    drawnPorts.clear();
                    storedPorts.clear();
                    outputPorts.clear();
                    togglers.clear();
                }

                ArrayList<Component> queue = new ArrayList<>(board.addQueue);

                for (Component component : queue) {
                    board.addComponent(component);
                    board.addQueue.remove(component);
                }

                queue = new ArrayList<>(board.removeQueue);

                for (Component component : queue) {
                    board.removeComponent(component);
                    board.removeQueue.remove(component);
                }

                drawnPortsSwitch.clear();
                storedPortsSwitch.clear();
                Window.this.g = g;
                setBackground(Color.DARK_GRAY);
                super.paint(g);

                Font font = g.getFont();
                int size = (int) (font.getSize() * 2.2f);
                g.setFont(new Font(font.getName(), Font.BOLD, size));

                if (toolbar == null || resetBoard) {
                    toolbar = new Toolbar(Window.this);
                }

                if (resetBoard) {
                    resetBoard = false;
                }

                if (buttons.isEmpty()) {
                    addButton("+", 0, () -> {
                        if (togglers.size() >= 12) return;
                        board.incrementInputs();
                        Toggler[] togglersArray = togglers.toArray(new Toggler[0]);
                        togglers.clear();
                        for (int i = 0; i < board.getInputNodes(); i++) {
                            addToggler(new Toggler(new Point(), i, i != board.getInputNodes() - 1 && togglersArray[i].isToggled()));
                        }
                    });
                    addButton("-", 0, () -> {
                        if (togglers.size() <= 1) return;
                        board.decrementInputs();
                        togglers.remove(togglers.size() - 1);
//                        outputPorts.remove(outputPorts.size() - 1);
                        storedPorts.clear();
                    });
                    addButton("Create", 0, () -> {
                        String name = componentNameArea.getText();
                        if (!Objects.equals(name, "")) {
                            String[] parts = name.split(":");
                            System.out.println(parts.length);
                            if (parts.length < 4) {
                                int r = red.getValue();
                                int gr = green.getValue();
                                int b = blue.getValue();

                                File componentFile = new File(componentNameArea.getText() + ".component");

                                if (componentFile.exists()) {
                                    componentFile.delete();
                                }

                                board.save(componentNameArea.getText() + ".component", r / 255f, gr / 255f, b / 255f, 1);
                            }
                            else {
                                Color componentColor = new Color(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), parts.length == 5 ? Integer.parseInt(parts[4]) : 255);
                                board.save(parts[0] + ".component", componentColor.getRed() / 255f, componentColor.getGreen() / 255f, componentColor.getBlue() / 255f, componentColor.getAlpha() / 255f);
                            }

                            red.randomize();
                            green.randomize();
                            blue.randomize();

                            componentNameArea.setText("");
                            resetBoard = true;
                        }
                    });
                    addButton("+", 0, () -> {
                        if (board.getOutputNodes() >= 12) return;
                        board.incrementOutputs();
                        Toggler[] togglersArray = togglers.toArray(new Toggler[0]);
                        togglers.clear();
                        for (int i = 0; i < board.getInputNodes(); i++) {
                            addToggler(new Toggler(new Point(), i, i != board.getInputNodes() - 1 && togglersArray[i].isToggled()));
                        }
                        storedPorts.clear();
                        drawnPorts.clear();
                        outputPorts.clear();
                    });
                    addButton("-", 0, () -> {
                        if (board.getOutputNodes() <= 1) return;
                        board.decrementOutputs();
                        storedPorts.clear();
                        drawnPorts.clear();
                        outputPorts.clear();
                    });
                    addButton("Clear", 0, () -> resetBoard = true);
                }

                for (Component component : board.getComponents()) {
                    String name = component.getName();
                    Rectangle2D rect = g.getFontMetrics(g.getFont()).getStringBounds(name, g);
                    int width = (int) (50 + rect.getWidth());
//                    int height = (int) (5 * Math.max(component.getOutputPortsNum(), component.getInputPortsNum()) + 10 + rect.getHeight());
                    int height = Math.max(component.getInputPortsNum(), component.getOutputPortsNum()) * portSize;

                    if (component.getInputPortsNum() == 1) {
                        height += 6;
                    }

                    component.setWidth(width);
                    component.setHeight(height);

                    int x = component.getX();
                    int y = component.getY();

                    // Drawing the ports
                    //Drawing the input ports
                    ArrayList<Port> localPorts = component.getInputPorts();
                    int ports = component.getInputPortsNum();
                    for (int i = 0; i < ports; i++) {
                        int pY = ((height) / (ports)) * (i + 1) - ((height) / (ports)) / 2;
                        drawnPortsSwitch.add(new Point(x, pY + y));
                        storedPortsSwitch.add(localPorts.get(i));
                    }

                    // Drawing the output ports
                    localPorts = component.getOutputPorts();
                    ports = component.getOutputPortsNum();

                    for (int i = 0; i < ports; i++) {
//                        int pY = y + height / (1 + ports);
                        int pY = y + ((height) / (ports)) * (i + 1) - ((height) / (ports)) / 2;
                        drawnPortsSwitch.add(new Point(x + width, pY));
                        storedPortsSwitch.add(localPorts.get(i));
                    }

                    // Drawing the component
                    g.setColor(component.getColor());

//                  ORIGINAL:  g.fillRect(x, y - 2, width, height + 4);
                    g.fillRoundRect(x, y - 2, width, height + 4, 6, 6);

                    // Drawing the text
                    g.setColor(Color.WHITE);

                    g.drawString(name, (int) (x + width / 2 - rect.getWidth() / 2), (int) (y + height / 2 + rect.getHeight() / 4));
                }

                colorCheckBox.render(g);

                // Drawing the input and output board ports
                int inputNodes = board.getInputNodes();
                ArrayList<Port> inputPorts = board.getInputPorts();
                for (int i = 0; i < inputNodes; i++) {
                    int pY = height / 2 + ((i + (inputNodes / -2)) * 32) - ((inputNodes % 2) * (portSize / 2));
                    Point point = new Point(48, pY);
                    drawnPortsSwitch.add(point);
                    storedPortsSwitch.add(inputPorts.get(i));
                    if (i >= togglers.size()) {
                        addToggler(new Toggler(new Point(point.x - togglerSize, point.y), i, false));
                    }
                    else {
                        togglers.get(i).setPoint(new Point(point.x - togglerSize, point.y));
                    }
                }

                int outputNodes = board.getOutputNodes();
                ArrayList<Port> outputPortsLocal = board.getOutputPorts();
                for (int i = 0; i < outputNodes; i++) {
                    int pY = height / 2 + ((i + (outputNodes / -2)) * 32) - ((outputNodes % 2) * (portSize / 2));
                    Point point = new Point(Window.this.getWidth() - 48, pY);
                    drawnPortsSwitch.add(new Point(point.x - togglerSize, point.y));
                    storedPortsSwitch.add(outputPortsLocal.get(i));

                    if (isResizedX) {
                        outputPorts.clear();
                    }

                    if (outputPorts.size() != outputNodes) {
                        // CHECKMARK
                        addOutputPort(new OutputPort(point, i));
                    }
                }

                if (!new HashSet<>(drawnPorts).containsAll(drawnPortsSwitch)) {
                    drawnPorts.clear();
                    drawnPorts.addAll(drawnPortsSwitch);
                }
                if (!new HashSet<>(storedPorts).containsAll(storedPortsSwitch)) {
                    storedPorts.clear();
                    storedPorts.addAll(storedPortsSwitch);
                }

                // Draw the holding wire
                if (holdingWire != null) {
                    g.setColor(holdingWire.isPowered() ? new Color(0xF52037) : new Color(0x1D2128));
                    Graphics2D g2d = (Graphics2D) g;
                    Stroke previousStroke = g2d.getStroke();
                    g2d.setStroke(new BasicStroke(6));
                    Port connectedPort = holdingWire.getPort1();
                    Point point = drawnPorts.get(storedPorts.indexOf(connectedPort));
                    g.drawLine((int) point.getX(), (int) point.getY(), lastMouseX, lastMouseY);
                    g2d.setStroke(previousStroke);
                    drawPort(point.x, point.y);
                }
                else if (holdingComponent != null) {
                    holdingComponent.setX(lastMouseX - holdingComponent.getWidth() / 2);
                    int y = lastMouseY - holdingComponent.getHeight() / 2;
                    holdingComponent.setY(clamp(y, 0, Window.this.height - toolbar.height - holdingComponent.getHeight()));
                }

                for (int i = 0; i < drawnPorts.size(); i++) {
                    Port port = storedPorts.get(i);
                    Point point = drawnPorts.get(i);
                    for (Wire wire : port.getWires()) {
                        if (wire.isOutput() != port.isOutput()) {
                            continue;
                        }
                        int index = storedPorts.indexOf(wire.port2);
                        if (index == -1) continue;
                        drawWire(point.x, point.y, drawnPorts.get(index).x, drawnPorts.get(index).y, wire.powered);
                    }
                }

                // Drawing the sliders
//                red.render(g, lastMouseX, lastMouseY, new Color(0xD82A2A), new Color(0x995A5A));
//                green.render(g, lastMouseX, lastMouseY, new Color(0x36D82A), new Color(0x59A756));
//                blue.render(g, lastMouseX, lastMouseY, new Color(0x2A95D8), new Color(0x4D82A5));

                red.render(g, lastMouseX, lastMouseY, new Color(0x995A5A), new Color(0xD82A2A));
                green.render(g, lastMouseX, lastMouseY, new Color(0x59A756), new Color(0x36D82A));
                blue.render(g, lastMouseX, lastMouseY, new Color(0x4D82A5), new Color(0x2A95D8));

                // Drawing the color depicted by the sliders
                Color backColor = new Color(red.getValue(), green.getValue(), blue.getValue());
                g.setColor(backColor);
                g.fillRect(0, 0, 100, 100);

                drawText(g, "Preview", 50, 90, 0.75f, backColor);

                for (Component component : board.getComponents()) {
                    for (Port port : component.getAllPorts()) {
                        for (Wire wire : port.getWires()) {
                            Line2D line = new Line2D.Float();
                            Port port1 = wire.getPort1();
                            Port port2 = wire.getPort2();
                            int index = storedPorts.indexOf(port1);
                            if (index == -1) {
                                continue;
                            }
                            Point a = drawnPorts.get(index);
                            int port2Index = storedPorts.indexOf(port2);
                            if (port2Index != -1) {
                                Point b = drawnPorts.get(port2Index);
                                line.setLine(a.x, a.y, b.x, b.y);
                                if (line.intersects(lastMouseX - 2, lastMouseY - 2, 6, 6)) {
//                                    g.fillOval(lastMouseX - 8, lastMouseY - 8, 16, 16);
                                    g.setColor(wire.isPowered() ? new Color(0xEA9191) : new Color(0x353535));
                                    Graphics2D g2d = (Graphics2D) g;
                                    Stroke previousStroke = g2d.getStroke();
                                    g2d.setStroke(new BasicStroke(6));
                                    g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
                                    g2d.setStroke(previousStroke);
                                    break;
                                }
                            }
                        }
                    }
                }

                for (Point point : drawnPorts) {
                    drawPort(point.x, point.y);
                }

                for (Toggler toggler : togglers) {
                    Point connectedPort = drawnPorts.get(storedPorts.indexOf(inputPorts.get(toggler.getPort())));
                    drawToggler(toggler.getPoint(), toggler.isToggled(), connectedPort);
                    drawPort(connectedPort.x, connectedPort.y);
                }

                for (OutputPort outputPort : outputPorts) {
                    Point connectedPort = drawnPorts.get(storedPorts.indexOf(outputPortsLocal.get(outputPort.getPort())));
                    drawOutputPort(outputPort.getPoint(), outputPort.isToggled(), connectedPort);
                    drawPort(connectedPort.x, connectedPort.y);
                }

                toolbar.draw(Window.this.getWidth(), Window.this.getHeight(), g);

                if (isResizedX || isResizedY) {
                    toolbar.initializeButtons();
                }

                for (Button button : buttons) {
                    button.render(g, lastMouseX, lastMouseY);
                }

//                int textWidth = componentNameArea.getText().length() * componentNameArea.getFont().getSize();
                Font textFont = componentNameArea.getFont();
                size = (int) (font.getSize() * 2.2f);
                componentNameArea.setAlignmentX(LEFT_ALIGNMENT);
                componentNameArea.setFont(new Font(textFont.getName(), Font.BOLD, size));
                componentNameArea.setLocation(lastX, 0);
                int textHeight = componentNameArea.getFontMetrics(textFont).getHeight() + 8;
                componentNameArea.setBounds(lastX+1, 0, Window.this.width - lastX, textHeight);
                g.drawLine(lastX, textHeight, width - lastX, textHeight);
                if (!isTextFocused) {
                    requestFocusInWindow();
                }
                else if (!componentNameArea.hasFocus()) {
                    componentNameArea.requestFocusInWindow();
                }

                Rectangle rect = componentNameArea.getBounds();
                rect.setBounds(rect.x, rect.y, 150, rect.height);
                componentNameArea.setBounds(rect);

                colorCheckBox.setX((int) (lastX + componentNameArea.getWidth() + colorCheckBox.getWidth() * 1.4f));
                colorCheckBox.setY(toolbar.height - colorCheckBox.getWidth());

                Main.board.update();

                if (nextBoard != null) {
                    Board.loadedComponents = Board.loadComponents();
                    board.clear();
                    drawnPorts.clear();
                    storedPorts.clear();
                    outputPorts.clear();
                    togglers.clear();
                    board.setOutputNodes(nextBoard.getOutputNodes());
                    board.setInputNodes(nextBoard.getInputNodes());
                    board.initializeClean();
                    board.setComponents(nextBoard.getComponents());
                    board.setInputPorts(nextBoard.getInputPorts());
                    board.setOutputPorts(nextBoard.getOutputPorts());
                    nextBoard = null;
                }

            }
        };

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Toolbar.Button button : toolbar.buttons) {
                    button.checkClick(e.getX(), e.getY());
                }
                for (Button button : buttons) {
                    button.checkClick(e.getX(), e.getY());
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Board board = Main.board;

                red.onClick(e.getX(), e.getY());
                green.onClick(e.getX(), e.getY());
                blue.onClick(e.getX(), e.getY());

                colorCheckBox.onClick(e.getX(), e.getY(), () -> {
                    red.setVisible(colorCheckBox.isChecked());
                    green.setVisible(colorCheckBox.isChecked());
                    blue.setVisible(colorCheckBox.isChecked());
                });

                isTextFocused = false;

                for (Toggler toggler : togglers) {
                    Point point = toggler.getPoint();
                    if (isMouseOver(point.x - togglerSize / 2, point.y - togglerSize / 2, point.x + togglerSize / 2, point.y + togglerSize / 2, x, y)) {
                        toggler.setToggled(!toggler.isToggled());
                        return;
                    }
                }

                boolean setComp = false;
                for (int i = 0; i < drawnPorts.size(); i++) {
                    Point point = drawnPorts.get(i);
                    if (isMouseOver(point.x - portSize / 2, point.y - portSize / 2, point.x + portSize / 2, point.y + portSize / 2, x, y)) {
                        Port port = storedPorts.get(i);

                        if (holdingWire == null || holdingWire.isOutput() == port.isOutput()) {
                            holdingWire = new Wire(port, null);
                            holdingWire.setOutput(port.isOutput());
                            holdingWire.setPowered(port.isPowered());
                        }
                        else if (holdingWire != null) {
                            if (!holdingWire.getPort1().isOutput() && port.isOutput()) {
                                holdingWire.setPort2(holdingWire.getPort1());
                                holdingWire.setPort1(port);
                                holdingWire.setOutput(port.isOutput());
                                holdingWire.getPort2().connectWire(holdingWire);
                            }
                            else {
                                holdingWire.setPort2(port);
                                holdingWire.getPort1().connectWire(holdingWire);
                            }
                            port.connectWire(holdingWire);

                            holdingWire = null;
                        }
                        holdingComponent = null;
                        return;
                    }
                }
                holdingWire = null;
                if (holdingComponent != null) {
                    holdingComponent = null;
                }
                else {
                    for (Component component : board.getComponents()) {
                        int componentX = component.getX();
                        int componentY = component.getY();
                        int width = component.getWidth();
                        int height = component.getHeight();
                        if (isMouseOver(componentX, componentY, componentX + width, componentY + height, x, y)) {
                            if (e.getButton() == 3) {
                                for (Port port : component.getAllPorts()) {
                                    for (Wire wire : port.getWires()) {
                                        if (wire.isOutput == port.isOutput()) {
                                            if (wire.getPort2() != null) {
                                                wire.getPort2().removeWire(wire);
                                            }
                                            port.removeWire(wire);
                                            break;
                                        }
                                    }
                                }
                                board.removeComponentQueue(component);
                                drawnPorts.clear();
                                storedPorts.clear();
                                break;
                            }
                            holdingComponent = component;
                            holdingComponent.setDragged(isMouseDragged);
                            setComp = true;
                            holdingWire = null;
                            break;
                        }
                    }
                    if (!setComp) {
                        holdingComponent = null;
                    }
                }

                if (e.getButton() == 3) {
                    for (Component component : board.getComponents()) {
                        for (Port port : component.getAllPorts()) {
                            for (Wire wire : port.getWires()) {
                                Line2D line = new Line2D.Float();
                                Port port1 = wire.getPort1();
                                Port port2 = wire.getPort2();
                                int index = storedPorts.indexOf(port1);
                                if (index == -1) {
                                    continue;
                                }
                                Point a = drawnPorts.get(index);
                                int port2Index = storedPorts.indexOf(port2);
                                if (port2Index != -1) {
                                    Point b = drawnPorts.get(port2Index);
                                    line.setLine(a.x, a.y, b.x, b.y);
                                    if (line.intersects(lastMouseX - 2, lastMouseY - 2, 6, 6)) {
                                        port1.removeWire(wire);
                                        port2.removeWire(wire);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (holdingComponent != null && holdingComponent.isDragged()) {
                    holdingComponent.setDragged(false);
                    holdingComponent = null;
                }
                isMouseDragged = false;
                red.stopDrag();
                green.stopDrag();
                blue.stopDrag();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        componentNameArea.getCaret().setBlinkRate(0);
        componentNameArea.setForeground(Color.WHITE);
        componentNameArea.setBackground(TRANSPARENT);
        componentNameArea.setVisible(true);
        componentNameArea.setCaretColor(Color.WHITE);

        componentNameArea.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isTextFocused = true;
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        panel.add(componentNameArea);

        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                isMouseDragged = true;
                if (holdingComponent != null) {
                    holdingComponent.setDragged(true);
                }
                red.onMouseDrag(e.getX(), e.getY());
                green.onMouseDrag(e.getX(), e.getY());
                blue.onMouseDrag(e.getX(), e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }

        });


        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    shouldEdit.setA(true);
                }
                else if (e.getKeyCode() == KeyEvent.VK_E) {
                    shouldEdit.setB(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    shouldEdit.setA(false);
                }
                else if (e.getKeyCode() == KeyEvent.VK_E) {
                    shouldEdit.setB(false);
                }
            }
        });

        Dimension dim = new Dimension(width, height);

        panel.setMinimumSize(dim);
        panel.setPreferredSize(dim);
        panel.setMaximumSize(dim);

        frame.setResizable(true);

        frame.add(panel);

        frame.setLocationRelativeTo(null);

        frame.pack();

        frame.setVisible(true);

        while (true) {
            resizedWidth.setA(panel.getWidth());
            resizedHeight.setA(panel.getHeight());
            panel.repaint();
        }

    }

    ObjectValidator resizedWidth = new ObjectValidator(0, 0);
    ObjectValidator resizedHeight = new ObjectValidator(0, 0);

    public void drawPort(int x, int y) {
        if (isMouseOver(x - portSize / 2, y - portSize / 2, x + portSize / 2, y + portSize / 2, lastMouseX, lastMouseY)) {
            drawHoveredPort(x, y);
            return;
        }
        Color previousColor = g.getColor();
        g.setColor(new Color(0x202020));
        int size = portSize;
        g.fillOval(x - size / 2, y - size / 2 , size, size);
        g.setColor(previousColor);
    }

    public void drawHoveredPort(int x, int y) {
        Color previousColor = g.getColor();
        g.setColor(new Color(0x5A5A5A));
        int size = portSize;
        g.fillOval(x - size / 2, y - size / 2 , size, size);
        g.setColor(previousColor);
    }

    public void drawWire(int x1, int y1, int x2, int y2, boolean powered) {
//      ORIGINAL:  g.setColor(powered ? new Color(0xF52037) : new Color(0x1D2128));
        g.setColor(powered ? new Color(0xF13F50) : new Color(0x1D2128));
        Graphics2D g2d = (Graphics2D) g;
        Stroke previousStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(6));
        g.drawLine(x1, y1, x2, y2);
        g2d.setStroke(previousStroke);
    }

    int lastX;

    ArrayList<Button> buttons = new ArrayList<>();



    public interface ClickEvent {

        public void run();

    }

    public void addButton(String text, int x, ClickEvent clickEvent) {
        int buttonWidth = (int) g.getFontMetrics().getStringBounds(text, g).getWidth() + 40;
        buttons.add(new Button(text, x + lastX, 0, buttonWidth, 40, clickEvent));
        lastX += buttonWidth;
    }

    public void addToggler(Toggler toggler) {
        this.togglers.add(toggler);
    }

    public void addOutputPort(OutputPort outputPort) {
        this.outputPorts.add(outputPort);
    }

    public void drawToggler(Point point, boolean toggled, Point portPoint) {
        int x = point.x;
        int y = point.y;
        Color previousColor = g.getColor();

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);

        Stroke savedStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(4.5f));

        g.drawLine(x, y, portPoint.x, portPoint.y);

        g2d.setStroke(savedStroke);

        g.setColor(toggled ? new Color(0xF52037) : new Color(0x1D2128));
        int size = togglerSize;
        g.fillOval(x - size / 2, y - size / 2 , size, size);
        g.setColor(previousColor);
    }

    public void drawOutputPort(Point point, boolean toggled, Point portPoint) {
        int x = point.x;
        int y = point.y;
        Color previousColor = g.getColor();

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);

        Stroke savedStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(4.5f));

        g.drawLine(x, y, portPoint.x, portPoint.y);

        g2d.setStroke(savedStroke);

        g.setColor(toggled ? new Color(0xF52037) : new Color(0x1D2128));
        int size = togglerSize;
        g.fillOval(x - size / 2, y - size / 2 , size, size);
        g.setColor(previousColor);
    }

    public int getTextWidth(Graphics g, String text) {
        return (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
    }

    public int getTextHeight(Graphics g, String text) {
        return (int) g.getFontMetrics().getStringBounds(text, g).getHeight();
    }

    public void drawText(Graphics g, String text, int x, int y, float sizeMult, Color background) {

        Font font = g.getFont();
        int size = (int) (font.getSize() * sizeMult);
        g.setFont(new Font(font.getName(), Font.BOLD, size));

        int textWidth = getTextWidth(g, text);
        int textHeight = getTextHeight(g, text);

        Color prev = g.getColor();

        g.setColor((background == null || isBright(background)) ? Color.WHITE.darker() : Color.DARK_GRAY);
        g.drawString(text, x - textWidth / 2, y - textHeight / 2);

        g.setFont(font);
        g.setColor(prev);

    }

    public boolean isBright(Color color) {
        return !(getHSP(color) > 127.5);
    }

    public double getHSP(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return Math.sqrt(0.299 * (r * r) + 0.587 * (g * g) + 0.114 * (b * b));
    }

    public int getWidth() {
        return (int) this.resizedWidth.getA();
    }

    public int getHeight() {
        return (int) this.resizedHeight.getA();
    }
}
