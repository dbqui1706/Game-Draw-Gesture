package fit.nlu.adapter.spiner.model;

public class TimeItem extends BaseSpinnerItem {
    private int seconds;

    public TimeItem(int seconds) {
        super(seconds + " giây");
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }
}