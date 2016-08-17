package com.armi.popularmovies;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Utility class to format movie release dates
 */
public class MovieDateFormatter {

    /**
     * Pattern used for release date
     */
    public static final String RELEASE_DATE_PATTERN = "yyyy-MM-dd";

    /**
     * Private instance
     */
    private static DateFormat formatter;

    /**
     * Getter for singleton of formatter
     *
     * @return instance of formatter
     */
    public static DateFormat getDateFormatter() {
        if (formatter == null) {
            formatter = new SimpleDateFormat(RELEASE_DATE_PATTERN, Locale.getDefault());
        }

        return formatter;
    }
}
