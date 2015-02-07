package pl.nemolab.sphinxqa;

import java.util.List;

import pl.nemolab.sphinxqa.model.Card;

public interface SubtitleOutput {
    boolean export(List<Card> cards, String outputFile);
}
