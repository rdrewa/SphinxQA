package pl.nemolab.sphinxqa;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Config {
    public static final String KEY_MOVIE_MIN_SIZE = "prefMovieMinSize";
    public static final String KEY_MOVIE_MIN_DURATION = "prefMovieMinDuration";
    public static final String KEY_SUBTITLES_SHOWING = "prefSubtitlesShowing";
    public static final String KEY_STORAGE_TYPE = "prefStorageType";
    public static final String KEY_STORAGE_FOLDER = "prefStorageFolder";
    public static final String KEY_STORAGE_USER_FOLDER = "prefStorageUserFolder";
    public static final String KEY_CHARSET = "prefCharset";
    public static final String DEFAULT_MOVIE_MIN_SIZE = "100";
    public static final String DEFAULT_MOVIE_MIN_DURATION = "20";
    public static final String DEFAULT_SUBTITLES_SHOWING = "never";
    public static final String DEFAULT_STORAGE_TYPE = "APP_FOLDER";
    public static final String DEFAULT_STORAGE_FOLDER = "/sdcard/SphinxQA/";
    public static final String DEFAULT_CHARSET = "Auto Detect";
    public static final String ALTERNATIVE_CHARSET = "UTF-8";

    private Context context;
    private SharedPreferences settings;

    public Config(Context context) {
        this.context = context;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String retrieveMinSize() {
        int minSize = 1000000 * Integer.parseInt(settings.getString(
            KEY_MOVIE_MIN_SIZE,
            DEFAULT_MOVIE_MIN_SIZE
        ));
        return String.valueOf(minSize);
    }

    public String retrieveMinDuration() {
        String duration = settings.getString(
            KEY_MOVIE_MIN_DURATION,
            DEFAULT_MOVIE_MIN_DURATION
        );
        return duration;
    }

    public String retrieveCharset() {
        String charset = settings.getString(
            KEY_CHARSET,
            DEFAULT_CHARSET
        );
        if (charset.equals(DEFAULT_CHARSET)) {
            String language = context.getResources().getConfiguration().locale.getLanguage();
            CharsetDetector detector = new CharsetDetector();
            charset = detector.retrieve(language);
        }
        return charset;
    }

    public String retrieveSubtitlesShowing() {
        String subtitlesShowing = settings.getString(
                KEY_SUBTITLES_SHOWING,
                DEFAULT_SUBTITLES_SHOWING
        );
        return subtitlesShowing;
    }

    public String retrieveStorageType() {
        String storageType = settings.getString(
                KEY_STORAGE_TYPE,
                DEFAULT_STORAGE_TYPE
        );
        return storageType;
    }

    public String retrieveStorageFolder() {
        String storageFolder = settings.getString(
                KEY_STORAGE_FOLDER,
                DEFAULT_STORAGE_FOLDER
        );
        return storageFolder;
    }
}
