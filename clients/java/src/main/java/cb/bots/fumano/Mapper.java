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
         if (!playState.getBombs().isEmpty()) {
            Map<String, Integer> bombMap = playState.getBombs().getFirst();
            int row = bombMap.get("row");
            int column = bombMap.get("col");
            int explodeRound = bombMap.get("explode_in_round") - 1;
            connect4Xtreme.set(row, column, (byte) (explodeRound - connect4Xtreme.getRound()));
        }
        connect4Xtreme.setState(connect4Xtreme.calcState());
        return connect4Xtreme;
    }
}
