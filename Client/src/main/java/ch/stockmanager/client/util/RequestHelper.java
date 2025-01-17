package ch.stockmanager.client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestHelper {

    /**
     *
     * @param stringUrl the url of the api method
     * @return the server answer's as a String
     * @throws RuntimeException if the answer code isn't the success code
     */
    public static HttpURLConnection createConnexion(String stringUrl, String method) throws RuntimeException, IOException, URISyntaxException {
        URI uri = new URI(stringUrl);
        URL url = uri.toURL();
        HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
        connexion.setRequestMethod(method);
        return connexion;
    }

    public static HttpURLConnection sendRequest(HttpURLConnection connexion, int successCode) throws RuntimeException {
        try {
            connexion.connect();
            int responseCode = connexion.getResponseCode();
            if (responseCode != successCode) {
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to connect to the server: " + e.getMessage(), e);
        }
        return connexion;
    }

    public static void loadJson(HttpURLConnection connexion, Object o) throws JsonProcessingException {
        connexion.setRequestProperty("Content-Type", "application/json");
        connexion.setDoOutput(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInputString = objectMapper.writeValueAsString(o);

        try (OutputStream os = connexion.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getAnswer(HttpURLConnection connexion) throws IOException {
        int responseCode = connexion.getResponseCode();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connexion.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * parse a server answer body in json to a class
     * @param json the json to parse
     * @return a class corresponding to the json
     * @param <T> the class to which we parse the input
     * @throws JsonProcessingException if the json doesn't match the class or is invalid
     */
    public static <T> T parse(String json, TypeReference<T> typeReference) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, typeReference);
    }
}
