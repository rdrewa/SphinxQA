package pl.nemolab.sphinxqa.subs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by senator on 2015-01-24.
 */
public class CardCreator {

    public static final int THRESHOLD = 150;

    private List<Subtitle> src;
    private List<Subtitle> dst;
    private int lastIndex = 0, length;

    public CardCreator(List<Subtitle> src, List<Subtitle> dst) {
        this.src = src;
        this.dst = dst;
        length = dst.size();
    }

    public ArrayList<Card> create(
            ArrayList<Integer> marked
    ) {
        ArrayList<Card> subsPairs = new ArrayList<Card>();
        ArrayList<Subtitle> tmp = new ArrayList<>();
        for (int index : marked) {
            Subtitle subtitle = src.get(index);
            if (subtitle != null) {
                tmp.add(subtitle);
            }
        }

        for (Subtitle subtitle : tmp) {
            Subtitle answer = subtitle;
            Subtitle question = matchSubs(answer);
            if (question != null) {
                subsPairs.add(new Card(question, answer));
            }
        }

        return subsPairs;
    }

    private Subtitle matchSubs(Subtitle subtitle) {
        int i;
        int currentPos = subtitle.getStartMs();
        for (i = lastIndex; i < length; i++) {
            Subtitle item = dst.get(i);
            if (isIn(currentPos, item)) {
                lastIndex = i;
                return item;
            }

        }
        return null;
    }

    private boolean isIn(int currentPos, Subtitle subtitle) {
        return currentPos >= (subtitle.getStartMs() - THRESHOLD)
                && currentPos <= (subtitle.getStopMs() + THRESHOLD);
    }
}
