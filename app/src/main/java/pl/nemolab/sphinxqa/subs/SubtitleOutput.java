package pl.nemolab.sphinxqa.subs;

import java.util.List;

import pl.nemolab.sphinxqa.model.Card;

public interface SubtitleOutput {
    boolean export(List<Card> cards, String outputFile);

    String getExtension();
}