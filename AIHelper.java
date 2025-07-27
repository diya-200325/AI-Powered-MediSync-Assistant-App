package com.example.sympto;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIHelper {

    private static final String TAG = "AIHelper";

    // 🔑 Gemini API key (be careful with exposing it)
    private static final String API_KEY = "AIzaSyB9pLsWN8ev3633TuGzW3Quc_Xu_3geqN4";

    // Gemini model name (you can change the version later if needed)
    private static final String MODEL_NAME = "gemini-1.5-pro-latest";

    // ✅ Clean API URL
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/" +
                    MODEL_NAME + ":generateContent?key=" + API_KEY;

    // Executor for background tasks
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Handler for main thread updates
    private static final Handler handler = new Handler(Looper.getMainLooper());

    // ✅ Callback interface for handling AI responses asynchronously
    public interface AIResponseCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    /**
     * ✅ Public method to fetch AI analysis from Gemini API
     * @param symptoms The user-input symptoms
     * @param callback Callback to handle success/error
     */
    public static void getAIAnalysis(String symptoms, AIResponseCallback callback) {

        executorService.execute(() -> {
            HttpURLConnection connection = null;

            try {
                // ✅ Create URL object (no syntax errors!)
                URL url = new URL(API_URL);

                // ✅ Set up connection
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // ✅ Build JSON request body
                JSONObject requestBody = new JSONObject();

                JSONArray contents = new JSONArray();
                JSONObject content = new JSONObject();
                JSONArray parts = new JSONArray();

                JSONObject textPart = new JSONObject();
                textPart.put("text", symptoms);

                parts.put(textPart);
                content.put("parts", parts);
                contents.put(content);

                requestBody.put("contents", contents);

                Log.d(TAG, "Request JSON: " + requestBody.toString());

                // ✅ Send request to Gemini API
                OutputStream os = connection.getOutputStream();
                os.write(requestBody.toString().getBytes());
                os.flush();
                os.close();

                // ✅ Check response code
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    // ✅ Read and log error message
                    InputStream errorStream = connection.getErrorStream();
                    String errorMessage = convertStreamToString(errorStream);

                    Log.e(TAG, "Error Response: " + errorMessage);

                    handler.post(() -> callback.onError("HTTP Error " + responseCode + ": " + errorMessage));
                    return; // Stop further processing
                }

                // ✅ Read success response
                InputStream inputStream = connection.getInputStream();
                String responseString = convertStreamToString(inputStream);

                Log.d(TAG, "Response JSON: " + responseString);

                // ✅ Parse JSON response from Gemini
                JSONObject jsonResponse = new JSONObject(responseString);
                JSONArray candidates = jsonResponse.getJSONArray("candidates");

                if (candidates.length() > 0) {
                    JSONObject firstCandidate = candidates.getJSONObject(0);
                    JSONObject responseContent = firstCandidate.getJSONObject("content");
                    JSONArray partsArray = responseContent.getJSONArray("parts");

                    if (partsArray.length() > 0) {
                        JSONObject firstPart = partsArray.getJSONObject(0);
                        String responseText = firstPart.getString("text");

                        handler.post(() -> callback.onSuccess(responseText));
                    } else {
                        handler.post(() -> callback.onError("No parts found in the response."));
                    }
                } else {
                    handler.post(() -> callback.onError("No candidates found in the response."));
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception occurred", e);
                handler.post(() -> callback.onError("Exception: " + e.getMessage()));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        });
    }

    /**
     * ✅ Helper method to convert InputStream to String
     */
    private static String convertStreamToString(InputStream stream) {
        Scanner scanner = new Scanner(stream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
