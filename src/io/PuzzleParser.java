package io;

import core.Board;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class PuzzleParser {
    private static final String VALID_SYMBOLS = "*XLZO0123456789";

    private PuzzleParser() {
    }

    public static Board parse(Path path) throws IOException, ParseException {
        if (path == null) {
            throw new ParseException("Input file is not selected.");
        }

        List<String> lines = new ArrayList<String>(Files.readAllLines(path, StandardCharsets.UTF_8));
        removeTrailingBlankLines(lines);
        if (lines.isEmpty()) {
            throw new ParseException("Input file is empty.");
        }

        String firstLine = stripBom(lines.get(0)).trim();
        String[] dimensions = firstLine.isEmpty() ? new String[0] : firstLine.split("\\s+");
        if (dimensions.length != 2) {
            throw new ParseException("First line must contain exactly two positive integers: N M.");
        }

        int rows = parsePositiveInteger(dimensions[0], "N");
        int cols = parsePositiveInteger(dimensions[1], "M");
        int expectedLines = 1 + rows + rows;
        if (lines.size() != expectedLines) {
            throw new ParseException(
                    "Expected " + expectedLines + " lines: first line, " + rows
                            + " board rows, and " + rows + " cost rows.");
        }

        char[][] tiles = parseTiles(lines, rows, cols);
        int[][] costs = parseCosts(lines, rows, cols);
        validateStartGoalAndCheckpoints(tiles);

        try {
            return new Board(tiles, costs);
        } catch (IllegalArgumentException exception) {
            throw new ParseException(exception.getMessage());
        }
    }

    private static char[][] parseTiles(List<String> lines, int rows, int cols) throws ParseException {
        char[][] tiles = new char[rows][cols];
        for (int row = 0; row < rows; row++) {
            String line = lines.get(row + 1).trim();
            if (line.length() != cols) {
                throw new ParseException("Board row " + (row + 1) + " must have length " + cols + ".");
            }

            for (int col = 0; col < cols; col++) {
                char symbol = line.charAt(col);
                if (VALID_SYMBOLS.indexOf(symbol) < 0) {
                    throw new ParseException(
                            "Invalid board symbol '" + symbol + "' at row " + (row + 1)
                                    + ", column " + (col + 1) + ".");
                }
                tiles[row][col] = symbol;
            }
        }
        return tiles;
    }

    private static int[][] parseCosts(List<String> lines, int rows, int cols) throws ParseException {
        int[][] costs = new int[rows][cols];
        int firstCostLine = 1 + rows;
        for (int row = 0; row < rows; row++) {
            String line = lines.get(firstCostLine + row).trim();
            String[] values = line.isEmpty() ? new String[0] : line.split("\\s+");
            if (values.length != cols) {
                throw new ParseException("Cost row " + (row + 1) + " must contain exactly " + cols + " integers.");
            }

            for (int col = 0; col < cols; col++) {
                costs[row][col] = parseNonNegativeInteger(values[col], row, col);
            }
        }
        return costs;
    }

    private static void validateStartGoalAndCheckpoints(char[][] tiles) throws ParseException {
        int starts = 0;
        int goals = 0;
        boolean[] checkpoints = new boolean[10];

        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
                char tile = tiles[row][col];
                if (tile == 'Z') {
                    starts++;
                } else if (tile == 'O') {
                    goals++;
                } else if (Character.isDigit(tile)) {
                    checkpoints[tile - '0'] = true;
                }
            }
        }

        if (starts != 1) {
            throw new ParseException("Board must contain exactly one start tile Z.");
        }
        if (goals != 1) {
            throw new ParseException("Board must contain exactly one goal tile O.");
        }

        boolean missingEarlierDigit = false;
        for (int digit = 0; digit < checkpoints.length; digit++) {
            if (checkpoints[digit]) {
                if (missingEarlierDigit) {
                    throw new ParseException("Checkpoint digits must be contiguous starting from 0.");
                }
            } else {
                missingEarlierDigit = true;
            }
        }
    }

    private static int parsePositiveInteger(String value, String name) throws ParseException {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                throw new ParseException(name + " must be a positive integer.");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new ParseException(name + " must be a positive integer.");
        }
    }

    private static int parseNonNegativeInteger(String value, int row, int col) throws ParseException {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 0) {
                throw new ParseException(
                        "Cost at row " + (row + 1) + ", column " + (col + 1)
                                + " must be non-negative.");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new ParseException(
                    "Cost at row " + (row + 1) + ", column " + (col + 1)
                            + " must be a non-negative integer.");
        }
    }

    private static void removeTrailingBlankLines(List<String> lines) {
        while (!lines.isEmpty() && lines.get(lines.size() - 1).trim().isEmpty()) {
            lines.remove(lines.size() - 1);
        }
    }

    private static String stripBom(String value) {
        if (!value.isEmpty() && value.charAt(0) == '\uFEFF') {
            return value.substring(1);
        }
        return value;
    }
}
