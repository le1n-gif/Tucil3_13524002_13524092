package gui;

import core.Board;
import core.Direction;
import core.HeuristicType;
import core.Position;
import core.SearchAlgorithm;
import core.SearchResult;
import core.State;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public final class SolutionSaver {
    private SolutionSaver() {
    }

    public static void save(
            File outputFile,
            File inputFile,
            SearchAlgorithm algorithm,
            HeuristicType heuristicType,
            Board board,
            SearchResult result
    ) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        try {
            writer.write("Input file: " + (inputFile == null ? "-" : inputFile.getAbsolutePath()));
            writer.newLine();
            writer.write("Algorithm: " + algorithm);
            writer.newLine();
            if (algorithm != SearchAlgorithm.UCS) {
                writer.write("Heuristic: " + heuristicType);
                writer.newLine();
            }
            writer.write("Found: " + (result.isFound() ? "yes" : "no"));
            writer.newLine();
            writer.write("Move string: " + result.getMoveString());
            writer.newLine();
            writer.write("Total cost: " + result.getTotalCost());
            writer.newLine();
            writer.write("Iterations: " + result.getIterations());
            writer.newLine();
            writer.write("Execution time: " + result.getExecutionTimeMillis() + " ms");
            writer.newLine();

            writer.newLine();
            if (result.isFound()) {
                writer.write("Solution steps:");
                writer.newLine();
                writeSteps(writer, board, result.getSolutionStates(), result.getSolutionMoves());
            } else {
                writer.write("No solution found.");
                writer.newLine();
            }

            writer.newLine();
            writer.write("Explored states:");
            writer.newLine();
            writeExploredStates(writer, board, result.getExploredStates());
        } finally {
            writer.close();
        }
    }

    private static void writeSteps(
            BufferedWriter writer,
            Board board,
            List<State> states,
            List<Direction> moves
    ) throws IOException {
        for (int index = 0; index < states.size(); index++) {
            if (index == 0) {
                writer.write("Initial");
            } else {
                writer.write("Step " + index + ": " + moves.get(index - 1));
            }
            writer.newLine();

            char[][] grid = renderGrid(board, states.get(index));
            for (int row = 0; row < grid.length; row++) {
                writer.write(new String(grid[row]));
                writer.newLine();
            }
            writer.newLine();
        }
    }

    private static void writeExploredStates(
            BufferedWriter writer,
            Board board,
            List<State> states
    ) throws IOException {
        for (int index = 0; index < states.size(); index++) {
            writer.write("Iteration " + (index + 1));
            writer.newLine();

            char[][] grid = renderGrid(board, states.get(index));
            for (int row = 0; row < grid.length; row++) {
                writer.write(new String(grid[row]));
                writer.newLine();
            }
            writer.newLine();
        }
    }

    private static char[][] renderGrid(Board board, State state) {
        char[][] grid = board.copyTileGrid();
        Position start = board.getStart();
        Position actor = state.getPosition();
        if (!start.equals(actor)) {
            grid[start.getRow()][start.getCol()] = '*';
        }
        grid[actor.getRow()][actor.getCol()] = 'Z';
        return grid;
    }
}
