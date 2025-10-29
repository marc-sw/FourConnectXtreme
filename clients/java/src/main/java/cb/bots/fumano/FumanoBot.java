package cb.bots.fumano;

import cb.PlayState;
import cb.bots.BotAi;
import cb.bots.fumano.mcts.MonteCarloTreeSearch;

public class FumanoBot implements BotAi {

    private final MonteCarloTreeSearch mcts;
    private long calcTime;

    public FumanoBot() {
        this.mcts = new MonteCarloTreeSearch();
        this.calcTime = 450L;
    }


    @Override
    public int play(PlayState playState) {
        int move = mcts.findBestAction(Mapper.loadConnect4Xtreme(playState), calcTime);
        calcTime = 650L;
        return move;
    }

    @Override
    public String getName() {
        return "MCTSxJava";
    }
}
