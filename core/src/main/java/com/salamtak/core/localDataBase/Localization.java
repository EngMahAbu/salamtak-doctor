package com.salamtak.core.localDataBase;

import android.content.Context;
import android.content.res.Configuration;
import java.util.Locale;


/**
 * Created by Mohamed Mansour on 10/3/2021.
 */

public class Localization {

    public static final int ARABIC_VALUE = 1;
    public static final int ENGLISH_VALUE = 2;
    private static final String ARABIC_CODE = "ar";
    private static final String ENGLISH_CODE = "en";

    public static void setApplicationLanguage(Context context) {


        if (UserData.getLanguageID() > 0) {
            if (UserData.getLanguageID() == ARABIC_VALUE){
                changeLocale(context, new Locale("ar"));
            }else if (UserData.getLanguageID() == ENGLISH_VALUE){
                changeLocale(context, new Locale("en"));
            }
            else
                return;
        }

    }

    private static void changeLocale(Context context, Locale locale) {
        Configuration conf = context.getResources().getConfiguration();
        conf.locale = locale;
        Locale.setDefault(locale);
        conf.setLayoutDirection(conf.locale);

        context.getResources().updateConfiguration(conf, context.getResources().getDisplayMetrics());

    }

    public static String getLanguageName() {
        if (UserData.getLanguageID() == ARABIC_VALUE)
            return ARABIC_CODE;
        else if (UserData.getLanguageID() == ENGLISH_VALUE)
            return ENGLISH_CODE;
        else
            return "";
    }
}
