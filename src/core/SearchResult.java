package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SearchResult {
    private final boolean found;
    private final String moveString;
    private final int totalCost;
    private final int iterations;
    private final long executionTimeNanos;
    private final List<State> solutionStates;
    private final List<Direction> solutionMoves;
    private final List<State> exploredStates;

    private SearchResult(
            boolean found,
            String moveString,
            int totalCost,
            int iterations,
            long executionTimeNanos,
            List<State> solutionStates,
            List<Direction> solutionMoves,
            List<State> exploredStates
    ) {
        this.found = found;
        this.moveString = moveString;
        this.totalCost = totalCost;
        this.iterations = iterations;
        this.executionTimeNanos = executionTimeNanos;
        this.solutionStates = immutableCopy(solutionStates);
        this.solutionMoves = immutableCopy(solutionMoves);
        this.exploredStates = immutableCopy(exploredStates);
    }

    public static SearchResult found(
            String moveString,
            int totalCost,
            int iterations,
            long executionTimeMillis,
            List<State> solutionStates,
            List<Direction> solutionMoves,
            List<State> exploredStates
    ) {
        return new SearchResult(
                true,
                moveString,
                totalCost,
                iterations,
                executionTimeMillis * 1_000_000L,
                solutionStates,
                solutionMoves,
                exploredStates
        );
    }

    public static SearchResult foundWithNanos(
            String moveString,
            int totalCost,
            int iterations,
            long executionTimeNanos,
            List<State> solutionStates,
            List<Direction> solutionMoves,
            List<State> exploredStates
    ) {
        return new SearchResult(
                true,
                moveString,
                totalCost,
                iterations,
                executionTimeNanos,
                solutionStates,
                solutionMoves,
                exploredStates
        );
    }

    public static SearchResult notFound(int iterations, long executionTimeMillis, List<State> exploredStates) {
        return new SearchResult(
                false,
                "",
                -1,
                iterations,
                executionTimeMillis * 1_000_000L,
                Collections.<State>emptyList(),
                Collections.<Direction>emptyList(),
                exploredStates
        );
    }

    public static SearchResult notFoundWithNanos(int iterations, long executionTimeNanos, List<State> exploredStates) {
        return new SearchResult(
                false,
                "",
                -1,
                iterations,
                executionTimeNanos,
                Collections.<State>emptyList(),
                Collections.<Direction>emptyList(),
                exploredStates
        );
    }

    private static <T> List<T> immutableCopy(List<T> values) {
        return Collections.unmodifiableList(new ArrayList<T>(values));
    }

    public boolean isFound() {
        return found;
    }

    public String getMoveString() {
        return moveString;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getIterations() {
        return iterations;
    }

    public long getExecutionTimeMillis() {
        return executionTimeNanos / 1_000_000L;
    }

    public double getExecutionTimeMillisDecimal() {
        return executionTimeNanos / 1_000_000.0;
    }

    public List<State> getSolutionStates() {
        return solutionStates;
    }

    public List<Direction> getSolutionMoves() {
        return solutionMoves;
    }

    public List<State> getExploredStates() {
        return exploredStates;
    }
}
