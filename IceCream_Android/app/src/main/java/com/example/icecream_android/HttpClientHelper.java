package com.example.icecream_android;

import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

public class HttpClientHelper {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public interface ResponseCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }

    public void sendPostRequest(String url, JSONObject jsonBody, ResponseCallback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onFailure("Помилка: " + response.code());
                }
            }
        });
    }
}
