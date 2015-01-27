package pl.nemolab.sphinxqa.subs;

import java.io.Serializable;

public class TimeCode implements Serializable {

    private String text;
    private int milliSecond;

    public TimeCode(String text, int milliSecond) {
        this.text = text;
        this.milliSecond = milliSecond;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMilliSecond() {
        return milliSecond;
    }

    public void setMilliSecond(int milliSecond) {
        this.milliSecond = milliSecond;
    }
}
