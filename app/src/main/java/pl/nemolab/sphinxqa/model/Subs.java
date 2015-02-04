package pl.nemolab.sphinxqa.model;

import pl.nemolab.sphinxqa.subs.Subtitle;

/**
 * Created by senator on 2015-02-03.
 */
public class Subs {
    public static final String PATTERN_CLEAN = "\\<.*?\\>";
    public static final String PATTERN_SPLIT =  "/\\r?\\n/g";
    private String start;
    private String stop;
    private String src;
    private String dst;
    private int position;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Subs(Subtitle pointerSrc, Subtitle pointerDst) {
        if (pointerSrc != null) {
            src = cleanText(pointerSrc.getText());
            start = pointerSrc.getStart().getText();
            stop = pointerSrc.getStop().getText();
            position = pointerSrc.getStartMs();
        }
        if (pointerDst != null) {
            dst = cleanText(pointerDst.getText());
        }
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
