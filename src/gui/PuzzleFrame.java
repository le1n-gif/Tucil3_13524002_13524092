package gui;

import core.Board;
import core.HeuristicType;
import core.SearchAlgorithm;
import core.SearchResult;
import core.Solver;
import core.State;
import io.ParseException;
import io.PuzzleParser;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PuzzleFrame extends JFrame {
    private static final String MODE_SOLUTION = "Solution steps";
    private static final String MODE_EXPLORED = "Explored states";

    private final JButton chooseButton;
    private final JButton solveButton;
    private final JLabel fileLabel;
    private final JComboBox<AlgorithmOption> algorithmCombo;
    private final JComboBox<HeuristicOption> heuristicCombo;
    private final BoardPanel boardPanel;
    private final ResultPanel resultPanel;

    private File selectedFile;
    private Board board;
    private SearchResult result;
    private SearchAlgorithm selectedAlgorithm;
    private HeuristicType selectedHeuristic;
    private int currentStep;
    private boolean updatingStep;
    private final Timer playbackTimer;

    public PuzzleFrame() {
        super("Ice Sliding Puzzle Solver");

        chooseButton = new JButton("Choose Input File");
        solveButton = new JButton("Solve");
        fileLabel = new JLabel("No file selected");
        algorithmCombo = new JComboBox<AlgorithmOption>(new AlgorithmOption[] {
                new AlgorithmOption("UCS", SearchAlgorithm.UCS),
                new AlgorithmOption("GBFS", SearchAlgorithm.GBFS),
                new AlgorithmOption("A*", SearchAlgorithm.ASTAR)
        });
        heuristicCombo = new JComboBox<HeuristicOption>(new HeuristicOption[] {
                new HeuristicOption("H1 - Manhattan Next Target", HeuristicType.MANHATTAN_NEXT_TARGET),
                new HeuristicOption("H2 - Manhattan Ordered Chain", HeuristicType.MANHATTAN_ORDERED_CHAIN),
                new HeuristicOption("H3 - Chebyshev Next Target", HeuristicType.CHEBYSHEV_NEXT_TARGET),
                new HeuristicOption("H4 - Euclidean Next Target", HeuristicType.EUCLIDEAN_NEXT_TARGET)
        });
        boardPanel = new BoardPanel();
        resultPanel = new ResultPanel();
        playbackTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                advancePlayback();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(960, 640);
        setLocationRelativeTo(null);

        add(buildTopPanel(), BorderLayout.NORTH);
        add(new JScrollPane(boardPanel), BorderLayout.CENTER);
        add(resultPanel, BorderLayout.EAST);

        registerListeners();
        updateHeuristicEnabled();
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));
        panel.add(chooseButton);
        panel.add(fileLabel);
        panel.add(new JLabel("Algorithm:"));
        panel.add(algorithmCombo);
        panel.add(new JLabel("Heuristic:"));
        panel.add(heuristicCombo);
        panel.add(solveButton);
        return panel;
    }

    private void registerListeners() {
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                chooseInputFile();
            }
        });

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                solvePuzzle();
            }
        });

        algorithmCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                updateHeuristicEnabled();
            }
        });

        resultPanel.getPreviousButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                stopPlayback();
                showStep(currentStep - 1);
            }
        });

        resultPanel.getNextButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                stopPlayback();
                showStep(currentStep + 1);
            }
        });

        resultPanel.getStepSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                if (!updatingStep) {
                    stopPlayback();
                    showStep(resultPanel.getStepSlider().getValue());
                }
            }
        });

        resultPanel.getPlaybackModeCombo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (result != null) {
                    stopPlayback();
                    configureActivePlayback();
                    showStep(0);
                }
            }
        });

        resultPanel.getPlayPauseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                togglePlayback();
            }
        });

        resultPanel.getSpeedCombo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                updatePlaybackDelay();
            }
        });

        resultPanel.getSaveButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                saveResult();
            }
        });
    }

    private void chooseInputFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        if (selectedFile != null) {
            chooser.setCurrentDirectory(selectedFile.getParentFile());
        }

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        selectedFile = chooser.getSelectedFile();
        fileLabel.setText(selectedFile.getAbsolutePath());
        try {
            stopPlayback();
            board = PuzzleParser.parse(selectedFile.toPath());
            result = null;
            boardPanel.setBoard(board);
            boardPanel.setState(new State(board.getStart(), 0));
            resultPanel.setStatus("Input loaded.");
            resultPanel.configurePlaybackModes(false, false);
            resultPanel.configurePlayback(0);
            resultPanel.setStepLabel("Initial");
            resultPanel.getSaveButton().setEnabled(false);
        } catch (IOException exception) {
            clearLoadedBoard("Could not read input file: " + exception.getMessage());
        } catch (ParseException exception) {
            clearLoadedBoard("Invalid input: " + exception.getMessage());
        }
    }

    private void clearLoadedBoard(String message) {
        stopPlayback();
        board = null;
        result = null;
        boardPanel.setBoard(null);
        resultPanel.setError(message);
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private void solvePuzzle() {
        if (board == null) {
            resultPanel.setError("Choose a valid input file first.");
            return;
        }

        selectedAlgorithm = ((AlgorithmOption) algorithmCombo.getSelectedItem()).algorithm;
        selectedHeuristic = selectedAlgorithm == SearchAlgorithm.UCS
                ? HeuristicType.NONE
                : ((HeuristicOption) heuristicCombo.getSelectedItem()).heuristicType;

        setControlsEnabled(false);
        resultPanel.setRunning();
        stopPlayback();

        SwingWorker<SearchResult, Void> worker = new SwingWorker<SearchResult, Void>() {
            @Override
            protected SearchResult doInBackground() {
                return Solver.solve(board, selectedAlgorithm, selectedHeuristic);
            }

            @Override
            protected void done() {
                setControlsEnabled(true);
                try {
                    result = get();
                    showResult();
                } catch (Exception exception) {
                    resultPanel.setError("Solver failed: " + exception.getMessage());
                    JOptionPane.showMessageDialog(
                            PuzzleFrame.this,
                            exception.getMessage(),
                            "Solver Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showResult() {
        resultPanel.setResult(result);
        resultPanel.configurePlaybackModes(result.isFound(), !result.getExploredStates().isEmpty());
        configureActivePlayback();
        showStep(0);
    }

    private void configureActivePlayback() {
        List<State> states = activePlaybackStates();
        resultPanel.configurePlayback(states.size());
        if (states.isEmpty()) {
            resultPanel.setStepLabel("Step: -");
        }
    }

    private void showStep(int step) {
        if (result == null) {
            return;
        }

        List<State> states = activePlaybackStates();
        if (states.isEmpty()) {
            return;
        }

        currentStep = Math.max(0, Math.min(step, states.size() - 1));
        boardPanel.setState(states.get(currentStep));

        updatingStep = true;
        resultPanel.getStepSlider().setValue(currentStep);
        updatingStep = false;

        if (isSolutionPlayback()) {
            if (currentStep == 0) {
                resultPanel.setStepLabel("Initial");
            } else {
                resultPanel.setStepLabel(
                        "Step " + currentStep + ": " + result.getSolutionMoves().get(currentStep - 1));
            }
        } else {
            resultPanel.setStepLabel("Iteration " + (currentStep + 1) + " of " + states.size());
        }

        resultPanel.getPreviousButton().setEnabled(currentStep > 0);
        resultPanel.getNextButton().setEnabled(currentStep < states.size() - 1);
        resultPanel.getPlayPauseButton().setEnabled(states.size() > 1);
    }

    private List<State> activePlaybackStates() {
        if (result == null) {
            return java.util.Collections.<State>emptyList();
        }
        if (isSolutionPlayback()) {
            return result.getSolutionStates();
        }
        return result.getExploredStates();
    }

    private boolean isSolutionPlayback() {
        Object selectedMode = resultPanel.getPlaybackModeCombo().getSelectedItem();
        return MODE_SOLUTION.equals(selectedMode);
    }

    private void togglePlayback() {
        if (playbackTimer.isRunning()) {
            stopPlayback();
        } else {
            List<State> states = activePlaybackStates();
            if (states.size() <= 1) {
                return;
            }
            if (currentStep >= states.size() - 1) {
                showStep(0);
            }
            updatePlaybackDelay();
            playbackTimer.start();
            resultPanel.setPlaying(true);
        }
    }

    private void advancePlayback() {
        List<State> states = activePlaybackStates();
        if (states.isEmpty() || currentStep >= states.size() - 1) {
            stopPlayback();
            return;
        }
        showStep(currentStep + 1);
    }

    private void stopPlayback() {
        if (playbackTimer.isRunning()) {
            playbackTimer.stop();
        }
        resultPanel.setPlaying(false);
    }

    private void updatePlaybackDelay() {
        Object selectedSpeed = resultPanel.getSpeedCombo().getSelectedItem();
        int delay = 500;
        if ("0.5x".equals(selectedSpeed)) {
            delay = 1000;
        } else if ("2x".equals(selectedSpeed)) {
            delay = 250;
        } else if ("5x".equals(selectedSpeed)) {
            delay = 100;
        }
        playbackTimer.setDelay(delay);
    }

    private void saveResult() {
        if (result == null) {
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("solution-result.txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            SolutionSaver.save(
                    chooser.getSelectedFile(),
                    selectedFile,
                    selectedAlgorithm,
                    selectedHeuristic,
                    board,
                    result);
            resultPanel.setStatus("Result saved.");
        } catch (IOException exception) {
            resultPanel.setStatus("Could not save result: " + exception.getMessage());
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateHeuristicEnabled() {
        AlgorithmOption option = (AlgorithmOption) algorithmCombo.getSelectedItem();
        heuristicCombo.setEnabled(option != null && option.algorithm != SearchAlgorithm.UCS);
    }

    private void setControlsEnabled(boolean enabled) {
        chooseButton.setEnabled(enabled);
        solveButton.setEnabled(enabled);
        algorithmCombo.setEnabled(enabled);
        if (enabled) {
            updateHeuristicEnabled();
        } else {
            heuristicCombo.setEnabled(false);
        }
    }

    private static final class AlgorithmOption {
        private final String label;
        private final SearchAlgorithm algorithm;

        private AlgorithmOption(String label, SearchAlgorithm algorithm) {
            this.label = label;
            this.algorithm = algorithm;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static final class HeuristicOption {
        private final String label;
        private final HeuristicType heuristicType;

        private HeuristicOption(String label, HeuristicType heuristicType) {
            this.label = label;
            this.heuristicType = heuristicType;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
