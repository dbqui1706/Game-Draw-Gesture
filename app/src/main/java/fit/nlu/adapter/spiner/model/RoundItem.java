package fit.nlu.adapter.spiner.model;

public class RoundItem extends BaseSpinnerItem {
    private int number;

    public RoundItem(int number) {
        super(number + " round");
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean isMatchingValue(int value) {
        return number == value;
    }
}
