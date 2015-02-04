package pl.nemolab.sphinxqa.model;

import pl.nemolab.sphinxqa.subs.Subtitle;

/**
 * Created by senator on 2015-02-03.
 */
public class Subs {
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
        String pattern = "\\<.*?\\>";
        if (pointerSrc != null) {
            src = pointerSrc.getText().replaceAll(pattern, "");
            start = pointerSrc.getStart().getText().replaceAll(pattern, "");
            stop = pointerSrc.getStop().getText().replaceAll(pattern, "");
            position = pointerSrc.getStartMs();
        }
        if (pointerDst != null) {
            dst = pointerDst.getText().replaceAll(pattern, "");
        }
    }
}
