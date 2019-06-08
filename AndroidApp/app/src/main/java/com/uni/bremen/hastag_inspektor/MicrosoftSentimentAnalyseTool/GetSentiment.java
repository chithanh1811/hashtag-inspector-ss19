package com.uni.bremen.hastag_inspektor.MicrosoftSentimentAnalyseTool;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class GetSentiment {
    private String accessKey;
    private String host;
    private String path;

    public GetSentiment(String accessKey, String host, String path) {
        this.accessKey = accessKey;
        this.host = host;
        this.path = path;
    }

    public String getTheSentiment(Documents responseFromMicrosoftDocuments) throws Exception {
        String text = new Gson().toJson(responseFromMicrosoftDocuments);
        byte[] encoded_text = text.getBytes(StandardCharsets.UTF_8);
        URL url = new URL(host + path);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/json");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", accessKey);
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(encoded_text, 0, encoded_text.length);
        wr.flush();
        wr.close();

        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return response.toString();
    }

    public String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(json_text).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

}


