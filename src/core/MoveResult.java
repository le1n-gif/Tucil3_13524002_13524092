package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MoveResult {
    private final Direction direction;
    private final List<Position> crossedCells;
    private final Position stopCell;
    private final int movementCost;
    private final State resultingState;

    public MoveResult(
            Direction direction,
            List<Position> crossedCells,
            Position stopCell,
            int movementCost,
            State resultingState
    ) {
        if (direction == null || crossedCells == null || stopCell == null || resultingState == null) {
            throw new IllegalArgumentException("MoveResult arguments cannot be null");
        }
        this.direction = direction;
        this.crossedCells = Collections.unmodifiableList(new ArrayList<Position>(crossedCells));
        this.stopCell = stopCell;
        this.movementCost = movementCost;
        this.resultingState = resultingState;
    }

    public Direction getDirection() {
        return direction;
    }

    public List<Position> getCrossedCells() {
        return crossedCells;
    }

    public Position getStopCell() {
        return stopCell;
    }

    public int getMovementCost() {
        return movementCost;
    }

    public State getResultingState() {
        return resultingState;
    }
}
