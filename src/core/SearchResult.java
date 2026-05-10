package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SearchResult {
    private final boolean found;
    private final String moveString;
    private final int totalCost;
    private final int iterations;
    private final long executionTimeMillis;
    private final List<State> solutionStates;
    private final List<Direction> solutionMoves;
    private final List<State> exploredStates;

    private SearchResult(
            boolean found,
            String moveString,
            int totalCost,
            int iterations,
            long executionTimeMillis,
            List<State> solutionStates,
            List<Direction> solutionMoves,
            List<State> exploredStates
    ) {
        this.found = found;
        this.moveString = moveString;
        this.totalCost = totalCost;
        this.iterations = iterations;
        this.executionTimeMillis = executionTimeMillis;
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
                executionTimeMillis,
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
                executionTimeMillis,
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
        return executionTimeMillis;
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
