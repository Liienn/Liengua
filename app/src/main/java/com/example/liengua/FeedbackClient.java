package com.example.liengua;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedbackClient {

    private static final String TAG = "FeedbackClient";
    private static final String WEB_APP_URL = "https://script.google.com/macros/s/AKfycbxSfRrwg54dPyGchoJ7RVEFllu49qVnKJH5C7sZzFN-u8c89q56Rlw04rLwZwAxV1RQvw/exec";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    public static void sendFeedback(int id, String originalPhrase, Boolean delete, String feedback, String user) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("original_phrase", originalPhrase);
            json.put("suggestDelete", delete);
            json.put("feedback", feedback);
            json.put("user", user);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Log.d("JSON", json.toString());
            Request request = new Request.Builder()
                    .url(WEB_APP_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Feedback send failed: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Log.d(TAG, "Feedback sent successfully! " + response.body().string());

                    } else {
                        Log.e(TAG, "Feedback send failed: " + response.message());
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error creating feedback JSON: " + e.getMessage());
        }
    }
}
