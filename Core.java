import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.TimerTask;
import java.lang.Math;
import javax.sound.sampled.*;
import java.io.*;

class Core extends JPanel {

    private String winner = "";
    protected int x;
    protected int y;
    protected int diameter = 10;
    private double vx = 10, vy = 3;
    private int rightScore = 0, leftScore = 0;
    private int tileWidth, tileHeight;
    private boolean moveTile1Up, moveTile1Down, moveTile2Up, moveTile2Down;
    protected boolean ballShouldMove = false;
    private boolean gameOver = false;
    protected boolean isSlowed = false; //checks if the ball is slowed right now
    protected boolean ifSlowedRight = false; //checks if the ball slowed before
    protected boolean ifSlowedLeft = false; //checks if the ball slowed before
    protected boolean speedUpPressedRight = false;
    protected boolean speedUpPressedLeft = false;
    protected boolean collidedRightTile = false;
    protected boolean collidedLeftTile = false;
    protected boolean collidedLeftSide = false;
    protected boolean collidedRightSide = false;
    protected boolean speedUpBeforeRight = false;
    protected boolean speedUpBeforeLeft = false;
    protected ColoredShape tileLeft;
    protected ColoredShape tileRight;
    private Timer speedIncreaseTimer;
    private ArrayList<ColoredShape> tiles;
    protected Ball ball;
    private java.util.Timer slowDownTimer;
    private int lastWinner = 0; //1 means right, -1 means left
    private boolean speeding = false;
    private Shape tile1, tile2;

    JLabel flameLabelRight;
    JLabel flameLabelLeft;
    JLabel flameUsedLabelRight;
    JLabel flameUsedLabelLeft;
    JLabel freezeLabelRight;
    JLabel freezeLabelLeft;
    JLabel freezeUsedLabelRight;
    JLabel freezeUsedLabelLeft;


    public Core(int tileWidth, int tileHeight) {
        
        setOpaque(false);
        setLayout(null);
        
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tiles = new ArrayList<>();

        x = (int) Interface.frameWidth / 2;
        y = (int) Interface.frameHeight / 2;
        vx = (Math.random() < 0.5) ? vx : -1*vx;
        vy = (Math.random() < 0.5) ? vy : -1*vy;

        tile1 = new Rectangle(tileWidth / 2, Interface.frameHeight / 3, tileWidth, tileHeight);
        tile2 = new Rectangle(Interface.frameWidth - tileWidth*2, Interface.frameHeight / 3, tileWidth, tileHeight);
        tileLeft = new ColoredShape(tile1, Color.white);
        tileRight = new ColoredShape(tile2, Color.white);
        tiles.add(0, tileLeft);
        tiles.add(1, tileRight);

        ball = new Ball(x, y, diameter, Color.white, vx, vy);

        // Add KeyListener to detect key presses
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_W) {
                    moveTile1Up = true;
                } else if (keyCode == KeyEvent.VK_S) {
                    moveTile1Down = true;
                } else if (keyCode == KeyEvent.VK_UP) {
                    moveTile2Up = true;
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    moveTile2Down = true;
                }

                if (keyCode == KeyEvent.VK_SPACE) {
                    
                    if (gameOver) {
                        restartGame();
                    } else {
                        ballShouldMove = true;
                    }

                    speedIncreaseTimer.start();

                }

                if (keyCode == KeyEvent.VK_Z) {

                    if (!isSlowed && !ifSlowedLeft && ballShouldMove) {
                        isSlowed = true;
                        ifSlowedLeft = true;
                        slowDownLeft();
                        isSlowed = false;
                    }
                
                }

                if (keyCode == KeyEvent.VK_CONTROL) {

                    if (!isSlowed && !ifSlowedRight && ballShouldMove) {
                        isSlowed = true;
                        ifSlowedRight = true;
                        slowDownRight();
                        isSlowed = false;
                    }

                }

                if (keyCode == KeyEvent.VK_SHIFT && !speedUpBeforeRight) {

                    speedUpRight();
                    
                }

                if (keyCode == KeyEvent.VK_X && !speedUpBeforeLeft) {

                    speedUpLeft();
                    
                }

                if (keyCode == KeyEvent.VK_ESCAPE) {

                    restartGame();

                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_W) {
                    moveTile1Up = false;
                } else if (keyCode == KeyEvent.VK_S) {
                    moveTile1Down = false;
                } else if (keyCode == KeyEvent.VK_UP) {
                    moveTile2Up = false;
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    moveTile2Down = false;
                }
            }
        });
        
        ImageIcon originalIcon = new ImageIcon("images\\restartButton.jpg");

        int scaledWidth = 50;
        int scaledHeight = 50; // Automatically calculate the height to preserve aspect ratio
        Image scaledImage = originalIcon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel restartLabel = new JLabel(scaledIcon);

        restartLabel.setBounds(575, 6, scaledWidth, scaledHeight);

        add(restartLabel);

        restartLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                restartGame();
            }
        });
        
        //------------------------------------------------
        
        ImageIcon flameIcon = new ImageIcon("images\\flame.jpg");

        int flameScaledWidth = 30;
        int flameScaledHeight = 30;
        Image flameScaledImage = flameIcon.getImage().getScaledInstance(flameScaledWidth, flameScaledHeight, Image.SCALE_SMOOTH);
        ImageIcon flameScaledIcon = new ImageIcon(flameScaledImage);
        
        flameLabelRight = new JLabel(flameScaledIcon);
        flameLabelRight.setBounds(1100, 13, flameScaledWidth, flameScaledHeight);

        flameLabelLeft = new JLabel(flameScaledIcon);
        flameLabelLeft.setBounds(70, 13, flameScaledWidth, flameScaledHeight);

        add(flameLabelRight);
        add(flameLabelLeft);

        ImageIcon flameUsedIcon = new ImageIcon("images\\flame_used.jpg");

        Image flameUsedScaledImage = flameUsedIcon.getImage().getScaledInstance(flameScaledWidth, flameScaledHeight, Image.SCALE_SMOOTH);
        ImageIcon flameUsedScaledIcon = new ImageIcon(flameUsedScaledImage);
        
        flameUsedLabelRight = new JLabel(flameUsedScaledIcon);
        flameUsedLabelRight.setBounds(1100, 13, flameScaledWidth, flameScaledHeight);

        flameUsedLabelLeft = new JLabel(flameUsedScaledIcon);
        flameUsedLabelLeft.setBounds(70, 13, flameScaledWidth, flameScaledHeight);

        //----------------------------------------------------

        ImageIcon freezeIcon = new ImageIcon("images\\freeze.jpg");

        int freezeScaledWidth = 30;
        int freezeScaledHeight = 30;
        Image freezeScaledImage = freezeIcon.getImage().getScaledInstance(freezeScaledWidth, freezeScaledHeight, Image.SCALE_SMOOTH);
        ImageIcon freezeScaledIcon = new ImageIcon(freezeScaledImage);
        
        freezeLabelRight = new JLabel(freezeScaledIcon);
        freezeLabelRight.setBounds(1140, 13, freezeScaledWidth, freezeScaledHeight);

        freezeLabelLeft = new JLabel(freezeScaledIcon);
        freezeLabelLeft.setBounds(30, 13, freezeScaledWidth, freezeScaledHeight);

        add(freezeLabelRight);
        add(freezeLabelLeft);

        ImageIcon freezeUsedIcon = new ImageIcon("images\\freeze_used.jpg");

        Image freezeUsedScaledImage = freezeUsedIcon.getImage().getScaledInstance(freezeScaledWidth, freezeScaledHeight, Image.SCALE_SMOOTH);
        ImageIcon freezeUsedScaledIcon = new ImageIcon(freezeUsedScaledImage);
        
        freezeUsedLabelRight = new JLabel(freezeUsedScaledIcon);
        freezeUsedLabelRight.setBounds(1140, 13, freezeScaledWidth, freezeScaledHeight);

        freezeUsedLabelLeft = new JLabel(freezeUsedScaledIcon);
        freezeUsedLabelLeft.setBounds(30, 13, freezeScaledWidth, freezeScaledHeight);
        
        //-----------------------------------------------------
        
        Timer timer = new Timer(10, new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {

                moveBalls();
                checkCollision();
                moveTiles();
                gameOver();
                displayImages();
                repaint();
                //revalidate();
                
            }

        });

        timer.start();

        speedIncreaseTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (ballShouldMove) {

                    ball.setVx((int) (ball.getVx() * 1.25));
                    ball.setVy((int) (ball.getVy() * 1.25));

                }

            }
        });

    }

    public void moveBalls() {
        
        if (!gameOver && ballShouldMove) {    
            ball.setX(ball.getX() + ball.getVx());
            ball.setY(ball.getY() + ball.getVy());
        }
    
        if (ball.getX() > getWidth() - ball.getShape().width || ball.getX() < 0) {
            
            playSound("sounds\\collisionsound3.wav");
            
            if (ball.getX() > getWidth() - ball.getShape().width) {
                
                collidedRightSide = true;
                ball.setVx(-Math.abs(ball.getVx()));
                leftScore += 1;
                lastWinner = -1;

            } else if (ball.getX() < 0) {
                
                collidedLeftSide = true;
                ball.setVx(Math.abs(ball.getVx()));
                rightScore += 1;
                lastWinner = 1;

            }

            resetBallLocation();
            ballShouldMove = false;

        }

        if (ball.getY() > getHeight() - ball.getShape().height || ball.getY() < 0) {
            
            playSound("sounds\\collisionsound2.wav");
            ball.setVy(-ball.getVy());

        }

    }

    public void gameOver() {

        if (rightScore >= 10 || leftScore >= 10) {
            
            playSound("sounds\\gameoversound1.wav");
            gameOver = true;
            if (rightScore >= 10) {
                winner = "Right Player";
            } else {
                winner = "Left Player";
            }

            rightScore = 0;
            leftScore = 0;

        }

    }

    private void restartGame() {

        gameOver = false;
        winner = "";
        resetBallLocation();
        ballShouldMove = false;
        rightScore = 0;
        leftScore = 0;
        speedUpBeforeLeft = false;
        speedUpBeforeRight = false;
        ifSlowedRight = false;
        ifSlowedLeft = false;
        restartImages(); //should be after bool clearing.

    }
    
    public void resetBallLocation() {
        
        collidedLeftSide = false;
        collidedRightSide = false;
        speedUpPressedRight = false;
        speedUpPressedLeft = false;    

        if(ball.getColor() == Color.ORANGE) {
            ball.setColor(Color.WHITE);
            ball.setVx(ball.getVx() * 1/3);
        }

        if(tileRight.getColor() == Color.ORANGE) {
            tileRight.setColor(Color.WHITE);
        } else if (tileLeft.getColor() == Color.ORANGE){
            tileLeft.setColor(Color.WHITE);
        }
        
        ball.setX((int) (Interface.frameWidth / 2));
        ball.setY((int) (Interface.frameHeight / 2));

        ball.setVx(10);
        ball.setVy(3);

        if (lastWinner == 1) {

            ball.setVy((Math.random() < 0.5) ? ball.getVy() : -1 * ball.getVy());
            ball.setVx(Math.abs(ball.getVx()));

        }
        else if (lastWinner == -1) {

            ball.setVy((Math.random() < 0.5) ? ball.getVy() : -1 * ball.getVy());
            ball.setVx(-1 * Math.abs(ball.getVx()));

        }
        else if (lastWinner == 0) {

            ball.setVy((Math.random() < 0.5) ? ball.getVy() : -1 * ball.getVy());
            ball.setVx((Math.random() < 0.5) ? ball.getVx() : -1 * ball.getVx());

        }

    }
    

    public void checkCollision() {

        collidedRightTile = false;
        collidedLeftTile = false;
            
        for (ColoredShape tile : tiles) {
                
            double ballX = ball.getX();
            double ballY = ball.getY();
            double ballRadius = ball.getRadius();
            double ballVx = ball.getVx();
            double ballVy = ball.getVy();

            Shape tileShape = tile.getShape();
            Rectangle2D tileBounds = tileShape.getBounds2D();
            double tileLeft = tileBounds.getMinX();
            double tileR = tileBounds.getMaxX();
            double tileTop = tileBounds.getMinY();
            double tileBottom = tileBounds.getMaxY();

            // Calculate the number of steps based on the maximum velocity component
            double maxSpeed = Math.max(Math.abs(ballVx), Math.abs(ballVy));
            int numSteps = (int) Math.ceil(maxSpeed / ballRadius);

            // Perform collision detection for each step
            for (int i = 0; i < numSteps; i++) {
                double stepX = ballX + ballVx * (i + 1) / numSteps;
                double stepY = ballY + ballVy * (i + 1) / numSteps;

                if (stepX + ballRadius >= tileLeft && stepX - ballRadius <= tileR &&
                stepY + ballRadius >= tileTop && stepY - ballRadius <= tileBottom) {

                    playSound("sounds\\collisionsound1.wav");

                    // Determine the side of the collision
                    double overlapX = Math.max(0, Math.min(stepX + ballRadius, tileR) - Math.max(stepX - ballRadius, tileLeft));
                    double overlapY = Math.max(0, Math.min(stepY + ballRadius, tileBottom) - Math.max(stepY - ballRadius, tileTop));

                    boolean collisionFromLeftOrRight = overlapX < overlapY;

                    if (collisionFromLeftOrRight) {
                        
                        if (ball.getVx() < 0) {
                            collidedLeftTile = true;

                            if (speedUpPressedLeft) {
                                speedUpCollided();
                            }

                        }

                        if (ball.getVx() > 0) {
                            collidedRightTile = true;
                            
                            if (speedUpPressedRight) {
                                speedUpCollided();
                            }

                        }

                        if(collidedLeftTile && speedUpPressedRight && ball.getColor() == Color.ORANGE) {
                            stopSpeedingRight();
                        }

                        if(collidedRightTile && speedUpPressedLeft && ball.getColor() == Color.ORANGE) {
                            stopSpeedingLeft();
                        }

                        if(moveTile1Up && collidedLeftTile) {
                            ball.setVy(ball.getVy() + 5/2);
                        }
                        else if (moveTile1Down && collidedLeftTile) {
                            ball.setVy(ball.getVy() - 5/2);
                        }
                        else if(moveTile2Up && collidedRightTile) {
                            ball.setVy(ball.getVy() + 5/2);
                        }
                        else if (moveTile2Down && collidedRightTile) {
                            ball.setVy(ball.getVy() - 5/2);
                        }

                        ball.setVx((int) -ball.getVx());

                    } else {
 
                        ball.setVy((int) -ball.getVy());

                    }

                    break; // Exit the loop after handling the collision
                }

            }

        }
    }

    public void moveTiles() {

        if (moveTile1Up) {
            Rectangle bounds = (Rectangle) tiles.get(0).getShape().getBounds2D();
            int newY = (int) (bounds.getY() - 5);
            
            if (newY >= 0) { 
                tiles.get(0).setShape(new Rectangle((int) bounds.getX(), newY, tileWidth, tileHeight));
            }

        } else if (moveTile1Down) {
            
            Rectangle bounds = (Rectangle) tiles.get(0).getShape().getBounds2D();
            int newY = (int) (bounds.getY() + 5);
            
            if (newY + tileHeight <= getHeight()) { // Check if the new position is within the frame
                tiles.get(0).setShape(new Rectangle((int) bounds.getX(), newY, tileWidth, tileHeight));
            }

        }
    
        if (moveTile2Up) {

            Rectangle bounds = (Rectangle) tiles.get(1).getShape().getBounds2D();
            int newY = (int) (bounds.getY() - 5);
            
            if (newY >= 0) { // Check if the new position is within the frame
                tiles.get(1).setShape(new Rectangle((int) bounds.getX(), newY, tileWidth, tileHeight));
            }

        } else if (moveTile2Down) {

            Rectangle bounds = (Rectangle) tiles.get(1).getShape().getBounds2D();
            int newY = (int) (bounds.getY() + 5);
            
            if (newY + tileHeight <= getHeight()) { // Check if the new position is within the frame
                tiles.get(1).setShape(new Rectangle((int) bounds.getX(), newY, tileWidth, tileHeight));
            }

        }
    }

    public static void playSound(String filePath) {

        try {

            File sound = new File(filePath);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(sound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {

            e.printStackTrace();

        }

    }

    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (ColoredShape tile : tiles) {
            g2d.setColor(tile.getColor());
            g2d.fill(tile.getShape());
        }

        g2d.setColor(ball.getColor());
        g2d.fill(ball.getShape());

        String strLeftScore = String.valueOf(leftScore);
        String strRightScore = String.valueOf(rightScore);

        Font scoreFont = new Font("Arial", Font.BOLD, 36);
        g2d.setFont(scoreFont);

        FontMetrics fmLeft = g.getFontMetrics(scoreFont);
        FontMetrics fmRight = g.getFontMetrics(scoreFont);
        int xLeft = (getWidth() - fmLeft.stringWidth(strLeftScore)) / 2 - 80;
        int xRight = (getWidth() - fmRight.stringWidth(strRightScore)) / 2 + 80;
        int yLeft = 40;
        int yRight = 40;
            
        g2d.drawString(strLeftScore, xLeft, yLeft);
        g2d.drawString(strRightScore, xRight, yRight);

        if (gameOver) {
            
            String gameOverMessage = "Game Over, " + winner + " won.";
            
            Font font = new Font("Arial", Font.BOLD, 36);
            g2d.setFont(font);
            
            FontMetrics fm = g.getFontMetrics(font);
            int x = (getWidth() - fm.stringWidth(gameOverMessage)) / 2;
            int y = getHeight() / 2;
            
            g2d.drawString(gameOverMessage, x, y);
            
        }
        
    }

    public void restartImages() {
        
        remove(flameUsedLabelRight);
        remove(flameUsedLabelLeft);
        remove(freezeUsedLabelRight);
        remove(freezeUsedLabelLeft);

        revalidate();
        
        add(flameLabelRight);
        add(flameLabelLeft);
        add(freezeLabelRight);
        add(freezeLabelLeft);

        repaint();

    }

    public void displayImages() {
        
        if (speedUpBeforeRight) {

            remove(flameLabelRight);
            revalidate();
            add(flameUsedLabelRight);    

        }

        if (speedUpBeforeLeft) {

            remove(flameLabelLeft);
            revalidate();
            add(flameUsedLabelLeft);

        }

        //----------------------------------------

        if (ifSlowedRight) {

            remove(freezeLabelRight);
            add(freezeUsedLabelRight);

        }

        if (ifSlowedLeft) {

            remove(freezeLabelLeft);
            add(freezeUsedLabelLeft);

        }

    }

    public int getRightScore() {
        return rightScore;
    }

    public int getLeftScore() {
        return leftScore;
    }

    // Special Powers

    public void speedUpRight() {

        if (ballShouldMove) {
            speedUpPressedRight = true;
            tileRight.setColor(Color.ORANGE); 
            speedUpBeforeRight = true;
        }

    }

    public void speedUpLeft() {

        if (ballShouldMove) {
            speedUpPressedLeft = true;
            tileLeft.setColor(Color.ORANGE); 
            speedUpBeforeLeft = true;
        }

    }

    public void speedUpCollided() {
        
        playSound("sounds\\fire.wav");
        speeding = true;
        
        if (speedUpPressedRight) {
            
            ball.setVx(ball.getVx() * 3);
            tileRight.setColor(Color.WHITE);
            ball.setColor(Color.ORANGE);

        }

        if (speedUpPressedLeft) {
            
            ball.setVx(ball.getVx() * 3);
            tileLeft.setColor(Color.WHITE);
            ball.setColor(Color.ORANGE);

        }

    }

    public void stopSpeedingLeft() {

        speeding = false;
        ball.setColor(Color.WHITE);
        ball.setVx(ball.getVx() * 1/3);
        speedUpPressedLeft = false;

    }

    public void stopSpeedingRight() {

        speeding = false;
        ball.setColor(Color.WHITE);
        ball.setVx(ball.getVx() * 1/3);
        speedUpPressedRight = false;

    }

    public void slowDownRight() {
        
        if (speeding && ball.getVx() > 0) {

            stopSpeedingLeft();

        }
        else if (speeding && ball.getVx() < 0) {

            stopSpeedingRight();

        }
        
        long delay = 1250;
        playSound("sounds\\freeze.wav");

        ball.setVx(ball.getVx() * 1/3);
        ball.setVy(ball.getVy() * 1/3);
        ball.setColor(Color.CYAN);
        tileRight.setColor(Color.CYAN);

        slowDownTimer = new java.util.Timer();
        slowDownTimer.schedule(new TimerTask() {

            @Override
            public void run() {
  
                ball.setVx(ball.getVx() * 3);
                ball.setVy(ball.getVy() * 3);
                ball.setColor(Color.WHITE);
                tileRight.setColor(Color.WHITE);
                //Core.playSound("sounds\\freeze_reverse.wav");

            }

        }, delay);  

    }

    public void slowDownLeft() {
        
        if (speeding && ball.getVx() > 0) {

            stopSpeedingLeft();

        }
        else if (speeding && ball.getVx() < 0) {

            stopSpeedingRight();

        }
        
        long delay = 1250;
        playSound("sounds\\freeze.wav");

        ball.setVx(ball.getVx() * 1/3);
        ball.setVy(ball.getVy() * 1/3);
        ball.setColor(Color.CYAN);
        tileLeft.setColor(Color.CYAN);

        slowDownTimer = new java.util.Timer();
        slowDownTimer.schedule(new TimerTask() {

            @Override
            public void run() {
  
                ball.setVx(ball.getVx() * 3);
                ball.setVy(ball.getVy() * 3);
                ball.setColor(Color.WHITE);
                tileLeft.setColor(Color.WHITE);
                //Core.playSound("sounds\\freeze_reverse.wav");

            }

        }, delay);  

    }

}