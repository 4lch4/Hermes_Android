package com.devinl.hermes.utils;

import android.content.Context;
import android.util.Log;

import com.devinl.hermes.R;
import com.devinl.hermes.models.User;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import static com.devinl.hermes.utils.Constants.CHAR_LIST;

/**
 * Created by Alcha on 3/26/2017.
 * Contains methods for retrieving the various keys needed to run the app. Such as the Twitter Key,
 * Twitter Secret, etc. The Twitter key and secret are both required for the Digits library.
 */

public class KeyUtility {
    /** Basic log tag **/
    private static final String LOG_TAG = "KeyUtility";

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
     * Using the provided integer, generate a token using the <code>CHAR_LIST</code> constant, and
     * return it as a String.
     *
     * @param stringLen int representing length desired
     *
     * @return String
     */
    public static String generateToken(int stringLen) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < stringLen; i++) {
            int num = getRandomNum();
            char ch = CHAR_LIST.charAt(num);

            builder.append(ch);
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

    public static void updateDeviceToken(String token, Context context) {
        Log.d(LOG_TAG, "Updating Firebase device token - " + token);

        PrefManager prefManager = new PrefManager(context);
        DBManager dbManager = new DBManager(context);

        dbManager.updateDeviceToken(token);
        prefManager.setFirebaseToken(token);
    }

    public static void updateUser(User user, Context context) {
        Log.d(LOG_TAG, "Updating user - " + user.getUsername());

        PrefManager prefManager = new PrefManager(context);
        DBManager dbManager = new DBManager(context);

        prefManager.setUser(user);
        dbManager.updateUser(user);
    }
}
