package pl.nemolab.sphinxqa.subs;

import java.io.Serializable;

public class Subtitle implements Serializable {
    private int nr;
    private String text;
    private TimeCode start;
    private TimeCode stop;

    public Subtitle(int nr, String text, TimeCode start, TimeCode stop) {
        this.nr = nr;
        this.text = text;
        this.start = start;
        this.stop = stop;
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TimeCode getStart() {
        return start;
    }

    public void setStart(TimeCode start) {
        this.start = start;
    }

    public TimeCode getStop() {
        return stop;
    }

    public void setStop(TimeCode stop) {
        this.stop = stop;
    }

    public int getStartMs() {
        return start.getMiliSecond();
    }

    public int getStopMs() {
        return stop.getMiliSecond();
    }
}
