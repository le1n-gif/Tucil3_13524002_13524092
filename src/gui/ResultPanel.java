package gui;

import core.SearchResult;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ResultPanel extends JPanel {
    private final JTextArea summaryArea;
    private final JLabel stepLabel;
    private final JLabel statusLabel;
    private final JButton previousButton;
    private final JButton nextButton;
    private final JButton playPauseButton;
    private final JButton saveButton;
    private final JSlider stepSlider;
    private final JComboBox<String> playbackModeCombo;
    private final JComboBox<String> speedCombo;

    public ResultPanel() {
        super(new BorderLayout(8, 8));

        summaryArea = new JTextArea(8, 28);
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);

        stepLabel = new JLabel("Step: -");
        statusLabel = new JLabel("Ready.");
        previousButton = new JButton("Previous");
        nextButton = new JButton("Next");
        playPauseButton = new JButton("Play");
        saveButton = new JButton("Save Result");
        stepSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 0, 0);
        playbackModeCombo = new JComboBox<String>();
        speedCombo = new JComboBox<String>(new String[] { "0.5x", "1x", "2x", "5x" });
        speedCombo.setSelectedItem("1x");

        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        controls.add(playbackModeCombo, constraints);

        constraints.gridy = 1;
        controls.add(stepLabel, constraints);

        constraints.gridy = 2;
        constraints.weightx = 1.0;
        controls.add(stepSlider, constraints);

        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weightx = 0.0;
        controls.add(previousButton, constraints);

        constraints.gridx = 1;
        controls.add(nextButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        controls.add(playPauseButton, constraints);

        constraints.gridx = 1;
        controls.add(speedCombo, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        controls.add(saveButton, constraints);

        constraints.gridy = 6;
        controls.add(statusLabel, constraints);

        add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);

        setPlaybackEnabled(false);
        saveButton.setEnabled(false);
    }

    public JButton getPreviousButton() {
        return previousButton;
    }

    public JButton getNextButton() {
        return nextButton;
    }

    public JButton getPlayPauseButton() {
        return playPauseButton;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JSlider getStepSlider() {
        return stepSlider;
    }

    public JComboBox<String> getPlaybackModeCombo() {
        return playbackModeCombo;
    }

    public JComboBox<String> getSpeedCombo() {
        return speedCombo;
    }

    public void setRunning() {
        summaryArea.setText("Solving...");
        statusLabel.setText("Running solver...");
        setPlaybackEnabled(false);
        saveButton.setEnabled(false);
    }

    public void setResult(SearchResult result) {
        StringBuilder builder = new StringBuilder();
        if (result.isFound()) {
            builder.append("Solution found.").append(System.lineSeparator());
            builder.append("Move string: ").append(result.getMoveString()).append(System.lineSeparator());
            builder.append("Total cost: ").append(result.getTotalCost()).append(System.lineSeparator());
        } else {
            builder.append("No solution found.").append(System.lineSeparator());
        }
        builder.append("Iterations: ").append(result.getIterations()).append(System.lineSeparator());
        builder.append("Explored states: ").append(result.getExploredStates().size()).append(System.lineSeparator());
        builder.append("Execution time: ").append(formatMillis(result)).append(" ms");

        summaryArea.setText(builder.toString());
        summaryArea.setCaretPosition(0);
        saveButton.setEnabled(true);
        statusLabel.setText("Done.");
    }

    public void setError(String message) {
        summaryArea.setText("");
        statusLabel.setText(message);
        setPlaybackEnabled(false);
        saveButton.setEnabled(false);
    }

    public void configurePlayback(int stepCount) {
        boolean enabled = stepCount > 0;
        stepSlider.setMinimum(0);
        stepSlider.setMaximum(Math.max(0, stepCount - 1));
        stepSlider.setValue(0);
        setPlaybackEnabled(enabled);
    }

    public void configurePlaybackModes(boolean solutionAvailable, boolean exploredAvailable) {
        playbackModeCombo.removeAllItems();
        if (solutionAvailable) {
            playbackModeCombo.addItem("Solution steps");
        }
        if (exploredAvailable) {
            playbackModeCombo.addItem("Explored states");
        }
        playbackModeCombo.setEnabled(playbackModeCombo.getItemCount() > 0);
    }

    public void setStepLabel(String text) {
        stepLabel.setText(text);
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    public void setPlaying(boolean playing) {
        playPauseButton.setText(playing ? "Pause" : "Play");
    }

    public void setPlaybackEnabled(boolean enabled) {
        previousButton.setEnabled(enabled);
        nextButton.setEnabled(enabled);
        stepSlider.setEnabled(enabled);
        playPauseButton.setEnabled(enabled);
        speedCombo.setEnabled(enabled);
        playbackModeCombo.setEnabled(enabled && playbackModeCombo.getItemCount() > 0);
    }

    private static String formatMillis(SearchResult result) {
        return String.format(java.util.Locale.US, "%.3f", result.getExecutionTimeMillisDecimal());
    }
}
