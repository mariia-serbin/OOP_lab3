package com.fit.tracker.util;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.Locale;

public final class LocaleHelper {
    public static final String UKRAINIAN = "uk";
    public static final String ENGLISH = "en";

    private LocaleHelper() {
    }

    public static void applyStartupLocale() {
        if (AppCompatDelegate.getApplicationLocales().isEmpty()) {
            apply(UKRAINIAN);
        }
    }

    public static void apply(String language) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language));
    }

    public static String current() {
        Locale locale = AppCompatDelegate.getApplicationLocales().get(0);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return ENGLISH.equals(locale.getLanguage()) ? ENGLISH : UKRAINIAN;
    }
}
