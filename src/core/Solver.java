package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public final class Solver {
    private Solver() { // Utility class
    }

    public static SearchResult solve(Board board, SearchAlgorithm algorithm, HeuristicType heuristicType) {
        if (board == null || algorithm == null) {
            throw new IllegalArgumentException("Board and algorithm cannot be null");
        }

        HeuristicType selectedHeuristic = algorithm == SearchAlgorithm.UCS
                ? HeuristicType.NONE
                : normalizeHeuristic(heuristicType);

        long startTime = System.nanoTime();
        State startState = new State(board.getStart(), 0);
        Node startNode = new Node(
                startState,
                0,
                Heuristics.estimate(board, startState, selectedHeuristic), // Tidak akan menghitung heuristic kalau
                                                                           // selectedHeuristic adalah tipe NONE
                null,
                null,
                0);

        PriorityQueue<Node> frontier = new PriorityQueue<Node>(nodeComparator(algorithm));
        Map<State, Integer> bestCostByState = new HashMap<State, Integer>(); // HashMap dengan key state, sudah ada
                                                                             // override hashCode() di class State
        List<State> exploredStates = new ArrayList<State>();

        frontier.add(startNode);
        bestCostByState.put(startState, 0);

        int iterations = 0;
        long sequence = 1;

        while (!frontier.isEmpty()) {
            Node current = frontier.poll(); // Find best node according to pathfinding algorithm (based off g(n) and
                                            // h(n))
            Integer bestKnownCost = bestCostByState.get(current.state);
            if (bestKnownCost != null && current.costFromStart > bestKnownCost) {
                continue;
            }

            iterations++;
            exploredStates.add(current.state);

            if (board.isGoalState(current.state)) {
                return buildFoundResult(current, iterations, elapsedNanos(startTime), exploredStates);
            }

            List<MoveResult> neighbors = Movement.generateNeighbors(board, current.state);
            for (MoveResult move : neighbors) {
                State nextState = move.getResultingState();
                int nextCost = current.costFromStart + move.getMovementCost(); // Tetap dihitung di GBFS walaupun GBFS
                                                                               // tidak pakai g(n) sbg prioritas karena
                                                                               // sederhana, tidak spt heuristik
                Integer previousBest = bestCostByState.get(nextState);
                if (previousBest != null && nextCost >= previousBest) { // Kalau sudah pernah ke state tsb dengan cost
                                                                        // lebih rendah, skip aja
                    continue;
                }

                bestCostByState.put(nextState, nextCost);
                int heuristic = Heuristics.estimate(board, nextState, selectedHeuristic); // Estimasi heuristic, h(n),
                                                                                          // tidak akan menambah time
                                                                                          // complexity kalau
                                                                                          // selectedHeuristic tipe NONE
                frontier.add(new Node(
                        nextState,
                        nextCost,
                        heuristic,
                        current,
                        move.getDirection(),
                        sequence));
                sequence++;
            }
        }

        return SearchResult.notFoundWithNanos(iterations, elapsedNanos(startTime), exploredStates);
    }

    private static HeuristicType normalizeHeuristic(HeuristicType heuristicType) {
        if (heuristicType == null || heuristicType == HeuristicType.NONE) {
            return HeuristicType.MANHATTAN_NEXT_TARGET;
        }
        return heuristicType;
    }

    private static Comparator<Node> nodeComparator(final SearchAlgorithm algorithm) {
        return new Comparator<Node>() {
            @Override
            public int compare(Node left, Node right) {
                int priorityComparison = Integer.compare(priority(left, algorithm), priority(right, algorithm));
                if (priorityComparison != 0) {
                    return priorityComparison;
                }

                int heuristicComparison = Integer.compare(left.heuristicCost, right.heuristicCost);
                if (heuristicComparison != 0) {
                    return heuristicComparison;
                }

                int costComparison = Integer.compare(left.costFromStart, right.costFromStart);
                if (costComparison != 0) {
                    return costComparison;
                }

                return Long.compare(left.sequence, right.sequence);
            }
        };
    }

    private static int priority(Node node, SearchAlgorithm algorithm) {
        if (algorithm == SearchAlgorithm.UCS) {
            return node.costFromStart;
        }
        if (algorithm == SearchAlgorithm.GBFS) {
            return node.heuristicCost;
        }
        return node.costFromStart + node.heuristicCost; // Else A*
    }

    private static SearchResult buildFoundResult(
            Node goalNode,
            int iterations,
            long executionTimeNanos,
            List<State> exploredStates) {
        LinkedList<State> states = new LinkedList<State>();
        LinkedList<Direction> moves = new LinkedList<Direction>();

        Node current = goalNode;
        while (current != null) {
            states.addFirst(current.state);
            if (current.moveFromParent != null) {
                moves.addFirst(current.moveFromParent);
            }
            current = current.parent;
        }

        StringBuilder moveString = new StringBuilder();
        for (Direction move : moves) {
            moveString.append(move.getSymbol());
        }

        return SearchResult.foundWithNanos(
                moveString.toString(),
                goalNode.costFromStart,
                iterations,
                executionTimeNanos,
                states,
                moves,
                Collections.unmodifiableList(new ArrayList<State>(exploredStates)));
    }

    private static long elapsedNanos(long startTimeNanos) {
        return System.nanoTime() - startTimeNanos;
    }

    private static final class Node {
        private final State state;
        private final int costFromStart;
        private final int heuristicCost;
        private final Node parent;
        private final Direction moveFromParent;
        private final long sequence;

        private Node(
                State state,
                int costFromStart,
                int heuristicCost,
                Node parent,
                Direction moveFromParent,
                long sequence) {
            this.state = state;
            this.costFromStart = costFromStart;
            this.heuristicCost = heuristicCost;
            this.parent = parent;
            this.moveFromParent = moveFromParent;
            this.sequence = sequence;
        }
    }
}
