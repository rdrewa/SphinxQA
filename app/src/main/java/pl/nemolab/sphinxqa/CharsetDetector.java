package pl.nemolab.sphinxqa;

import java.util.HashMap;
import java.util.Map;

import static pl.nemolab.sphinxqa.Config.ALTERNATIVE_CHARSET;

public class CharsetDetector {

    private Map<String, String> map;

    public CharsetDetector() {
        map = new HashMap();
        initMap();
    }

    private void initMap() {
        map.put("cs", "windows-1250");
        map.put("hu", "windows-1250");
        map.put("pl", "windows-1250");
        map.put("ro", "windows-1250");
        map.put("ru", "windows-1251");
        map.put("da", "windows-1252");
        map.put("nl", "windows-1252");
        map.put("en", "windows-1252");
        map.put("fr", "windows-1252");
        map.put("it", "windows-1252");
        map.put("no", "windows-1252");
        map.put("pt", "windows-1252");
        map.put("sv", "windows-1252");
        map.put("el", "windows-1253");
        map.put("tr", "windows-1254");
        map.put("iw", "windows-1255");
        map.put("ar", "windows-1256");
    }

    public String retrieve(String language) {
        if (map.containsKey(language)) {
            return map.get(language);
        }
        return ALTERNATIVE_CHARSET;
    }
}
