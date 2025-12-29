package sandbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CountdownGUI {
    private JFrame frame;
    private JLabel timeLabel;
    private Timer timer;
    private int secondsRemaining;
    
    public CountdownGUI() {
        frame = new JFrame("Aftelklok");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());
        
        timeLabel = new JLabel("00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        
        JPanel inputPanel = new JPanel();
        JTextField minuteField = new JTextField(3);
        JTextField secondField = new JTextField(3);
        JButton startButton = new JButton("Start");
        
        inputPanel.add(new JLabel("Minuten:"));
        inputPanel.add(minuteField);
        inputPanel.add(new JLabel("Seconden:"));
        inputPanel.add(secondField);
        inputPanel.add(startButton);
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int minutes = Integer.parseInt(minuteField.getText());
                    int seconds = Integer.parseInt(secondField.getText());
                    secondsRemaining = minutes * 60 + seconds;
                    
                    if (timer != null && timer.isRunning()) {
                        timer.stop();
                    }
                    
                    timer = new Timer(1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (secondsRemaining <= 0) {
                                timer.stop();
                                timeLabel.setText("Klaar!");
                                JOptionPane.showMessageDialog(frame, "Tijd is om!");
                            } else {
                                int min = secondsRemaining / 60;
                                int sec = secondsRemaining % 60;
                                timeLabel.setText(String.format("%02d:%02d", min, sec));
                                secondsRemaining--;
                            }
                        }
                    });
                    
                    timer.start();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Voer geldige getallen in!");
                }
            }
        });
        
        frame.add(timeLabel, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CountdownGUI();
            }
        });
    }
}
