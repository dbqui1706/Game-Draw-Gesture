package fit.nlu.adapter.spiner.model;

public class RoundItem extends BaseSpinnerItem {
    private int round;

    public RoundItem(int round) {
        super(round + " round");
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    @Override
    public boolean isMatchingValue(int value) {
        return round == value;
    }
}
