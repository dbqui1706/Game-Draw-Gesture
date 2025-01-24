package fit.nlu.adapter.spiner.model;

public class PersonItem extends BaseSpinnerItem {
    private int number;

    public PersonItem(int number) {
        super(number + " người");
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
