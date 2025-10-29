package cb.bots.fumano.mcts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonteCarloTreeSearch {

    private static final Random random = new Random();

    private Node lastBestChild;

    public MonteCarloTreeSearch() {
        lastBestChild = null;
    }

    public int findBestAction(State state, int iterations) {
        if (state.isTerminal()) {
            return -1;
        }
        Node root = new Node(state);
        root.addChildren();

        for (int i = 0; i < iterations; i++) {
            iterate(root);
        }
        return root.findHighestAvgScoreChild().getAction();
    }

    public int findBestAction(State state, long milliSeconds) {
        if (state.isTerminal()) {
            return -1;
        }
        Node root = null;
        if (lastBestChild != null) {
            if (lastBestChild.getState().equals(state)) {
                root = lastBestChild;
            } else {
                for (Node child: lastBestChild.getChildren()) {
                    if (child.getState().equals(state)) {
                        root = child;
                        break;
                    }
                }
            }
        }
        if (root == null) {
            System.out.println("creating new root");
            root = new Node(state);
        } else {
            System.out.println("using cached root");
            root.removeParent();
        }
        if (root.getChildren() == null) {
            root.addChildren();
        }
        long count = 0;
        long start = System.currentTimeMillis();
        long end = start + milliSeconds;
        long delta = 0;
        while (start + delta < end) {
            for (int i = 0; i < 8000; i++) {
                iterate(root);
                count++;
            }
            delta = System.currentTimeMillis() - start;
            start += delta;
        }
        System.out.printf("delta: %dms, iterations: %d%n",System.currentTimeMillis() - end, count);
        Node bestChild = root.findHighestAvgScoreChild();
        if (bestChild != null) {
            this.lastBestChild = bestChild;
            return bestChild.getAction();
        }
        return -1;
    }

    private void iterate(Node root) {
        List<Integer> actions = new ArrayList<>();
        Node current = root;
        while (!current.isLeaf()) {
            current = current.findHighestUCB1Child();
        }

        if (current.hasBeenVisited()) {
            current.addChildren();

            if (!current.getChildren().isEmpty()) {
                current = current.getChildren().getFirst();
            }
        }

        State currentState = current.getState().copy();

        while (!currentState.isTerminal()) {
            actions.clear();
            currentState.findActions(actions);
            currentState.simulate(actions.get(random.nextInt(actions.size())));
        }
        double score = currentState.getScore();

        while (true) {
            current.increaseScore(score);
            current.increaseVisits();
            if (current == root) {
                break;
            }
            current = current.getParent();
        }
    }
}
