# Hermes
An Android app that plays the middle man between Hermes and a users text messages. When the phone
receives a text message, it's forwarded to Hermes and then sent to the correct user. If the user
sends a message to Hermes, it's sent to this app and then to the correct user.

## TODO
- Receive messages from Firebase instance
- Send messages to correct recipient based on Firebase data

## Dependencies (as of 3/27/17)
- com.github.rey5137:material:1.2.4
- com.android.support:appcompat-v7:25.3.0
- com.joanzapata.iconify:android-iconify-material:2.2.2
- com.jakewharton:butterknife:8.5.1
- com.android.support:cardview-v7:25.3.0
- com.android.support:design:25.3.0
- com.google.firebase:firebase-database:10.2.0
- com.google.firebase:firebase-auth:10.0.1
- com.android.support.constraint:constraint-layout:1.0.2
- com.digits.sdk.android:digits:2.0.6@aar