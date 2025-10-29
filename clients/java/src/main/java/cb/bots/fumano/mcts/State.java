package cb.bots.fumano.mcts;

import java.util.List;

public interface State {

    double getScore();
    boolean isMaximising();
    boolean isTerminal();
    void simulate(int action);
    State copy();
    void findActions(List<Integer> result);

}
