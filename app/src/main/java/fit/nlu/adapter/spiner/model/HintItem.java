package fit.nlu.adapter.spiner.model;

public class HintItem extends BaseSpinnerItem {
    private int count;

    public HintItem(int count) {
        super(count + " gợi ý");
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean isMatchingValue(int value) {
        return count == value;
    }
}
