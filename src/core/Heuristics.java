package core;

public final class Heuristics {
    private Heuristics() { // Utility class
    }

    public static int estimate(Board board, State state, HeuristicType heuristicType) {
        if (heuristicType == null || heuristicType == HeuristicType.NONE) {
            return 0;
        }
        if (heuristicType == HeuristicType.NEXT_TARGET) {
            return estimateNextTarget(board, state);
        }
        if (heuristicType == HeuristicType.ORDERED_CHAIN) {
            return estimateOrderedChain(board, state);
        }
        throw new IllegalArgumentException("Unsupported heuristic: " + heuristicType);
    }

    private static int estimateNextTarget(Board board, State state) {
        Position target = nextTarget(board, state);
        return state.getPosition().manhattanDistance(target) * board.getMinTraversableCost();
    }

    private static int estimateOrderedChain(Board board, State state) {
        int distance = 0;
        Position current = state.getPosition();

        for (int index = state.getNextCheckpointIndex(); index < board.getCheckpointCount(); index++) {
            Position checkpoint = board.getCheckpoints().get(index);
            distance += current.manhattanDistance(checkpoint);
            current = checkpoint;
        }

        distance += current.manhattanDistance(board.getGoal());
        return distance * board.getMinTraversableCost();
    }

    private static Position nextTarget(Board board, State state) {
        if (state.getNextCheckpointIndex() < board.getCheckpointCount()) {
            return board.getCheckpoints().get(state.getNextCheckpointIndex());
        }
        return board.getGoal();
    }
}
