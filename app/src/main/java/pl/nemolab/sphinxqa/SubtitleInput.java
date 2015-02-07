package pl.nemolab.sphinxqa;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import pl.nemolab.sphinxqa.subs.Subtitle;

public interface SubtitleInput {
    List<Subtitle> parseFile(String filePath) throws IOException, ParseException;
}
