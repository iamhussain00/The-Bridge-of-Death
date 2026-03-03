package myGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class The_Bridge_of_Death {
	
    private JFrame frame; 
    private JPanel mainPanel = new JPanel(new BorderLayout());
    private JLabel titleLabel = new JLabel("The Bridge of Death", SwingConstants.CENTER);
    private JLabel startLabel = new JLabel("START", SwingConstants.CENTER);
    private JLabel finishLabel = new JLabel("FINISH", SwingConstants.CENTER);
    private JPanel gridPanel = new JPanel(new GridLayout(6, 2, 5, 5));
    private JPanel[][] glassPanels = new JPanel[6][2];
    private String footsteps = "/images/footsteps.png";
    private String cracks = "/images/cracks.png";
    private String[][] imagesOrder = new String[6][2];
    private Random random = new Random();
    private int currentStep = 0;
    private boolean gameActive = true;
    
    public The_Bridge_of_Death() {
        frame = new JFrame(); 
        initializeComponents();
        setupNewGame();
        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }

    private void initializeComponents() {
    
        frame.setTitle("Bridge of Death");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBackground(new Color(40, 40, 40));
        titleLabel.setOpaque(true);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        configureSectionLabel(startLabel, 1);
        configureSectionLabel(finishLabel, 2);         
        
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        initializeGlassPanels();

      
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(startLabel, BorderLayout.CENTER);
        
        JPanel finishPanel = new JPanel(new BorderLayout());
        finishPanel.add(finishLabel, BorderLayout.CENTER);
        finishPanel.setPreferredSize(new Dimension(0, 50)); 
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(finishPanel, BorderLayout.SOUTH);
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    private void configureSectionLabel(JLabel label, int f1) {
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        if(f1 == 1) label.setBackground(new Color(70, 70, 70));
        else label.setBackground(new Color(20, 50, 100));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    private void initializeGlassPanels() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 2; j++) {
                glassPanels[i][j] = new JPanel(new BorderLayout());
                glassPanels[i][j].setBackground(new Color(200, 220, 255));
                glassPanels[i][j].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                gridPanel.add(glassPanels[i][j]);
            }
        }
    }
    
    private void displayImage(String imageName, JPanel panel) {
        panel.removeAll();
        ImageIcon icon = new ImageIcon(getClass().getResource(imageName));
        
        double scale = (imageName.equals(cracks)) 
            ? Math.max((double) panel.getWidth() / icon.getIconWidth(), (double) panel.getHeight() / icon.getIconHeight())
            : Math.min((double) panel.getWidth() / icon.getIconWidth(), (double) panel.getHeight() / icon.getIconHeight());

        Image scaledImage = icon.getImage().getScaledInstance(
            (int)(icon.getIconWidth() * scale), 
            (int)(icon.getIconHeight() * scale), 
            Image.SCALE_SMOOTH
        );

        JLabel label = new JLabel(new ImageIcon(scaledImage));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        panel.setLayout(new BorderLayout());
        panel.add(label, BorderLayout.CENTER);

        panel.revalidate();
        panel.repaint();
    }	
    
    private void setupNewGame() {
        gameActive = true;
        currentStep = 0;
        generatePath();
        refreshGlassPanels();
        enableCurrentStep();
    }

    private void generatePath() {
        for (int i = 0; i < 6; i++) {
            boolean leftSafe = random.nextBoolean();
            imagesOrder[i][0] = leftSafe ? footsteps : cracks;
            imagesOrder[i][1] = leftSafe ? cracks : footsteps;
        }
    } 
    
    private void refreshGlassPanels() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 2; j++) {
                glassPanels[i][j].removeAll();
                glassPanels[i][j].setBackground(new Color(200, 220, 255));
                glassPanels[i][j].revalidate();
                glassPanels[i][j].repaint();

                for (MouseListener listener : glassPanels[i][j].getMouseListeners()) {
                    glassPanels[i][j].removeMouseListener(listener);
                }

                final int row = i;
                final int col = j;
                glassPanels[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (gameActive && row == currentStep) {
                            handleGlassSelection(row, col);
                        }
                    }
                });
            }
        }
    }

    private void handleGlassSelection(int row, int col) {
        if (!gameActive) return;
        revealGlass(row, col);
        checkGlassSafety(row, col);
    }

    private void revealGlass(int row, int col) {
        displayImage(imagesOrder[row][col], glassPanels[row][col]);
        glassPanels[row][col].setEnabled(false);
        glassPanels[row][1 - col].setEnabled(false);
    }

    private void checkGlassSafety(int row, int col) {
        if (imagesOrder[row][col].equals(cracks)) {
            handleGameLoss();
        } else {
            progressToNextStep();
        }
    }

    private void handleGameLoss() {
        gameActive = false;
        JOptionPane.showMessageDialog(frame, "You fell through the glass! Game Over.");
        setupNewGame();
    }
    
    
    private void progressToNextStep() {
        if (currentStep < 5) {
            currentStep++;
            enableCurrentStep();
        } else {
            handleGameWin();
        }
    }
    private void enableCurrentStep() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 2; j++) {
                boolean enabled = (i == currentStep);
                glassPanels[i][j].setEnabled(enabled);
                glassPanels[i][j].setBackground
                (enabled ? new Color(200, 220, 255) : new Color(180, 200, 220));
            }
        }
    }

    private void handleGameWin() {
        gameActive = false;
        JOptionPane.showMessageDialog(frame, "Congratulations! You crossed the bridge safely!");
        setupNewGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new The_Bridge_of_Death());
    }
}