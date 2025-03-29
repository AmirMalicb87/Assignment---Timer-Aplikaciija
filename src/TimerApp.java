import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class TimerApp {
    private JFrame settingsFrame;
    private JFrame blinkFrame;
    private JTextField timeField;
    private JTextField countdownField;
    private JRadioButton onTimeRadio;
    private JRadioButton countdownRadio;
    private JButton colorButton;
    private JLabel colorLabel;
    private JComboBox<String> speedCombo;
    private JButton startButton;
    private JButton stopButton;
    private Timer blinkTimer;
    private Timer countdownTimer;
    private Color selectedColor = Color.RED;
    private boolean isWhite = true;

    public TimerApp() {
        JDialog initialDialog = new JDialog();
        initialDialog.setTitle("Timer Application");
        initialDialog.setModal(true);
        initialDialog.setLayout(new FlowLayout());

        JButton settingsButton = new JButton("Settings");
        JButton closeButton = new JButton("Close");

        settingsButton.addActionListener(e -> {
            initialDialog.dispose();
            createSettingsWindow();
        });
        closeButton.addActionListener(e -> System.exit(0));

        initialDialog.add(settingsButton);
        initialDialog.add(closeButton);
        initialDialog.pack();
        initialDialog.setLocationRelativeTo(null);
        initialDialog.setVisible(true);
    }

    private void createSettingsWindow() {
        settingsFrame = new JFrame("Timer Settings");
        settingsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        settingsFrame.setLayout(new GridLayout(7,2,10,10));

        onTimeRadio = new JRadioButton("On Time (HH:mm:ss)");
        timeField = new JTextField("00:00:00");

        countdownRadio = new JRadioButton("Countdown (seconds)");
        countdownField = new JTextField("0");

        ButtonGroup timeGroup = new ButtonGroup();
        timeGroup.add(onTimeRadio);
        timeGroup.add(countdownRadio);
        onTimeRadio.setSelected(true);

        colorButton = new JButton("Choose Color");
        colorLabel = new JLabel("RGB: 255, 0, 0");
        colorLabel.setForeground(selectedColor);

        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(settingsFrame, "Choose Color", selectedColor);
            if (newColor != null) {
                selectedColor = newColor;
                colorLabel.setText(String.format("RGB: %d, %d, %d",
                        newColor.getRed(), newColor.getGreen(), newColor.getBlue()));
                colorLabel.setForeground(newColor);
            }
        });
        String[] speeds = {"1000 ms", "2000 ms", "3000 ms", "4000 ms", "5000 ms"};
        speedCombo = new JComboBox<>(speeds);

        startButton = new JButton("Start Countdown");
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> startTimer());
        stopButton.addActionListener(e -> stopTimer());

        settingsFrame.add(onTimeRadio);
        settingsFrame.add(timeField);
        settingsFrame.add(countdownRadio);
        settingsFrame.add(countdownField);
        settingsFrame.add(new JLabel("Blink Color"));
        settingsFrame.add(colorButton);
        settingsFrame.add(new JLabel("Selected Color"));
        settingsFrame.add(colorLabel);
        settingsFrame.add(new JLabel("Blink speed:"));
        settingsFrame.add(speedCombo);
        settingsFrame.add(startButton);
        settingsFrame.add(stopButton);

        settingsFrame.pack();
        settingsFrame.setLocationRelativeTo(null);
        settingsFrame.setVisible(true);
    }
    private void startTimer() {
        setControlsEnabled(false);

        if (onTimeRadio.isSelected()) {
            try {
                String time1 = timeField.getText();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime targetTime = LocalTime.parse(time1, formatter);
                LocalTime now = LocalTime.now();

                long delay = Duration.between(now, targetTime).toMillis();

                if (delay < 0) {
                    delay += Duration.ofDays(1).toMillis();
                }

                countdownTimer = new Timer((int)delay, e -> showBlinkWindow());
                countdownTimer.setRepeats(false);
                countdownTimer.start();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(settingsFrame, "Invalid time format (use HH:mm:ss)");
                setControlsEnabled(true);
            }
        } else {
            try {
                int seconds = Integer.parseInt(countdownField.getText());
                if (seconds < 0) throw new NumberFormatException();

                countdownTimer = new Timer(seconds * 1000, e -> showBlinkWindow());
                countdownTimer.setRepeats(false);
                countdownTimer.start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(settingsFrame, "Please enter a valid non-negative number");
                setControlsEnabled(true);
            }
        }
    }
    private void showBlinkWindow() {
        blinkFrame = new JFrame("Timer Alert");
        blinkFrame.setSize(300, 200);
        blinkFrame.setLocationRelativeTo(null);

        int speed = Integer.parseInt(Objects.requireNonNull(speedCombo.getSelectedItem()).toString().split(" ")[0]);
        blinkTimer = new Timer(speed, e -> {
            blinkFrame.getContentPane().setBackground(isWhite ? Color.WHITE : selectedColor);
            isWhite = !isWhite;
        });
        blinkTimer.start();

        blinkFrame.setVisible(true);
    }
    private void stopTimer() {
        if (countdownTimer != null) countdownTimer.stop();
        if (blinkTimer != null) blinkTimer.stop();
        if (blinkFrame != null) blinkFrame.dispose();
        setControlsEnabled(true);
    }
    private void setControlsEnabled(boolean enabled) {
        onTimeRadio.setEnabled(enabled);
        countdownRadio.setEnabled(enabled);
        timeField.setEnabled(enabled);
        countdownField.setEnabled(enabled);
        colorButton.setEnabled(enabled);
        speedCombo.setEnabled(enabled);
        startButton.setEnabled(enabled);
        stopButton.setEnabled(!enabled);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TimerApp::new);
    }
}
