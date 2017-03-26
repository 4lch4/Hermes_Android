package com.devinl.hermes.utils;

import android.content.Context;
import android.util.Log;

import com.devinl.hermes.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Alcha on 3/26/2017.
 * Contains methods for retrieving the various keys needed to run the app. Such as the Twitter Key,
 * Twitter Secret, etc.
 */

public class KeyRetriever {
    public static String getTwitterKey(Context context) {
        StringBuilder results = new StringBuilder();
        final String LOG_TAG = "getTwitterKey()";
        String line;

        try (InputStream inputStream = context.getResources().openRawResource(R.raw.twitter_key);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            while ((line = reader.readLine()) != null) {
                results.append(line);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting Twitter key - " + e.getLocalizedMessage());
        }

        return results.toString();
    }

    public static String getTwitterSecret(Context context) {
        StringBuilder results = new StringBuilder();
        final String LOG_TAG = "getTwitterSecret()";
        String line;

        try (InputStream inputStream = context.getResources().openRawResource(R.raw.twitter_secret);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            while ((line = reader.readLine()) != null) {
                results.append(line);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting Twitter secret - " + e.getLocalizedMessage());
        }

        return results.toString();
    }
}
