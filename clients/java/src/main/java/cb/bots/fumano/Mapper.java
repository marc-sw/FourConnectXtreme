package cb.bots.fumano;

import cb.PlayState;

import java.util.Map;

public class Mapper {

    public static Connect4Xtreme loadConnect4Xtreme(PlayState playState) {
        Connect4Xtreme connect4Xtreme = new Connect4Xtreme();
        for (int y = 0; y < Connect4Xtreme.ROWS; y++) {
            for (int x = 0; x < Connect4Xtreme.COLUMNS; x++) {
                int value =  playState.getBoard().get(y).get(x);
                if (value == 1) {
                    value = Connect4Xtreme.BLUE;
                } else if (value == 2) {
                    value = Connect4Xtreme.RED;
                }
                connect4Xtreme.set(y, x, (byte) value);
            }
        }
        connect4Xtreme.setRound(playState.getRound() - 1);
        if (playState.getBombs().isEmpty()) {
            connect4Xtreme.setBomb(null);
        } else {
            Map<String, Integer> bombMap = playState.getBombs().getFirst();
            Bomb bomb = new Bomb(bombMap.get("row"), bombMap.get("col"), bombMap.get("explode_in_round") - 1);
            connect4Xtreme.setBomb(bomb);
        }
        connect4Xtreme.setState(connect4Xtreme.calcState());
        return connect4Xtreme;
    }
}
