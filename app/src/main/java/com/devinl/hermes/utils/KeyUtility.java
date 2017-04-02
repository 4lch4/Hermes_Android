package com.devinl.hermes.utils;

import android.content.Context;
import android.util.Log;

import com.devinl.hermes.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import static com.devinl.hermes.utils.Constants.CHAR_LIST;

/**
 * Created by Alcha on 3/26/2017.
 * Contains methods for retrieving the various keys needed to run the app. Such as the Twitter Key,
 * Twitter Secret, etc.
 */

public class KeyUtility {
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

    /**
     * Generate a {@link String} that represents a user token and return it. The token is 10
     * characters long and consists of the characters <code>a-z, A-Z, 0-9, /, and .</code>
     *
     * @return String
     */
    public static String generateUserToken() {
        StringBuilder builder = new StringBuilder();
        final int stringLen = 10;

        for (int i = 0; i < stringLen; i++) {
            int num = getRandomNum();
            char ch = CHAR_LIST.charAt(num);

            // Test to see if the char is a forward or backward slash. If so, don't add it and
            // remove 1 from i to ensure at least 10 characters are added to the string.
            if (ch != '/' && ch != '\\')
                builder.append(ch);
            else
                i--;
        }

        return builder.toString();
    }

    /**
     * Generate a random number and return a character based on that number using the Alphabet  in
     * {@link com.devinl.hermes.utils.Constants}.
     *
     * @return int value for <code>a-z, A-Z, 0-9, /, and .</code>
     */
    private static int getRandomNum() {
        int randomInt;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }
}
