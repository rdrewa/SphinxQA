package pl.nemolab.sphinxqa;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.util.Locale;

public class Config {
    public static final String KEY_MOVIE_MIN_SIZE = "prefMovieMinSize";
    public static final String KEY_MOVIE_MIN_DURATION = "prefMovieMinDuration";
    public static final String KEY_PLAYER_SHOW_SUBTITLES = "prefPlayerShowSubtitles";
    public static final String KEY_LIST_SHOW_SUBTITLES = "prefListShowSubtitles";
    public static final String KEY_STORAGE_TYPE = "prefStorageType";
    public static final String KEY_STORAGE_FOLDER = "prefStorageFolder";
    public static final String KEY_STORAGE_USER_FOLDER = "prefStorageUserFolder";
    public static final String KEY_CHARSET = "prefCharset";
    public static final String KEY_LANG = "prefLang";
    public static final String KEY_USER_MAIL = "prefUserMail";
    public static final int DEFAULT_MOVIE_MIN_SIZE = 100;
    public static final int DEFAULT_MOVIE_MIN_DURATION = 20;
    public static final String DEFAULT_PLAYER_SHOW_SUBTITLES = "never";
    public static final boolean DEFAULT_LIST_SHOW_SUBTITLES = false;
    public static final String DEFAULT_STORAGE_TYPE = "APP_FOLDER";
    public static final String DEFAULT_STORAGE_FOLDER = "/SphinxQA";
    public static final String DEFAULT_CHARSET = "Auto Detect";
    public static final String ALTERNATIVE_CHARSET = "UTF-8";
    public static final String PLAYER_SHOW_SUBTITLES_NEVER = "never";
    public static final String PLAYER_SHOW_SUBTITLES_MARKED = "marked";
    public static final String PLAYER_SHOW_SUBTITLES_ALWAYS = "always";
    public static final String STORAGE_TYPE_APP_FOLDER = "APP_FOLDER";
    public static final String STORAGE_TYPE_USER_FOLDER = "USER_FOLDER";
    public static final String STORAGE_TYPE_MOVIE_FOLDER = "MOVIE_FOLDER";

    private Context context;
    private SharedPreferences settings;

    public Config(Context context) {
        this.context = context;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String retrieveMinSize() {
        int intSize = getMinSize();
        int minSize = 1000000 * intSize;
        return String.valueOf(minSize);
    }

    public int getMinSize() {
        return settings.getInt(KEY_MOVIE_MIN_SIZE, DEFAULT_MOVIE_MIN_SIZE);
    }

    public String retrieveMinDuration() {
        int intDuration = getMinDuration();
        int minDuration = 60000 * intDuration;
        return String.valueOf(minDuration);
    }

    public int getMinDuration() {
        return settings.getInt(KEY_MOVIE_MIN_DURATION, DEFAULT_MOVIE_MIN_DURATION);
    }

    public String retrieveCharset() {
        String charset = settings.getString(
            KEY_CHARSET,
            DEFAULT_CHARSET
        );
        if (charset.equals(DEFAULT_CHARSET)) {
            String language = getLang();
            CharsetDetector detector = new CharsetDetector();
            charset = detector.retrieve(language);
        }
        return charset;
    }

    private String getLang() {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    public String retrievePlayerShowSubtitles() {
        String showSubtitles = settings.getString(
                KEY_PLAYER_SHOW_SUBTITLES,
                DEFAULT_PLAYER_SHOW_SUBTITLES
        );
        return showSubtitles;
    }

    public boolean retrieveListShowSubtitles() {
        boolean showSubtitles = settings.getBoolean(
                KEY_LIST_SHOW_SUBTITLES,
                DEFAULT_LIST_SHOW_SUBTITLES
        );
        return showSubtitles;
    }

    public String retrieveStorageType() {
        String storageType = settings.getString(
                KEY_STORAGE_TYPE,
                DEFAULT_STORAGE_TYPE
        );
        return storageType;
    }

    public String retrieveStorageFolder() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String storageFolder = settings.getString(
                KEY_STORAGE_FOLDER,
                root + DEFAULT_STORAGE_FOLDER
        );
        return storageFolder;
    }

    public String retrieveUserMail() {
        return settings.getString(KEY_USER_MAIL, "");
    }
}
