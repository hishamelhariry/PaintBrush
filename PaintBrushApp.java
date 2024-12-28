import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class PaintBrushApp extends JFrame {
    private JPanel drawingPanel;
    private JButton lineButton, rectangleButton, ovalButton, pencilButton, eraserButton, clearButton, undoButton;
    private JRadioButton solidButton, dottedButton;
    private ButtonGroup shapeStyleGroup;
    private JComboBox<String> colorScheme;
    private JSlider brushSizeSlider;
    
    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    private boolean isPencil = false;
    private boolean isEraser = false;
    private boolean isSolid = true;
    private String currentShape = "Line";
    
    private Stack<ShapeAction> actionStack = new Stack<>();
    
    private Point startPoint = null;
    
    public PaintBrushApp() {
        setTitle("Paint Brush Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for drawing
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (ShapeAction action : actionStack) {
                    action.draw(g);
                }
            }
        };
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.setPreferredSize(new Dimension(600, 500));
        drawingPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                if (startPoint != null) {
                    Point endPoint = e.getPoint();
                    ShapeAction action = createShapeAction(startPoint, endPoint);
                    if (action != null) {
                        actionStack.push(action);
                        repaint();
                    }
                }
                startPoint = null;
            }
        });

        drawingPanel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (isPencil && startPoint != null) {
                    Point endPoint = e.getPoint();
                    ShapeAction action = new ShapeAction("Pencil", startPoint, endPoint, currentColor, brushSize, isSolid);
                    actionStack.push(action);
                    startPoint = endPoint;
                    repaint();
                }
            }
        });

        
        JPanel toolsPanel = new JPanel();
        lineButton = new JButton("Line");
        rectangleButton = new JButton("Rectangle");
        ovalButton = new JButton("Oval");
        pencilButton = new JButton("Pencil");
        eraserButton = new JButton("Eraser");
        clearButton = new JButton("Clear");
        undoButton = new JButton("Undo");

        lineButton.addActionListener(e -> currentShape = "Line");
        rectangleButton.addActionListener(e -> currentShape = "Rectangle");
        ovalButton.addActionListener(e -> currentShape = "Oval");
        pencilButton.addActionListener(e -> { isPencil = true; isEraser = false; });
        eraserButton.addActionListener(e -> { isPencil = false; isEraser = true; });
        clearButton.addActionListener(e -> {
            actionStack.clear();
            repaint();
        });
        undoButton.addActionListener(e -> {
            if (!actionStack.isEmpty()) {
                actionStack.pop();
                repaint();
            }
        });

        toolsPanel.add(lineButton);
        toolsPanel.add(rectangleButton);
        toolsPanel.add(ovalButton);
        toolsPanel.add(pencilButton);
        toolsPanel.add(eraserButton);
        toolsPanel.add(clearButton);
        toolsPanel.add(undoButton);

        
        solidButton = new JRadioButton("Solid",true);
        dottedButton = new JRadioButton("Dotted");
        solidButton.addActionListener(e -> isSolid = true);
        dottedButton.addActionListener(e -> isSolid = false);

        shapeStyleGroup = new ButtonGroup();
        shapeStyleGroup.add(solidButton);
        shapeStyleGroup.add(dottedButton);

        JPanel stylePanel = new JPanel();
        stylePanel.add(solidButton);
        stylePanel.add(dottedButton);

        // Color Scheme dropdown
        colorScheme = new JComboBox<>(new String[]{"Black", "Red", "Green", "Blue", "Yellow"});
        colorScheme.addActionListener(e -> {
            String selectedColor = (String) colorScheme.getSelectedItem();
            switch (selectedColor) {
                case "Black": currentColor = Color.BLACK; break;
                case "Red": currentColor = Color.RED; break;
                case "Green": currentColor = Color.GREEN; break;
                case "Blue": currentColor = Color.BLUE; break;
                case "Yellow": currentColor = Color.YELLOW; break;
            }
        });

        
        brushSizeSlider = new JSlider(1, 20, 5);
        brushSizeSlider.addChangeListener(e -> brushSize = brushSizeSlider.getValue());

        JPanel controlPanel = new JPanel();
        controlPanel.add(colorScheme);
        controlPanel.add(brushSizeSlider);
        
       
        getContentPane().add(toolsPanel, BorderLayout.NORTH);
        getContentPane().add(drawingPanel, BorderLayout.CENTER);
        getContentPane().add(stylePanel, BorderLayout.SOUTH);
        getContentPane().add(controlPanel, BorderLayout.WEST);
    }

    private ShapeAction createShapeAction(Point start, Point end) {
        switch (currentShape) {
            case "Line":
                return new ShapeAction("Line", start, end, currentColor, brushSize, isSolid);
            case "Rectangle":
                return new ShapeAction("Rectangle", start, end, currentColor, brushSize, isSolid);
            case "Oval":
                return new ShapeAction("Oval", start, end, currentColor, brushSize, isSolid);
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaintBrushApp app = new PaintBrushApp();
            app.setVisible(true);
        });
    }
}

class ShapeAction {
    private String shapeType;
    private Point start, end;
    private Color color;
    private int size;
    private boolean solid;

    public ShapeAction(String shapeType, Point start, Point end, Color color, int size, boolean solid) {
        this.shapeType = shapeType;
        this.start = start;
        this.end = end;
        this.color = color;
        this.size = size;
        this.solid = solid;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        if (solid) {
            ((Graphics2D) g).setStroke(new BasicStroke(size));
        } else {
            ((Graphics2D) g).setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0));
        }

        switch (shapeType) {
            case "Line":
                g.drawLine(start.x, start.y, end.x, end.y);
                break;
            case "Rectangle":
                int width = Math.abs(end.x - start.x);
                int height = Math.abs(end.y - start.y);
                g.drawRect(Math.min(start.x, end.x), Math.min(start.y, end.y), width, height);
                break;
            case "Oval":
                int ovalWidth = Math.abs(end.x - start.x);
                int ovalHeight = Math.abs(end.y - start.y);
                g.drawOval(Math.min(start.x, end.x), Math.min(start.y, end.y), ovalWidth, ovalHeight);
                break;
            case "Pencil":
                g.drawLine(start.x, start.y, end.x, end.y);
                break;
        }
    }
}
