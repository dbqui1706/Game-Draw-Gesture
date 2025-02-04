package fit.nlu.adapter.spiner.model;

public abstract class BaseSpinnerItem {
    private String displayText;  // Text hiển thị trên spinner

    public BaseSpinnerItem(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
    public abstract boolean isMatchingValue(int value);
}