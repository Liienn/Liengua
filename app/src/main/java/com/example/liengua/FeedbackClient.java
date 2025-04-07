package com.example.liengua;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedbackClient {

    private static final String TAG = "FeedbackClient";
    private static final String WEB_APP_URL = "https://script.google.com/macros/s/AKfycbyWttUVz0B5pm-iwGkOFZ7QMTZWgKkp8iuQBRuHv5WIFx6RBhJMx54eGBnVCipNLP3THw/exec";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();

    public static void sendFeedback(int id, String originalPhrase, String feedback, String user) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("original_phrase", originalPhrase);
            json.put("feedback", feedback);
            json.put("user", user);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(WEB_APP_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Feedback send failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Feedback sent successfully!");
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
