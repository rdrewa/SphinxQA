package pl.nemolab.sphinxqa.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import pl.nemolab.sphinxqa.subs.SubtitleOutput;
import pl.nemolab.sphinxqa.model.Card;

public class QATextExporter implements SubtitleOutput {

    @Override
    public boolean export(List<Card> cards, String outputFile) {
        FileOutputStream output;
        OutputStreamWriter writer;
        String text;
        try {
            File file = new File(outputFile);
            file.createNewFile();
            output = new FileOutputStream(file);
            writer = new OutputStreamWriter(output);
            for (Card card : cards) {
                text = prepareText(card);
                writer.write(text);
            }
            writer.close();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String prepareText(Card card) {
        StringBuilder sb = new StringBuilder();
        String eol = "\n";
        sb.append("Q: " + card.getFront() + eol);
        sb.append("A: " + card.getBack() + eol);
        sb.append(eol);
        return sb.toString();
    }
}
