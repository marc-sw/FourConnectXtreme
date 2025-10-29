package cb.bots.fumano;

public record Bomb(int row, int column, int explodingRound) {

    public static Bomb copy(Bomb original) {
        if (original == null) {
            return null;
        }
        return new Bomb(original.row, original.column, original.explodingRound);
    }
}
