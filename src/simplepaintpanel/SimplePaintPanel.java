
package simplepaintpanel;

 import java.awt.*;
 import java.util.*;
import javax.swing.*;

  import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


class SimplePaintPanel extends JPanel {
    private final Set<Point> blackPixels = new HashSet<>();
    private final Set<Point> selectedShape = new HashSet<>();
    private final int brushSize;
    private Color currentColor = Color.BLACK;
    private boolean drawFreehand = true;
    private final Deque<Action> actionStack = new LinkedList<>();
    private ShapeType currentShape = ShapeType.NONE;
    private Point startPoint = null;
    private Point endPoint = null;

    public SimplePaintPanel() {
        this(5, new HashSet<>());
    }

    public SimplePaintPanel(Set<Point> blackPixels) {
        this(5, blackPixels);
    }

    public SimplePaintPanel(int brushSize, Set<Point> blackPixels) {
        this.setPreferredSize(new Dimension(600, 400));
        this.brushSize = brushSize;
        this.blackPixels.addAll(blackPixels);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent ev) {
                if (drawFreehand) {
                    Set<Point> pixelsToAdd = getPixelsAround(ev.getPoint());
                    addPixels(pixelsToAdd);
                    addAction(new Action(pixelsToAdd, true));
                } else {
                    // Implement shape selection logic here
                    selectShape(ev.getPoint());
                }
            }

            @Override
            public void mousePressed(MouseEvent ev) {
                startPoint = ev.getPoint();
                endPoint = ev.getPoint();
                if (!drawFreehand && currentShape != ShapeType.NONE) {
                    // Clear selected shape when starting to draw
                    selectedShape.clear();
                }
            }

            @Override
            public void mouseReleased(MouseEvent ev) {
                if (!drawFreehand && currentShape != ShapeType.NONE) {
                    endPoint = ev.getPoint();
                    switch (currentShape) {
                        case LINE:
                            drawLine();
                            break;
                        case RECTANGLE:
                            drawRectangle();
                            break;
                        case CIRCLE:
                            drawCircle();
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        this.addMouseMotionListener(mouseAdapter);
        this.addMouseListener(mouseAdapter);
    }

    @Override
    public void paint(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();
        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);
        g.setColor(currentColor);
        if (drawFreehand) {
            for (Point point : blackPixels)
                g.fillRect(point.x, point.y, brushSize, brushSize);
        } else {
            // Draw selected shape differently
            g.setColor(Color.RED);
            for (Point point : selectedShape)
                g.drawRect(point.x, point.y, brushSize, brushSize);
            g.setColor(currentColor);
            for (Point point : blackPixels)
                g.fillRect(point.x, point.y, brushSize, brushSize);
            if (startPoint != null && endPoint != null) {
                g.drawRect(
                    Math.min(startPoint.x, endPoint.x), 
                    Math.min(startPoint.y, endPoint.y), 
                    Math.abs(startPoint.x - endPoint.x), 
                    Math.abs(startPoint.y - endPoint.y)
                );
            }
        }
    }

    public void clear() {
        this.blackPixels.clear();
        this.repaint();
    }

    public void addPixels(Collection<? extends Point> pixels) {
        this.blackPixels.addAll(pixels);
        this.repaint();
    }

    public void removePixels(Collection<? extends Point> pixels) {
        this.blackPixels.removeAll(pixels);
        this.repaint();
    }

    public void selectShape(Point point) {
        // Implement shape selection logic here
        if (blackPixels.contains(point)) {
            selectedShape.add(point);
            this.repaint();
        }
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public void setDrawFreehand(boolean drawFreehand) {
        this.drawFreehand = drawFreehand;
    }

    private Set<Point> getPixelsAround(Point point) {
        Set<Point> points = new HashSet<>();
        for (int x = point.x - brushSize / 2; x < point.x + brushSize / 2; x++)
            for (int y = point.y - brushSize / 2; y < point.y + brushSize / 2; y++)
                points.add(new Point(x, y));
        return points;
    }

    public void addAction(Action action) {
        actionStack.push(action);
    }

    public void undoLastAction() {
        if (!actionStack.isEmpty()) {
            Action lastAction = actionStack.pop();
            if (lastAction.isAddAction()) {
                removePixels(lastAction.getPixels());
            } else {
                addPixels(lastAction.getPixels());
            }
        }
    }

    private void drawLine() {
        Set<Point> linePixels = BresenhamLineAlgorithm(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        addPixels(linePixels);
        addAction(new Action(linePixels, true));
    }

    private void drawRectangle() {
        int minX = Math.min(startPoint.x, endPoint.x);
        int minY = Math.min(startPoint.y, endPoint.y);
        int width = Math.abs(startPoint.x - endPoint.x);
        int height = Math.abs(startPoint.y - endPoint.y);
        Set<Point> rectanglePixels = new HashSet<>();
        for (int x = minX; x < minX + width; x++) {
            rectanglePixels.add(new Point(x, minY));
            rectanglePixels.add(new Point(x, minY + height));
        }
        for (int y = minY; y < minY + height; y++) {
            rectanglePixels.add(new Point(minX, y));
            rectanglePixels.add(new Point(minX + width, y));
        }
        addPixels(rectanglePixels);
        addAction(new Action(rectanglePixels, true));
    }

    private void drawCircle() {
        int radius = (int) Math.sqrt(Math.pow(startPoint.x - endPoint.x, 2) + Math.pow(startPoint.y - endPoint.y, 2));
        Set<Point> circlePixels = BresenhamCircleAlgorithm(startPoint.x, startPoint.y, radius);
        addPixels(circlePixels);
        addAction(new Action(circlePixels, true));
    }

    private Set<Point> BresenhamLineAlgorithm(int x1, int y1, int x2, int y2) {
        Set<Point> linePixels = new HashSet<>();
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            linePixels.add(new Point(x1, y1));
            if (x1 == x2 && y1 == y2) {
                break;
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
        return linePixels;
    }

    private Set<Point> BresenhamCircleAlgorithm(int centerX, int centerY, int radius) {
        Set<Point> circlePixels = new HashSet<>();
        int x = radius;
        int y = 0;
        int radiusError = 1 - x;

        while (x >= y) {
            circlePixels.add(new Point(centerX + x, centerY + y));
            circlePixels.add(new Point(centerX - x, centerY + y));
            circlePixels.add(new Point(centerX + x, centerY - y));
            circlePixels.add(new Point(centerX - x, centerY - y));
            circlePixels.add(new Point(centerX + y, centerY + x));
            circlePixels.add(new Point(centerX - y, centerY + x));
            circlePixels.add(new Point(centerX + y, centerY - x));
            circlePixels.add(new Point(centerX - y, centerY - x));

            y++;
            if (radiusError < 0) {
                radiusError += 2 * y + 1;
            } else {
                x--;
                radiusError += 2 * (y - x + 1);
            }
        }
        return circlePixels;
    }

    public void setCurrentShape(ShapeType shapeType) {
        this.currentShape = shapeType;
    }

    void setStartPoint(Point point) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void setEndPoint(Point point) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void drawShape() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public enum ShapeType {
        NONE, LINE, RECTANGLE, CIRCLE
    }
}



