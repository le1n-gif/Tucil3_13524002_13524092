package core;

public final class Heuristics {
    private Heuristics() { // Utility class
    }

    public static int estimate(Board board, State state, HeuristicType heuristicType) {
        if (heuristicType == null || heuristicType == HeuristicType.NONE) {
            return 0;
        }
        if (heuristicType == HeuristicType.MANHATTAN_NEXT_TARGET) {
            return estimateManhattanNextTarget(board, state);
        }
        if (heuristicType == HeuristicType.MANHATTAN_ORDERED_CHAIN) {
            return estimateManhattanOrderedChain(board, state);
        }
        if (heuristicType == HeuristicType.CHEBYSHEV_NEXT_TARGET) {
            return estimateChebyshevNextTarget(board, state);
        }
        if (heuristicType == HeuristicType.EUCLIDEAN_NEXT_TARGET) {
            return estimateEuclideanNextTarget(board, state);
        }
        throw new IllegalArgumentException("Unsupported heuristic: " + heuristicType);
    }

    private static int estimateManhattanNextTarget(Board board, State state) {
        Position target = nextTarget(board, state);
        return state.getPosition().manhattanDistance(target) * board.getMinTraversableCost();
    }

    private static int estimateManhattanOrderedChain(Board board, State state) {
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

    private static int estimateChebyshevNextTarget(Board board, State state) {
        Position target = nextTarget(board, state);
        int rowDiff = Math.abs(state.getPosition().getRow() - target.getRow());
        int colDiff = Math.abs(state.getPosition().getCol() - target.getCol());
        return Math.max(rowDiff, colDiff) * board.getMinTraversableCost();
    }

    private static int estimateEuclideanNextTarget(Board board, State state) {
        Position target = nextTarget(board, state);
        int rowDiff = state.getPosition().getRow() - target.getRow();
        int colDiff = state.getPosition().getCol() - target.getCol();
        double distance = Math.sqrt(rowDiff * rowDiff + colDiff * colDiff);
        return (int) Math.floor(distance * board.getMinTraversableCost());
    }

    private static Position nextTarget(Board board, State state) {
        if (state.getNextCheckpointIndex() < board.getCheckpointCount()) {
            return board.getCheckpoints().get(state.getNextCheckpointIndex());
        }
        return board.getGoal();
    }
}
