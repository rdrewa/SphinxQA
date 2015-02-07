package pl.nemolab.sphinxqa.model;

import pl.nemolab.sphinxqa.subs.Subtitle;

/**
 * Created by senator on 2015-01-24.
 */
public class Card {

    public static final String PATTERN_CLEAN = "\\<.*?\\>";
    public static final String PATTERN_SPLIT =  "/\\r?\\n/g";

    private int nr;
    private String front;
    private String back;
    private String start;
    private String stop;
    private int startMs;
    private int stopMs;
    private Subtitle pointerFront;
    private Subtitle pointerBack;
    private boolean export = true;

    public Card(Subtitle pointerFront, Subtitle pointerBack) {
        this.pointerFront = pointerFront;
        this.pointerBack = pointerBack;
        nr = pointerBack.getNr();
        front = pointerFront.getText();
        back = pointerBack.getText();
        front = cleanText(pointerFront.getText());
        back = cleanText(pointerBack.getText());
        start = pointerBack.getStart().getText();
        stop = pointerBack.getStart().getText();
        startMs = pointerBack.getStartMs();
        stopMs = pointerBack.getStopMs();
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public int getStartMs() {
        return startMs;
    }

    public void setStartMs(int startMs) {
        this.startMs = startMs;
    }

    public int getStopMs() {
        return stopMs;
    }

    public void setStopMs(int stopMs) {
        this.stopMs = stopMs;
    }

    public Subtitle getPointerFront() {
        return pointerFront;
    }

    public void setPointerFront(Subtitle pointerFront) {
        this.pointerFront = pointerFront;
    }

    public Subtitle getPointerBack() {
        return pointerBack;
    }

    public void setPointerBack(Subtitle pointerBack) {
        this.pointerBack = pointerBack;
    }

    public boolean isExport() {
        return export;
    }

    public void setExport(boolean export) {
        this.export = export;
    }

    private String cleanText(String input) {
        String[] lines = input.split(PATTERN_SPLIT);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line.replaceAll(PATTERN_CLEAN, ""));
        }
        return sb.toString();
    }
}
