package pl.nemolab.sphinxqa.subs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import pl.nemolab.sphinxqa.SubtitleInput;

public class SrtParser implements SubtitleInput {

    public static final String EOL = "\n";

    private Charset charset;

    public SrtParser(String charsetName) {
        charset = Charset.forName(charsetName);
    }

    @Override
    public List<Subtitle> parseFile(String filePath) throws IOException, ParseException {
        InputStream inputStream = new FileInputStream(filePath);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        List<Subtitle> subtitleList = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                line = line.replaceAll("\\s+", "");
                Number number = NumberFormat.getInstance().parse(line);
                int num = number.intValue();
                line = reader.readLine();
                TimeCode start = prepareStart(line);
                TimeCode stop = prepareStop(line);
                String text = prepareText(reader);
                Subtitle subtitle = new Subtitle(num, text, start, stop);
                subtitleList.add(subtitle);
                line = reader.readLine();
            }
        }
        return subtitleList;
    }

    private String readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        byte[]  array = line.getBytes(Charset.forName("Cp1252"));
        return new String(array, Charset.forName("UTF-8"));
    }

    private String prepareText(BufferedReader reader) throws IOException {
        StringBuffer sb = new StringBuffer();
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            sb.append(line + EOL);
            line = reader.readLine();
        }
        int eol = sb.lastIndexOf(EOL);
        return sb.substring(0, eol);
    }

    private TimeCode prepareStart(String line) {
        String string = line.substring(0,12);
        int millisecond = parseTime(string);
        return new TimeCode(string.substring(0, 8), millisecond);
    }

    private TimeCode prepareStop(String line) {
        String string = line.substring(line.length() - 12, line.length());
        int millisecond = parseTime(string);
        return new TimeCode(string.substring(0, 8), millisecond);
    }

    private int parseTime(String time) {
        int h, m, s, ms;
        h = Integer.parseInt(time.substring(0, 2));
        m = Integer.parseInt(time.substring(3, 5));
        s = Integer.parseInt(time.substring(6, 8));
        ms = Integer.parseInt(time.substring(9, 12));
        return ms + (s * 1000) + (m * 60000) + (h * 360000);
    }
}
