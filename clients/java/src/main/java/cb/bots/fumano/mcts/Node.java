package cb.bots.fumano.mcts;

import java.util.ArrayList;
import java.util.List;


public class Node {

    private double totalScore;
    private int visits;

    private final State state;
    private final int action;
    private Node parent;
    private List<Node> children;

    public Node(State state, int action, Node parent) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.totalScore = 0;
        this.visits = 0;
        this.children = null;
    }

    public Node(State state) {
        this(state, -1, null);
    }

    private double getRelativeTotalScore() {
        if (state.isMaximising()) {
            return totalScore;
        }
        return -totalScore;
    }

    public List<Node> getChildren() {
        return children;
    }

    public State getState() {
        return state;
    }

    public int getAction() {
        return action;
    }

    public Node getParent() {
        return parent;
    }

    public void removeParent() {
        this.parent = null;
    }

    public void increaseScore(double value) {
        this.totalScore += value;
    }

    public void increaseVisits() {
        this.visits++;
    }

    public boolean hasBeenVisited() {
        return visits > 0;
    }

    public void addChildren() {
        List<Integer> actions = new ArrayList<>();
        state.findActions(actions);
        this.children = new ArrayList<>(actions.size());
        for (int action: actions) {
            State childState = this.state.copy();
            childState.simulate(action);
            children.add(new Node(childState, action, this));
        }
    }

    public boolean isLeaf() {
        return this.children == null || this.children.isEmpty();
    }

    public double calcUCB1() {
        if (visits == 0) {
            return Double.MAX_VALUE;
        }
        return getRelativeTotalScore() / visits + 2 * Math.sqrt(Math.log(parent.visits) / visits);
    }

    public double calcAvgScore() {
        if (visits == 0) {
            return 0;
        }
        return getRelativeTotalScore() / visits;
    }

    public Node findHighestUCB1Child() {
        Node best = children.getFirst();
        double bestValue = best.calcUCB1();

        for (int i = 1; i < children.size(); i++) {
            double value = children.get(i).calcUCB1();
            if (value > bestValue) {
                best = children.get(i);
                bestValue = value;
            }
        }
        return best;
    }

    public Node findHighestAvgScoreChild() {
        Node best = children.getFirst();
        double bestValue = best.calcAvgScore();

        for (int i = 1; i < children.size(); i++) {
            double value = children.get(i).calcAvgScore();
            if (value > bestValue) {
                best = children.get(i);
                bestValue = value;
            }
        }
        return best;
    }
}
