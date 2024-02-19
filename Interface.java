import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Interface extends JFrame {

    protected static int frameHeight = 637;
    protected static int frameWidth = 1214;
    protected int tileWidth = 20;
    protected int tileHeight = 200;

    public Interface() {

        setSize(frameWidth, frameHeight);
        getContentPane().setBackground(Color.BLACK);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setTitle("Ping Pong");
        setResizable(false);
        add(new Core(tileWidth, tileHeight));
        setVisible(true);

    }

    public static void main(String[] args) {
        new Interface();
    }

}

class ColoredShape {
    
    protected Shape shape;
    protected Color color;

    public ColoredShape(Shape shape, Color color) {
        
        this.shape = shape;
        this.color = color;

    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}

class Ball {
    
    private Ellipse2D.Double shape;
    private Color color;
    private double vx;
    private double vy;

    public Ball(double x, double y, int diameter, Color color, double vx, double vy) {
        
        this.shape = new Ellipse2D.Double(x, y, diameter, diameter);
        this.color = color;
        this.vx = vx;
        this.vy = vy;

    }

    public Ellipse2D.Double getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getX() {
        return (int) shape.x;
    }

    public void setX(double x) {
        shape.x = x;
    }

    public double getY() {
        return (int) shape.y;
    }

    public void setY(double y) {
        shape.y = y;
    }

    public double getRadius(){
        return shape.getWidth() / 2;
    }

    public Point2D.Double getCenterPoint() {
        Point2D.Double centerPoint = new Point2D.Double(this.getX() + this.getRadius(), this.getY() + this.getRadius());
        return centerPoint;
    }

}
