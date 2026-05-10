package core;

import java.util.Objects;

public final class State {
    private final Position position;
    private final int nextCheckpointIndex;

    public State(Position position, int nextCheckpointIndex) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        if (nextCheckpointIndex < 0) {
            throw new IllegalArgumentException("Next checkpoint index cannot be negative");
        }
        this.position = position;
        this.nextCheckpointIndex = nextCheckpointIndex;
    }

    public Position getPosition() {
        return position;
    }

    public int getNextCheckpointIndex() {
        return nextCheckpointIndex;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof State)) {
            return false;
        }
        State state = (State) other;
        return nextCheckpointIndex == state.nextCheckpointIndex
                && position.equals(state.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, nextCheckpointIndex);
    }

    @Override
    public String toString() {
        return "State{position=" + position + ", nextCheckpointIndex=" + nextCheckpointIndex + "}";
    }
}
