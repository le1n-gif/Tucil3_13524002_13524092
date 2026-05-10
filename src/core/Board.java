package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Board {
    private final int rows;
    private final int cols;
    private final char[][] tiles;
    private final int[][] costs;
    private final Position start;
    private final Position goal;
    private final List<Position> checkpoints;
    private final List<Integer> checkpointDigits;
    private final Map<Integer, Integer> digitToCheckpointIndex;
    private final int minTraversableCost;

    public Board(char[][] tiles, int[][] costs) {
        validateDimensions(tiles, costs);
        this.rows = tiles.length;
        this.cols = tiles[0].length;
        this.tiles = copyTiles(tiles);
        this.costs = copyCosts(costs);

        Position foundStart = null;
        Position foundGoal = null;
        Map<Integer, Position> checkpointsByDigit = new HashMap<Integer, Position>();
        int minCost = Integer.MAX_VALUE;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                char tile = this.tiles[row][col];
                validateTile(tile, row, col);
                if (this.costs[row][col] < 0) {
                    throw new IllegalArgumentException("Cost cannot be negative at (" + row + ", " + col + ")");
                }

                Position position = new Position(row, col);
                if (tile == 'Z') {
                    if (foundStart != null) {
                        throw new IllegalArgumentException("Board must contain exactly one start tile Z");
                    }
                    foundStart = position;
                } else if (tile == 'O') {
                    if (foundGoal != null) {
                        throw new IllegalArgumentException("Board must contain exactly one goal tile O");
                    }
                    foundGoal = position;
                } else if (Character.isDigit(tile)) {
                    int digit = tile - '0';
                    if (checkpointsByDigit.containsKey(digit)) {
                        throw new IllegalArgumentException("Duplicate checkpoint digit " + digit);
                    }
                    checkpointsByDigit.put(digit, position);
                }

                if (tile != 'X' && tile != 'L') {
                    minCost = Math.min(minCost, this.costs[row][col]);
                }
            }
        }

        if (foundStart == null) {
            throw new IllegalArgumentException("Board must contain exactly one start tile Z");
        }
        if (foundGoal == null) {
            throw new IllegalArgumentException("Board must contain exactly one goal tile O");
        }

        this.start = foundStart;
        this.goal = foundGoal;
        this.checkpointDigits = buildCheckpointDigits(checkpointsByDigit);
        this.checkpoints = buildCheckpointPositions(checkpointsByDigit, checkpointDigits);
        this.digitToCheckpointIndex = buildDigitIndex(checkpointDigits);
        this.minTraversableCost = minCost == Integer.MAX_VALUE ? 0 : minCost;
    }

    private static void validateDimensions(char[][] tiles, int[][] costs) {
        if (tiles == null || costs == null) {
            throw new IllegalArgumentException("Tiles and costs cannot be null");
        }
        if (tiles.length == 0 || costs.length == 0) {
            throw new IllegalArgumentException("Board must have at least one row");
        }
        if (tiles.length != costs.length) {
            throw new IllegalArgumentException("Tile rows and cost rows must match");
        }
        if (tiles[0] == null || tiles[0].length == 0) {
            throw new IllegalArgumentException("Board must have at least one column");
        }
        int cols = tiles[0].length;
        for (int row = 0; row < tiles.length; row++) {
            if (tiles[row] == null || costs[row] == null) {
                throw new IllegalArgumentException("Board rows cannot be null");
            }
            if (tiles[row].length != cols || costs[row].length != cols) {
                throw new IllegalArgumentException("Every board and cost row must have the same width");
            }
        }
    }

    private static char[][] copyTiles(char[][] source) {
        char[][] copy = new char[source.length][];
        for (int row = 0; row < source.length; row++) {
            copy[row] = Arrays.copyOf(source[row], source[row].length);
        }
        return copy;
    }

    private static int[][] copyCosts(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int row = 0; row < source.length; row++) {
            copy[row] = Arrays.copyOf(source[row], source[row].length);
        }
        return copy;
    }

    private static void validateTile(char tile, int row, int col) {
        boolean valid = tile == '*'
                || tile == 'X'
                || tile == 'L'
                || tile == 'Z'
                || tile == 'O'
                || Character.isDigit(tile);
        if (!valid) {
            throw new IllegalArgumentException("Invalid tile '" + tile + "' at (" + row + ", " + col + ")");
        }
    }

    private static List<Integer> buildCheckpointDigits(Map<Integer, Position> checkpointsByDigit) {
        List<Integer> digits = new ArrayList<Integer>(checkpointsByDigit.keySet());
        Collections.sort(digits);
        return Collections.unmodifiableList(digits);
    }

    private static List<Position> buildCheckpointPositions(
            Map<Integer, Position> checkpointsByDigit,
            List<Integer> checkpointDigits
    ) {
        List<Position> ordered = new ArrayList<Position>();
        for (Integer digit : checkpointDigits) {
            ordered.add(checkpointsByDigit.get(digit));
        }
        return Collections.unmodifiableList(ordered);
    }

    private static Map<Integer, Integer> buildDigitIndex(List<Integer> checkpointDigits) {
        Map<Integer, Integer> indexByDigit = new HashMap<Integer, Integer>();
        for (int index = 0; index < checkpointDigits.size(); index++) {
            indexByDigit.put(checkpointDigits.get(index), index);
        }
        return Collections.unmodifiableMap(indexByDigit);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Position getStart() {
        return start;
    }

    public Position getGoal() {
        return goal;
    }

    public List<Position> getCheckpoints() {
        return checkpoints;
    }

    public List<Integer> getCheckpointDigits() {
        return checkpointDigits;
    }

    public int getCheckpointCount() {
        return checkpoints.size();
    }

    public int getMinTraversableCost() {
        return minTraversableCost;
    }

    public char getTile(Position position) {
        return tiles[position.getRow()][position.getCol()];
    }

    public int getCost(Position position) {
        return costs[position.getRow()][position.getCol()];
    }

    public boolean isInside(Position position) {
        return position.getRow() >= 0
                && position.getRow() < rows
                && position.getCol() >= 0
                && position.getCol() < cols;
    }

    public boolean isGoalState(State state) {
        return goal.equals(state.getPosition())
                && state.getNextCheckpointIndex() == checkpoints.size();
    }

    public int getCheckpointIndexForDigit(int digit) {
        Integer index = digitToCheckpointIndex.get(digit);
        return index == null ? -1 : index;
    }

    public char[][] copyTileGrid() {
        return copyTiles(tiles);
    }

    public int[][] copyCostGrid() {
        return copyCosts(costs);
    }
}
