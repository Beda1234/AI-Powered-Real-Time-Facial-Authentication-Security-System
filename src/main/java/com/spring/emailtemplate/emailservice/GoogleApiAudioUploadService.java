package com.spring.emailtemplate.emailservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GoogleApiAudioUploadService {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com";
    private static final String API_KEY = "AIzaSyAlrFrUJw5vtagw90cnb2iRs8jtURp843o";

    public String processAudioUpload(MultipartFile file, String displayName) throws IOException {
        String mimeType = file.getContentType();
        long numBytes = file.getSize();

        // Step 1: Initialize upload
        String uploadUrl = initializeUpload(BASE_URL, API_KEY, mimeType, numBytes, displayName);

        // Step 2: Upload the file
        String fileUri = uploadFile(uploadUrl, file);

        // Step 3: Generate content
        return generateContent(API_KEY, fileUri, mimeType);
    }

    private String initializeUpload(String baseUrl, String apiKey, String mimeType, long numBytes, String displayName) throws IOException {
        URL url = new URL(baseUrl + "/upload/v1beta/files?key=" + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("X-Goog-Upload-Protocol", "resumable");
        conn.setRequestProperty("X-Goog-Upload-Command", "start");
        conn.setRequestProperty("X-Goog-Upload-Header-Content-Length", String.valueOf(numBytes));
        conn.setRequestProperty("X-Goog-Upload-Header-Content-Type", mimeType);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String metadata = String.format("{\"file\": {\"display_name\": \"%s\"}}", displayName);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(metadata.getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to initialize upload. HTTP response code: " + responseCode);
        }

        String uploadUrl = conn.getHeaderField("X-Goog-Upload-URL");
        if (uploadUrl == null || uploadUrl.isEmpty()) {
            throw new IOException("Upload URL not found in response headers.");
        }

        return uploadUrl.trim();
    }

    private String uploadFile(String uploadUrl, MultipartFile file) throws IOException {
        URL url = new URL(uploadUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Length", String.valueOf(file.getSize()));
        conn.setRequestProperty("X-Goog-Upload-Offset", "0");
        conn.setRequestProperty("X-Goog-Upload-Command", "upload, finalize");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream(); InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getJSONObject("file").getString("uri");
    }

    private String generateContent(String apiKey, String fileUri, String mimeType) throws IOException {
        URL url = new URL(BASE_URL + "/v1beta/models/gemini-1.5-flash-001:generateContent?key=" + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String payload = String.format(
            "{\"contents\": [{\"parts\":[{\"text\": \"Describe this audio clip\"}, {\"file_data\":{\"mime_type\": \"%s\", \"file_uri\": \"%s\"}}]}]}",
            mimeType, fileUri);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        return response.toString();
    }
}

