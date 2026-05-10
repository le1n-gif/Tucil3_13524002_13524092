package core;

import java.util.ArrayList;
import java.util.List;

public final class Movement {
    private Movement() {
    }

    public static List<MoveResult> generateNeighbors(Board board, State state) {
        List<MoveResult> neighbors = new ArrayList<MoveResult>();
        for (Direction direction : Direction.values()) {
            MoveResult move = slide(board, state, direction);
            if (move != null) { // Kalau null, berarti tidak boleh (seperti ke lava, ke ujung board tanpa rock,
                                // skip suatu checkpoint)
                neighbors.add(move);
            }
        }
        return neighbors;
    }

    public static MoveResult slide(Board board, State state, Direction direction) {
        Position current = state.getPosition();
        int nextCheckpointIndex = state.getNextCheckpointIndex();
        int movementCost = 0;
        boolean moved = false;
        List<Position> crossedCells = new ArrayList<Position>();

        while (true) {
            Position next = current.translate(direction.getRowDelta(), direction.getColDelta());
            if (!board.isInside(next)) {
                return null;
            }

            char tile = board.getTile(next);
            if (tile == 'X') {
                if (!moved) {
                    return null;
                }
                State resultingState = new State(current, nextCheckpointIndex);
                return new MoveResult(direction, crossedCells, current, movementCost, resultingState);
            }

            if (tile == 'L') {
                return null;
            }

            if (Character.isDigit(tile)) {
                int checkpointIndex = board.getCheckpointIndexForDigit(tile - '0');
                if (checkpointIndex == nextCheckpointIndex) {
                    nextCheckpointIndex++;
                } else if (checkpointIndex > nextCheckpointIndex) {
                    return null;
                }
            }

            movementCost += board.getCost(next);
            crossedCells.add(next);
            current = next;
            moved = true;
        }
    }
}
