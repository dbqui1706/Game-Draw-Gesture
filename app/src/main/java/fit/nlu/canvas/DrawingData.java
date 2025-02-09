package fit.nlu.canvas;

import java.io.Serializable;

import lombok.Getter;


public class DrawingData implements Serializable {
    public static enum ActionType {
        START, MOVE, UNDO, UP, CLEAR
    }

    private float x;
    private float y;
    private ActionType action; // "start", "move", "up"
    private int color;
    private float strokeWidth;

    public DrawingData(float x, float y, ActionType action, int color, float strokeWidth) {
        this.x = x;
        this.y = y;
        this.action = action;
        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public ActionType getAction() {
        return action;
    }

    public int getColor() {
        return color;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }
}
