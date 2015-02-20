package pl.nemolab.sphinxqa.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import pl.nemolab.sphinxqa.model.Card;
import pl.nemolab.sphinxqa.subs.SubtitleOutput;

/**
 * Created by senator on 2015-02-20.
 */
public class SeparatedTextExporter implements SubtitleOutput {

    private String separator;
    private String extension;

    public SeparatedTextExporter(String separator, String extension) {
        this.separator = separator;
        this.extension = extension;
    }

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

    @Override
    public String getExtension() {
        return extension;
    }

    private String prepareText(Card card) {
        StringBuilder sb = new StringBuilder();
        String eol = "\n", br = "<br />";
        sb.append("\"" + card.getFront().replace(eol, br) + "\"");
        sb.append(separator);
        sb.append("\"" + card.getBack().replace(eol, br) + "\"");
        sb.append(eol);
        return sb.toString();
    }
}
