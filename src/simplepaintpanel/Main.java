 
package simplepaintpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.JMenuBar;
 
public class Main extends JFrame implements ActionListener {
    private final String ACTION_NEW = "New Image";
    private final String ACTION_LOAD = "Load Image";
    private final String ACTION_SAVE = "Save Image";
    private final String ACTION_UNDO = "Undo";
    private final String ACTION_LINE = "Line";
    private final String ACTION_RECTANGLE = "Rectangle";
    private final String ACTION_CIRCLE = "Circle";

    private final SimplePaintPanel paintPanel = new SimplePaintPanel();
    private final JToggleButton drawFreehandToggle = new JToggleButton("Draw Freehand");

    public Main() {
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Simple Paint");

        initMenu();
        initButtons();

        this.getContentPane().add(paintPanel);

        pack();
        setVisible(true);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem mnuNew = new JMenuItem(ACTION_NEW);
        JMenuItem mnuLoad = new JMenuItem(ACTION_LOAD);
        JMenuItem mnuSave = new JMenuItem(ACTION_SAVE);
        JMenuItem mnuUndo = new JMenuItem(ACTION_UNDO);
        mnuNew.setActionCommand(ACTION_NEW);
        mnuLoad.setActionCommand(ACTION_LOAD);
        mnuSave.setActionCommand(ACTION_SAVE);
        mnuUndo.setActionCommand(ACTION_UNDO);
        mnuNew.addActionListener(this);
        mnuLoad.addActionListener(this);
        mnuSave.addActionListener(this);
        mnuUndo.addActionListener(this);
        menu.add(mnuNew);
        menu.add(mnuLoad);
        menu.add(mnuSave);
        menu.add(mnuUndo);
        menuBar.add(menu);

        JMenu shapeMenu = new JMenu("Shapes");
        JMenuItem lineMenuItem = new JMenuItem(ACTION_LINE);
        JMenuItem rectangleMenuItem = new JMenuItem(ACTION_RECTANGLE);
        JMenuItem circleMenuItem = new JMenuItem(ACTION_CIRCLE);
        lineMenuItem.setActionCommand(ACTION_LINE);
        rectangleMenuItem.setActionCommand(ACTION_RECTANGLE);
        circleMenuItem.setActionCommand(ACTION_CIRCLE);
        lineMenuItem.addActionListener(this);
        rectangleMenuItem.addActionListener(this);
        circleMenuItem.addActionListener(this);
        shapeMenu.add(lineMenuItem);
        shapeMenu.add(rectangleMenuItem);
        shapeMenu.add(circleMenuItem);
        menuBar.add(shapeMenu);

        setJMenuBar(menuBar);
    }
 private void initButtons() {
    JPanel buttonPanel = new JPanel();
    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> paintPanel.clear());
    JButton colorButton = new JButton("Select Color");
    colorButton.addActionListener(e -> {
        Color selectedColor = JColorChooser.showDialog(this, "Choose Color", Color.BLACK);
        paintPanel.setCurrentColor(selectedColor);
    });
    JButton brushButton = new JButton("Brush Size");
    brushButton.addActionListener(e -> {
        // Implement brush size selection
    });
    JButton eraserButton = new JButton("Eraser");
    eraserButton.addActionListener(e -> {
        paintPanel.setCurrentColor(Color.WHITE);
    });
    drawFreehandToggle.addActionListener(e -> {
        paintPanel.setDrawFreehand(drawFreehandToggle.isSelected());
    });

    JButton undoButton = new JButton("Undo");
    undoButton.addActionListener(e -> {
        paintPanel.undoLastAction();
    });

    JButton rectangleButton = new JButton("Rectangle");
    rectangleButton.addActionListener(e -> {
        paintPanel.setCurrentShape(SimplePaintPanel.ShapeType.RECTANGLE);
        // Add mouse listener to allow drawing rectangle
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                paintPanel.setStartPoint(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                paintPanel.setEndPoint(e.getPoint());
                paintPanel.drawShape();
            }
        };
        paintPanel.addMouseListener(mouseAdapter);
        paintPanel.addMouseMotionListener(mouseAdapter);
    });

    JButton circleButton = new JButton("Circle");
    circleButton.addActionListener(e -> {
        paintPanel.setCurrentShape(SimplePaintPanel.ShapeType.CIRCLE);
        // Add mouse listener to allow drawing circle
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                paintPanel.setStartPoint(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                paintPanel.setEndPoint(e.getPoint());
                paintPanel.drawShape();
            }
        };
        paintPanel.addMouseListener(mouseAdapter);
        paintPanel.addMouseMotionListener(mouseAdapter);
    });

    JButton lineButton = new JButton("Line");
    lineButton.addActionListener(e -> {
        paintPanel.setCurrentShape(SimplePaintPanel.ShapeType.LINE);
        // Add mouse listener to allow drawing line
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                paintPanel.setStartPoint(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                paintPanel.setEndPoint(e.getPoint());
                paintPanel.drawShape();
            }
        };
        paintPanel.addMouseListener(mouseAdapter);
        paintPanel.addMouseMotionListener(mouseAdapter);
    });

    buttonPanel.add(clearButton);
    buttonPanel.add(colorButton);
    buttonPanel.add(brushButton);
    buttonPanel.add(eraserButton);
    buttonPanel.add(drawFreehandToggle);
    buttonPanel.add(undoButton);
    buttonPanel.add(rectangleButton);
    buttonPanel.add(circleButton);
    buttonPanel.add(lineButton);

    this.getContentPane().add(buttonPanel, BorderLayout.NORTH);
}




   
    @Override
    public void actionPerformed(ActionEvent ev) {
        String command = ev.getActionCommand();
        switch (command) {
            case ACTION_NEW:
                paintPanel.clear();
                break;
            case ACTION_LOAD:
                doLoadImage();
                break;
            case ACTION_SAVE:
                doSaveImage();
                break;
            case ACTION_UNDO:
                paintPanel.undoLastAction();
                break;
            case ACTION_LINE:
                paintPanel.setCurrentShape(SimplePaintPanel.ShapeType.LINE);
                break;
            case ACTION_RECTANGLE:
                paintPanel.setCurrentShape(SimplePaintPanel.ShapeType.RECTANGLE);
                break;
            case ACTION_CIRCLE:
                paintPanel.setCurrentShape(SimplePaintPanel.ShapeType.CIRCLE);
                break;
        }
    }

    private void doSaveImage() {
        // Saving image functionality
    }

    private void doLoadImage() {
        // Loading image functionality
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}


class Action {
    private final Set<Point> pixels;
    private final boolean addAction;

    public Action(Set<Point> pixels, boolean addAction) {
        this.pixels = pixels;
        this.addAction = addAction;
    }

    public Set<Point> getPixels() {
        return pixels;
    }

    public boolean isAddAction() {
        return addAction;
    }
}
